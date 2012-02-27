package com.chenyc.douban.entity;

import java.io.Serializable;

public class Group implements Serializable{

	private String url;
	private String name;
	private String totalPeople;
	public Group(String url, String name, String totalPeople, String imgUrl) {
		super();
		this.url = url;
		this.name = name;
		this.totalPeople = totalPeople;
		this.imgUrl = imgUrl;
	}
	public Group()
	{
	}
	private String imgUrl;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTotalPeople() {
		return totalPeople;
	}
	public void setTotalPeople(String totalPeople) {
		this.totalPeople = totalPeople;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
