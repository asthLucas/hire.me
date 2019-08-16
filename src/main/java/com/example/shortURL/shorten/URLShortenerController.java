package com.example.shortURL.shorten;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shortURL.utils.ResponseUtils;

@RestController
public class URLShortenerController {
	
	@Autowired
	private URLShortenerBean urlShortenerBean;
	
	@PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> shortenURL(@RequestBody String bodyString) throws NoSuchAlgorithmException, JSONException
	{
		JSONObject body = new JSONObject(bodyString);
		
		if(!urlShortenerBean.validateShortenRequestURL(body))
			return ResponseUtils.noURLSpecifiedErrorJSON();
		
		return urlShortenerBean.shorten(body);
	}	
}
