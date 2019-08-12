package com.example.shortURL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("urlRetrieverBean")
public class URLRetrieverBean {

	@Autowired
	private URLEntityRepository urlEntityRepository;
	
	public Map<String, Object> retrieve(String uri)
	{
		Long begining = System.nanoTime();

		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias(uri, uri);
		
		if(urlEntity == null)
			return ResponseUtils.noResultsFoundErrorJSON();
		
		return ResponseUtils.buildResponseBody(urlEntity, begining);
	}
}
