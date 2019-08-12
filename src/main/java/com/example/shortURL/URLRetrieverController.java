package com.example.shortURL;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLRetrieverController {
	
	@Autowired
	private URLRetrieverBean urlRetrieverBean;
	
	@GetMapping("/find")
	public ResponseEntity<Object> retrieveURL(@RequestParam("URI") String uri) throws URISyntaxException
	{
		return urlRetrieverBean.retrieve(uri);
	}
}
