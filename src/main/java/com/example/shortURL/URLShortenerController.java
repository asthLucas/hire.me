package com.example.shortURL;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLShortenerController {

	@Autowired
	private URLShortenerBean urlShortenerBean;
	
	@GetMapping("/create")
	public Map<String, Object> shortenURL(@RequestParam("URL") String urlToShorten,
										  @RequestParam(name = "CUSTOM_ALIAS", required = false) String customAlias ) throws NoSuchAlgorithmException
	{
		if(urlToShorten == null || urlToShorten.isBlank() || urlToShorten.isEmpty())
		{
			return urlShortenerBean.defaultShortenerErrorJSON();
		}
		
		return urlShortenerBean.shorten(urlToShorten, customAlias);
	}
}
