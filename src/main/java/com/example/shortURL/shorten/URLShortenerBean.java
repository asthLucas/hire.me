package com.example.shortURL.shorten;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.shortURL.model.URLEntity;
import com.example.shortURL.model.URLEntityRepository;
import com.example.shortURL.utils.ResponseUtils;

import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;

@Component("urlShortenerBean")
public class URLShortenerBean {
	final static int SHORT_URL_SIZE = 6;
	
	@Autowired
	private URLEntityRepository urlEntityRepository;

	public Map<String, Object> shorten(String originalURL, String alias) throws NoSuchAlgorithmException
	{
		Long begining = System.nanoTime();
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias(originalURL, alias);

		if(!validateAliasInUseByDifferentURL(urlEntity, alias))
			return ResponseUtils.urlAlreadyMappedToAliasErrorJSON();
		
		urlEntity = validateAndPersistURL(urlEntity, originalURL, alias);
		if(urlEntity == null)
			return ResponseUtils.aliasAlreadyInUseErrorJSON();
		
		return ResponseUtils.buildResponseBody(urlEntity, begining);
	}
	
	private boolean validateAliasInUseByDifferentURL(URLEntity urlEntity, String alias)
	{
		if(urlEntity == null || alias == null)
			return true;
		
		return urlEntity.getAlias().equals(alias);
	}
	
	private URLEntity validateAndPersistURL(URLEntity urlEntity, String originalURL, String alias)
	{
		if(urlEntity == null) {
			urlEntity = new URLEntity(originalURL, alias);
			urlEntityRepository.saveAndFlush(urlEntity);
			return urlEntity;
		} else if (isURLAliasAlreadyInUse(urlEntity, originalURL, alias)) {
			return null;
		} else {
			urlEntity.incrementTimesRequested();
			urlEntityRepository.saveAndFlush(urlEntity);
			return urlEntity;
		}
	}
	
	private boolean isURLAliasAlreadyInUse(URLEntity entity, String url, String alias)
	{
		boolean isAliasAlreadyInUse = entity.getAlias().equals(alias);
		boolean isInUseForSameURL = entity.getOriginalURL().equals(url);

		return isAliasAlreadyInUse && !isInUseForSameURL;
	}
	
	public static String hashURL(String url)
	{
		XXHash64 xx = XXHashFactory.fastestJavaInstance().hash64();
		ByteBuffer buffer = ByteBuffer.wrap(url.getBytes());
		Long hash = xx.hash(buffer, 0L);
		
		return Long.toHexString(Long.parseUnsignedLong((hash.toString().replace("-", "").substring(0, 14)))).substring(0, SHORT_URL_SIZE);
	}

	public void setUrlEntityRepository(URLEntityRepository urlEntityRepository) {
		this.urlEntityRepository = urlEntityRepository;
	}
	
}
