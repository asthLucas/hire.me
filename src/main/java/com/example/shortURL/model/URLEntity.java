package com.example.shortURL.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.example.shortURL.shorten.URLShortenerBean;

@Entity
public class URLEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long oid;
	
	private String originalURL;
	private String alias;
	private Long timesRequested;
	private Date lastAccessTimestamp;

	public URLEntity() {};
	
	public URLEntity(String url, String alias)
	{		
		if(alias == null || alias.isEmpty())
		{
			String hashedURL = URLShortenerBean.hashURL(url);
			this.alias = hashedURL;
		} else {
			this.alias = alias;
		}
		
		this.originalURL = url;
		this.timesRequested = Long.valueOf(1);
		this.lastAccessTimestamp = new Date();
	}
	
	public void incrementTimesRequested()
	{
		this.timesRequested += 1;
	}
	
	public Long getOid() {
		return oid;
	}
	public void setOid(Long oid) {
		this.oid = oid;
	}
	public String getOriginalURL() {
		return originalURL;
	}
	public void setOriginalURL(String originalURL) {
		this.originalURL = originalURL;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Long getTimesRequested() {
		return timesRequested;
	}
	public void setTimesRequested(Long timesRequested) {
		this.timesRequested = timesRequested;
	}
	public Date getLastAccessTimestamp() {
		return lastAccessTimestamp;
	}
	public void setLastAccessTimestamp(Date lastAccessTimestamp) {
		this.lastAccessTimestamp = lastAccessTimestamp;
	}
}
