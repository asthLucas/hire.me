package com.example.shortURL;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.springframework.stereotype.Component;

@Component
public class URLShortenerBean {
	final static int SHORT_URL_SIZE = 6;
	
	public static String shorten(String url) throws NoSuchAlgorithmException
	{
		String hashedURL = hashURL(url);
		String shortURL = "http://shortener/u/".concat(hashedURL.substring(0, SHORT_URL_SIZE));

		return shortURL;
	}
	
	protected static List<String> parseRequestParameter(String url)
	{
		List<String> requestParameters = Arrays.asList(url.split("\\&"));

		if(requestParameters.size() == 2)
			requestParameters.set(1, requestParameters.get(1).replace("CUSTOM_ALIAS=", ""));
		
		return requestParameters;
	}
	
	private static String hashURL(String url)
	{
		String urlToShorten = parseRequestParameter(url).get(0);

		DigestSHA3 SHA3 = new SHA3.Digest512();
		byte[] digest = SHA3.digest(urlToShorten.getBytes());
		
		StringBuilder bytes = new StringBuilder();
		
		for (int i = 0; i < digest.length; i++) {
			bytes.append(Math.abs(digest[i]));
		}
		
		return Long.toHexString(Long.parseUnsignedLong((bytes.toString().substring(0, 14))));
	}
}
