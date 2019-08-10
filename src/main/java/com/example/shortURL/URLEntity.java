package com.example.shortURL;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class URLEntity {
	
	@Id
	private Long oid;
	
	private String originalURL;
	private String alias;
	private Long timesRequested;
	private Date lastAccessTimestamp;

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
