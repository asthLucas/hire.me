package com.example.shortURL;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLShortenerController {

	@GetMapping("/create")
	public Map<String, Object> shortenURL(@RequestParam("u") String urlToShorten)
	{
		if(urlToShorten == null || urlToShorten.isBlank() || urlToShorten.isEmpty())
		{
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("ERR_CODE", "000");
			json.put("DESCRIPTION", "No input was specified, please inform the URL you wish to shorten.");
			json.put("TIMESTAMP", new Date().toString());
			
			return json;
		}
		return null;
	}
}
