package com.example.shortURL.retrieve;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.shortURL.model.URLEntity;
import com.example.shortURL.model.URLEntityRepository;
import com.example.shortURL.utils.ResponseUtils;

@Component("urlRetrieverBean")
public class URLRetrieverBean {

	@Autowired
	private URLEntityRepository urlEntityRepository;
	
	public ResponseEntity<Object> retrieve(String uri) throws URISyntaxException
	{
		Long begining = System.nanoTime();
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias(uri, uri);

		if(urlEntity == null)			
			return new ResponseEntity<Object>(ResponseUtils.noResultsFoundErrorJSON(), HttpStatus.NOT_FOUND);

		urlEntityRepository.saveAndFlush(urlEntity);
		
		URI uriToRedirect = new URI(urlEntity.getOriginalURL());
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uriToRedirect);
				
		return new ResponseEntity<Object>(ResponseUtils.buildResponseBody(urlEntity, begining), headers, HttpStatus.SEE_OTHER);
	}
	
	public ResponseEntity<Object> retrieveTop10()
	{
		List<URLEntity> urlEntities = urlEntityRepository.findTop10ByOrderByTimesRequestedDesc();
				
		return new ResponseEntity<Object>(ResponseUtils.buildResponseBody(urlEntities), HttpStatus.SEE_OTHER);
	}

}
