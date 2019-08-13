package com.example.shortURL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

@TestInstance(Lifecycle.PER_CLASS)
public class URLShortenerBeanUnitTest {
	
	private URLShortenerBean urlShortenerBean = new URLShortenerBean();

	@BeforeAll
	public void setup()
	{
		URLEntityRepository mockRepository = Mockito.mock(URLEntityRepository.class);
		urlShortenerBean.setUrlEntityRepository(mockRepository);
	}
	
	@Test
	public void testShorten_whenGivenURLWithoutCustomAlias_thenReturnShortenedURL() throws NoSuchAlgorithmException
	{
		String expected = "http://shortener/u/4fe98b";
		Map<String, Object> actual = urlShortenerBean.shorten("http://bemobi.com", null);
		assertTrue(expected.equals(actual.get("URL")));
		
		expected = "http://shortener/u/264472";
		actual = urlShortenerBean.shorten("http://portal.ufrj.br", null);
		assertTrue(expected.equals(actual.get("URL")));
		
		expected = "http://shortener/u/1c5ec8";
		actual = urlShortenerBean.shorten("http://google.com", null);
		assertTrue(expected.equals(actual.get("URL")));
		
		expected = "http://shortener/u/1ef084";
		actual = urlShortenerBean.shorten("http://facebook.com", null);
		assertTrue(expected.equals(actual.get("URL")));
	}

	@Test
	public void testShorten_whenGivenURLwithCustomAlias_thenReturnShortenedURLWithCustomAlias() throws NoSuchAlgorithmException
	{
		String expected = "http://shortener/u/bemobi";
		Map<String, Object> actual = urlShortenerBean.shorten("http://bemobi.com", "bemobi");
		assertTrue(expected.equals(actual.get("URL")));
	}
	
	@Test
	public void testShorten_whenGivenURLContainingSpecialCharacters_thenReturnShortenedURL() throws NoSuchAlgorithmException
	{
		String expected = "http://shortener/u/test";
		Map<String, Object> actual = urlShortenerBean.shorten("http://?&!@/\\|:;.com", "test");
		assertTrue(expected.equals(actual.get("URL")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testShorten_whenGivenURL_thenResultShouldIncludeTimeTaken() throws NoSuchAlgorithmException
	{
		Map<String, Object> actual = urlShortenerBean.shorten("http://bemobi.com", null);
		Map<String, Object> statistics = (Map<String, Object>) actual.get("STATISTICS");
		
		assertNotNull(statistics.get("TIME_TAKEN"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testShorten_whenURLRequestedOnce_thenResultShouldIncludeTimesURLWasRequested() throws NoSuchAlgorithmException
	{
		Map<String, Object> actual = urlShortenerBean.shorten("http://bemobi.com", null);
		Map<String, Object> statistics = (Map<String, Object>) actual.get("STATISTICS");
		
		assertEquals(Long.valueOf(1), statistics.get("TIMES_REQUESTED"));
	}	
}
