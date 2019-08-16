package com.example.shortURL.shorten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import com.example.shortURL.model.URLEntityRepository;

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
	public void testShorten_whenGivenURLWithoutCustomAlias_thenReturnShortenedURL() throws NoSuchAlgorithmException, JSONException
	{
		String expected = "http://shortener/u/4fe98b";

		JSONObject json = new JSONObject();
		json.put("url", "http://bemobi.com");
		
		Map<String, Object> actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));
		
		json.put("url", "http://portal.ufrj.br");
		expected = "http://shortener/u/264472";
		actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));
		
		json.put("url", "http://google.com");
		expected = "http://shortener/u/1c5ec8";
		actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));

		json.put("url", "http://facebook.com");
		expected = "http://shortener/u/1ef084";
		actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));
	}

	@Test
	public void testShorten_whenGivenURLwithCustomAlias_thenReturnShortenedURLWithCustomAlias() throws NoSuchAlgorithmException, JSONException
	{
		JSONObject json = new JSONObject();
		json.put("url", "http://bemobi.com");
		json.put("custom_alias", "bemobi");

		String expected = "http://shortener/u/bemobi";
		Map<String, Object> actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));
	}
	
	@Test
	public void testShorten_whenGivenURLContainingSpecialCharacters_thenReturnShortenedURL() throws NoSuchAlgorithmException, JSONException
	{
		JSONObject json = new JSONObject();
		json.put("url", "http://?&!@/\\|:;.com");
		json.put("custom_alias", "test");

		String expected = "http://shortener/u/test";
		Map<String, Object> actual = urlShortenerBean.shorten(json);
		assertTrue(expected.equals(actual.get("URL")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testShorten_whenGivenURL_thenResultShouldIncludeTimeTaken() throws NoSuchAlgorithmException, JSONException
	{
		JSONObject json = new JSONObject();
		json.put("url", "http://bemobi.com");
		
		Map<String, Object> actual = urlShortenerBean.shorten(json);
		Map<String, Object> statistics = (Map<String, Object>) actual.get("STATISTICS");
		
		assertNotNull(statistics.get("TIME_TAKEN"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testShorten_whenURLRequestedOnce_thenResultShouldIncludeTimesURLWasRequested() throws NoSuchAlgorithmException, JSONException
	{
		JSONObject json = new JSONObject();
		json.put("url", "http://bemobi.com");
		
		Map<String, Object> actual = urlShortenerBean.shorten(json);
		Map<String, Object> statistics = (Map<String, Object>) actual.get("STATISTICS");
		
		assertEquals(Long.valueOf(1), statistics.get("TIMES_REQUESTED"));
	}	

	@Test
	public void testValidateShortenRequestBody_whenRequestBodyWithoutURL_thenShouldReturnInvalid() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("URL", "");
		
		boolean actual = urlShortenerBean.validateShortenRequestURL(json);
		assertEquals(false, actual);
	}

	@Test
	public void testValidateShortenRequestBody_whenRequestBodyWithInvalidURL_thenShouldReturnInvalid() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("URL", "htph://wrongurl.000");
		
		boolean actual = urlShortenerBean.validateShortenRequestURL(json);
		assertEquals(false, actual);
	}

	@Test
	public void testValidateShortenRequestBody_whenRequestBodyWithValidURL_thenShouldReturnValid() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("url", "http://test.com");
		
		boolean actual = urlShortenerBean.validateShortenRequestURL(json);
		assertEquals(true, actual);
	}
	
	@Test
	public void testExtractJSONFromBody_whenNullBodyJSON_thenShouldReturnJSONWithKeysButEmptyValues() throws JSONException
	{
		JSONObject actual = urlShortenerBean.extractJSONFromBody(null);
		
		try {
			assertTrue(actual.get("URL").toString().isEmpty());
			assertTrue(actual.get("CUSTOM_ALIAS").toString().isEmpty());
		} catch (JSONException e) {
			fail();
		}
	}

	@Test
	public void testExtractJSONFromBody_whenBodyJSONWithURLWithNullAlias_thenShouldReturnJSONWithURLButEmptyAlias() throws JSONException
	{
		JSONObject body = new JSONObject();
		body.put("url", "https://test.com");
		body.put("custom_alias", null);
		
		JSONObject actual = urlShortenerBean.extractJSONFromBody(body);
		
		try {
			assertEquals("https://test.com", actual.get("URL").toString());
			assertTrue(actual.get("CUSTOM_ALIAS").toString().isEmpty());
		} catch (JSONException e) {
			fail();
		}
	}

}
