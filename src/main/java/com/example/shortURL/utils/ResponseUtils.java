package com.example.shortURL.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.shortURL.model.URLEntity;

public class ResponseUtils {

	public static Map<String, Object> buildResponseBody(URLEntity urlEntity, Long begining)
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ALIAS", urlEntity.getAlias());
		json.put("ORIGINAL_URL", urlEntity.getOriginalURL());
		json.put("URL", "http://shortener/u/".concat(urlEntity.getAlias()));
		json.put("TIMESTAMP", new Date());
		
		Map<String, Object> statistics = new HashMap<String, Object>();
		statistics.put("TIME_TAKEN", Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begining)).concat("ms"));
		statistics.put("TIMES_REQUESTED", urlEntity.getTimesRequested());
		json.put("STATISTICS", statistics);
		
		return json;
	}
	
	public static Map<String, Object> buildResponseBody(List<URLEntity> entities)
	{
		Map<String, Object> jsonResponse = new HashMap<String, Object>();
		
		List<Map<String, Object>> jsonEntities = new ArrayList<>();
		
		for(URLEntity urlEntity : entities)
		{
			Map<String, Object> jsonUrlEntity = new HashMap<String, Object>();
			
			jsonUrlEntity.put("ALIAS", urlEntity.getAlias());
			jsonUrlEntity.put("ORIGINAL_URL", urlEntity.getOriginalURL());
			jsonUrlEntity.put("URL", "http://shortener/u/".concat(urlEntity.getAlias()));
			jsonUrlEntity.put("TIMESTAMP", new Date());
			
			Map<String, Object> statistics = new HashMap<String, Object>();
			statistics.put("TIMES_REQUESTED", urlEntity.getTimesRequested());
			jsonUrlEntity.put("STATISTICS", statistics);
			
			jsonEntities.add(jsonUrlEntity);
		}

		jsonResponse.put("URLS", jsonEntities);
		
		return jsonResponse;
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
	
	public static Map<String, Object> urlAlreadyMappedToAliasErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERROR_CODE", "002");
		json.put("DESCRIPTION", "This URL has been mapped already.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
	}

	public static Map<String, Object> noResultsFoundErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERROR_CODE", "003");
		json.put("DESCRIPTION", "No URL found for the given identifier.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
	}

}
