package com.example.shortURL;

import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.jupiter.api.Test;

public class URLShortenerBeanUnitTest {

	@Test
	public void testParseRequestParameters_whenRequestURLWithoutAlias_thenReturnsOneParameter()
	{
		List<String> urlParameter = URLShortenerBean.parseRequestParameter("http://test.com");
		
		assertTrue(urlParameter.get(0).equals("http://test.com"));
	}

	@Test
	public void testParseRequestParameters_whenRequestURLWithAlias_thenReturnsTwoParameters()
	{
		List<String> urlParameter = URLShortenerBean.parseRequestParameter("http://test.com&CUSTOM_ALIAS=bemobi");
		
		assertTrue(urlParameter.get(0).equals("http://test.com"));
		assertTrue(urlParameter.get(1).equals("bemobi"));
	}
	
	@Test
	public void testShorten_whenGivenURLWithoutCustomAlias_thenReturnShortenedURL() throws NoSuchAlgorithmException
	{
		String expected = "http://shortener/u/1f7b69";
		String actual   = URLShortenerBean.shorten("http://bemobi.com");
		assertTrue(expected.equals(actual));
		
		expected = "http://shortener/u/10b301";
		actual = URLShortenerBean.shorten("http://portal.ufrj.br");
		assertTrue(expected.equals(actual));
		
		expected = "http://shortener/u/17c2cc";
		actual = URLShortenerBean.shorten("http://google.com");
		assertTrue(expected.equals(actual));
		
		expected = "http://shortener/u/1d34fc";
		actual = URLShortenerBean.shorten("http://facebook.com");
		assertTrue(expected.equals(actual));
	}
}
