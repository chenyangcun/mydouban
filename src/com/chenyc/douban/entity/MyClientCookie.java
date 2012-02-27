package com.chenyc.douban.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class MyClientCookie implements Serializable {

	private String name;
	private String value;
	private String domain;
	private String comment;
	private Date expiryDate;
	private String path;
	private int version;

	public MyClientCookie(Cookie cookie) {
		this.name = cookie.getName();
		this.value = cookie.getValue();
		this.domain =cookie.getDomain();
		this.expiryDate = cookie.getExpiryDate();
		this.comment=cookie.getComment();
		this.path = cookie.getPath();
		this.version = cookie.getVersion();
	}
	
	public Cookie toBasicCookie(){
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setComment(comment);
		cookie.setExpiryDate(expiryDate);
		cookie.setPath(path);
		cookie.setVersion(version);
		return cookie;
	}

	private static final long serialVersionUID = 1L;

}
