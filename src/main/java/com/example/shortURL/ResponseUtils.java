package com.example.shortURL;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResponseUtils {

	public static Map<String, Object> buildResponseBody(URLEntity urlEntity, Long begining)
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ALIAS", urlEntity.getAlias());
		json.put("URL", "http://shortener/u/".concat(urlEntity.getAlias()));
		json.put("TIMESTAMP", new Date());
		
		Map<String, Object> statistics = new HashMap<String, Object>();
		statistics.put("TIME_TAKEN", Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begining)).concat("ms"));
		statistics.put("TIMES_REQUESTED", urlEntity.getTimesRequested());
		json.put("STATISTICS", statistics);
		
		return json;
	}
	
	public static Map<String, Object> noURLSpecifiedErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERROR_CODE", "000");
		json.put("DESCRIPTION", "No input was specified, please inform the URL you wish to shorten.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
	}
	
	public static Map<String, Object> aliasAlreadyInUseErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERROR_CODE", "001");
		json.put("DESCRIPTION", "Custom alias already in use for a different URL, please use a different one.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
	}
}
