package com.example.shortURL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLRetrieverController {
	
	@Autowired
	private URLRetrieverBean urlRetrieverBean;
	
	@GetMapping("/find")
	public Map<String, Object> retrieveURL(@RequestParam("URI") String uri)
	{
		return urlRetrieverBean.retrieve(uri);
	}
}
