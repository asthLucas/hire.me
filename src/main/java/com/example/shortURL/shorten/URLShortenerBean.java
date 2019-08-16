package com.example.shortURL.shorten;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
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
	
	public Map<String, Object> shorten(JSONObject body) throws NoSuchAlgorithmException, JSONException
	{
		Long begining = System.nanoTime();

		JSONObject json = extractJSONFromBody(body);
		
		String url = (String) json.get("URL");
		String alias = (String) json.get("CUSTOM_ALIAS");
		
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias(url, alias);

		if(isDifferentAliasAlreadyUsed(urlEntity, alias))
			return ResponseUtils.urlAlreadyMappedToAliasErrorJSON();
		
		if(isAliasInUse(urlEntity, alias) && isAliasInUseByDifferentURL(urlEntity, url))
			return ResponseUtils.aliasAlreadyInUseErrorJSON();
		
		urlEntity = persistURL(urlEntity, url, alias);
		
		return ResponseUtils.buildResponseBody(urlEntity, begining);
	}

	public static String hashURL(String url)
	{
		XXHash64 xx = XXHashFactory.fastestJavaInstance().hash64();
		ByteBuffer buffer = ByteBuffer.wrap(url.getBytes());
		Long hash = xx.hash(buffer, 0L);
		
		return Long.toHexString(Long.parseUnsignedLong((hash.toString().replace("-", "").substring(0, 14)))).substring(0, SHORT_URL_SIZE);
	}

	public boolean validateShortenRequestURL(JSONObject json)
	{
		try {
			String url = (String) json.get("url");

			if(url.isEmpty())
				return false;
			
			if(url.contains("http://") || url.contains("https://"))
				return true;

			return false;
			
		} catch (JSONException e) {
			return false;
		}
	}
	
	protected JSONObject extractJSONFromBody(JSONObject body) throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("URL", "");
		json.put("CUSTOM_ALIAS", "");

		if(body == null)
			return json;
		
		try {
			json.put("URL", body.get("url"));
			json.put("CUSTOM_ALIAS", body.get("custom_alias"));
		} catch (JSONException e) {
			return json;
		}
		
		return json;
	}
	
	private boolean isDifferentAliasAlreadyUsed(URLEntity urlEntity, String alias)
	{
		if(urlEntity == null || alias == null || alias.isEmpty())
			return false;
		
		return !urlEntity.getAlias().equals(alias);
	}
	
	private boolean isAliasInUse(URLEntity urlEntity, String alias)
	{
		if(urlEntity == null || alias == null)
			return false;
		
		return urlEntity.getAlias().equals(alias);
	}
	
	private URLEntity persistURL(URLEntity urlEntity, String originalURL, String alias)
	{
		if(urlEntity == null) {
			urlEntity = new URLEntity(originalURL, alias);
			urlEntityRepository.saveAndFlush(urlEntity);
			return urlEntity;
		} else {
			urlEntity.incrementTimesRequested();
			urlEntityRepository.saveAndFlush(urlEntity);
			return urlEntity;
		}
	}
	
	private boolean isAliasInUseByDifferentURL(URLEntity entity, String url)
	{
		boolean isInUseForDifferentURL = !entity.getOriginalURL().equals(url);

		return isInUseForDifferentURL;
	}

	public void setUrlEntityRepository(URLEntityRepository urlEntityRepository) {
		this.urlEntityRepository = urlEntityRepository;
	}
	
}
