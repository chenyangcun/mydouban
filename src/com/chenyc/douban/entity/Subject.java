package com.chenyc.douban.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gdata.data.douban.Tag;

/**
 * 条目（书，电影，音乐）
 * 
 * @author chenyc
 * 
 */
public class Subject implements Serializable, Comparable<Subject> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BOOK = "book";
	public static final String MOVIE = "movie";
	public static final String MUSIC = "music";

	public String getCollectionUrl() {
		return collectionUrl;
	}

	public void setCollectionUrl(String collectionUrl) {
		this.collectionUrl = collectionUrl;
	}

	public float getMyRating() {
		return myRating;
	}

	public void setMyRating(float myRating) {
		this.myRating = myRating;
	}

	public String getMyShortComment() {
		return myShortComment;
	}

	public void setMyShortComment(String myShortComment) {
		this.myShortComment = myShortComment;
	}

	public String getMyTags() {
		return myTags;
	}

	public void setMyTags(String myTags) {
		this.myTags = myTags;
	}

	private String authorIntro;
	private boolean collection;
	private String collectionUrl;
	private String description; // 描述：作者等
	private String imgUrl; // 图片地址
	private float myRating;
	private String myShortComment;
	private String myTags = "";
	private float rating;
	private String status;
	private String summary; // 摘要
	private List<Tag> tags;
	private String title;
	private String type;
	private String url; // url地址

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		String id = "";
		if (this.url != null) {
			id = this.url.substring(this.url.lastIndexOf("/") + 1);
		}
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getAuthorIntro() {
		return authorIntro;
	}

	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getTagsToString() {
		String result = "";
		for (Tag tag : this.tags) {
			result += "," + tag.getName();
		}
		if (result.length() > 0) {
			result = result.substring(1);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((imgUrl == null) ? 0 : imgUrl.hashCode());
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subject other = (Subject) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (imgUrl == null) {
			if (other.imgUrl != null)
				return false;
		} else if (!imgUrl.equals(other.imgUrl))
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public int compareTo(Subject sub) {
		if (sub == null) {
			return 1;
		}
		if (this.getRating() > sub.getRating()) {
			return 1;
		}
		if (this.getRating() < sub.getRating()) {
			return 1;
		}
		return 0;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
