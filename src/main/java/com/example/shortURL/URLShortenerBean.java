package com.example.shortURL;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
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
		URLEntity urlEntity = urlEntityRepository.findByAlias(alias);
		
		if(urlEntity == null) {
			urlEntity = new URLEntity(originalURL, alias);
			urlEntityRepository.saveAndFlush(urlEntity);
		} else if (isURLAliasAlreadyInUse(urlEntity, originalURL, alias)) {
			return aliasAlreadyInUseErrorJSON();
		} else {
			urlEntity.incrementTimesRequested();
			urlEntityRepository.saveAndFlush(urlEntity);
		}
		
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ALIAS", urlEntity.getAlias());
		json.put("URL", "http://shortener/u/".concat(urlEntity.getAlias()));
		json.put("TIMESTAMP", new Date());
		
		return json;
	}
	
	public Map<String, Object> noURLSpecifiedErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERR_CODE", "000");
		json.put("DESCRIPTION", "No input was specified, please inform the URL you wish to shorten.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
	}
	
	public Map<String, Object> aliasAlreadyInUseErrorJSON()
	{
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("ERR_CODE", "001");
		json.put("DESCRIPTION", "Custom alias already in use for a different URL, please use a different one.");
		json.put("TIMESTAMP", new Date().toString());

		return json;
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
