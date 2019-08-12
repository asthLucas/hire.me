package com.example.shortURL;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("urlShortenerBean")
public class URLShortenerBean {
	final static int SHORT_URL_SIZE = 6;
	
	@Autowired
	private URLEntityRepository urlEntityRepository;

	public Map<String, Object> shorten(String originalURL, String alias) throws NoSuchAlgorithmException
	{
		Long begining = System.nanoTime();
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias(originalURL, alias);
		urlEntity = validateAndPersistURL(urlEntity, originalURL, alias);
		
		if(urlEntity == null)
			return ResponseUtils.aliasAlreadyInUseErrorJSON();
		
		return ResponseUtils.buildResponseBody(urlEntity, begining);
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
	
	static String hashURL(String url)
	{
		DigestSHA3 SHA3 = new SHA3.Digest512();
		byte[] digest = SHA3.digest(url.getBytes());
		
		StringBuilder bytes = new StringBuilder();
		
		for (int i = 0; i < digest.length; i++) {
			bytes.append(Math.abs(digest[i]));
		}
		
		return Long.toHexString(Long.parseUnsignedLong((bytes.toString().substring(0, 14)))).substring(0, SHORT_URL_SIZE);
	}
	
	public void setUrlEntityRepository(URLEntityRepository urlEntityRepository) {
		this.urlEntityRepository = urlEntityRepository;
	}
}
