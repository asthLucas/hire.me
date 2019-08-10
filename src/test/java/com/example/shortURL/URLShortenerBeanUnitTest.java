package com.example.shortURL;

import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class URLShortenerBeanUnitTest {

	@Test
	public void testParseRequestParameters_whenRequestURLWithoutAlias_thenReturnsOneParameter()
	{
		String urlParameter = URLShortenerBean.parseRequestParameter("/create?u=http://test.com");
		
		assertTrue(urlParameter.equals("http://test.com"));
	}
	
	@Test
	public void testShorten_whenGivenURLWithoutCustomAlias_thenReturnShortenedURL() throws NoSuchAlgorithmException
	{
		String expected = "http://1f7b69.com";
		String actual   = URLShortenerBean.shorten("http://bemobi.com");
		assertTrue(expected.equals(actual));
		
		expected = "http://10b301.com";
		actual = URLShortenerBean.shorten("http://portal.ufrj.br");
		assertTrue(expected.equals(actual));
		
		expected = "http://17c2cc.com";
		actual = URLShortenerBean.shorten("http://google.com");
		assertTrue(expected.equals(actual));

		expected = "http://1d34fc.com";
		actual = URLShortenerBean.shorten("http://facebook.com");
		assertTrue(expected.equals(actual));
	}
}
