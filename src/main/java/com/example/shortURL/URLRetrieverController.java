package com.example.shortURL;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLRetrieverController {
	
	@Autowired
	private URLRetrieverBean urlRetrieverBean;
	
	@GetMapping("/u/{URI}")
	public ResponseEntity<Object> retrieveURL(@PathVariable("URI") String uri) throws URISyntaxException
	{
		return urlRetrieverBean.retrieve(uri);
	}
}
