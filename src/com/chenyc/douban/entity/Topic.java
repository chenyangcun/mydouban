package com.chenyc.douban.entity;

import java.io.Serializable;

public class Topic implements Serializable{

	private String title;
	private String content;
	private String url;
	private String author;
	private String reply;
	private String lastdate;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getLastdate() {
		return lastdate;
	}
	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

	public Topic(String title, String content, String url, String author,
			String reply, String lastdate) {
		super();
		this.title = title;
		this.content = content;
		this.url = url;
		this.author = author;
		this.reply = reply;
		this.lastdate = lastdate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Topic(){}
}
