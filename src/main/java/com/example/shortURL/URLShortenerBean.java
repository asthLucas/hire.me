package com.example.shortURL;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.utils.URLEncodedUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.springframework.stereotype.Component;

@Component
public class URLShortenerBean {
	final static int SHORT_URL_SIZE = 6;
	
	public static String shorten(String url) throws NoSuchAlgorithmException
	{
		String hashedURL = hashURL(url);
		
		String shortURL = "http://".concat(hashedURL.substring(0, SHORT_URL_SIZE)).concat(".com");

		return shortURL;
	}
	
	protected static String parseRequestParameter(String url)
	{
		return URLEncodedUtils.parse(url, Charset.defaultCharset()).get(0).toString();
	}
	
	private static String hashURL(String url)
	{
		String urlToShorten = parseRequestParameter(url);

		DigestSHA3 SHA3 = new SHA3.Digest512();
		byte[] digest = SHA3.digest(urlToShorten.getBytes());
		
		StringBuilder bytes = new StringBuilder();
		
		for (int i = 0; i < digest.length; i++) {
			bytes.append(Math.abs(digest[i]));
		}
		
		return Long.toHexString(Long.parseUnsignedLong((bytes.toString().substring(0, 14))));
	}
}
