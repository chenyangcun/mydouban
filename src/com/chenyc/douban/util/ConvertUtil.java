package com.chenyc.douban.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenyc.douban.entity.Review;
import com.chenyc.douban.entity.Subject;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.ReviewEntry;
import com.google.gdata.data.douban.ReviewFeed;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.SubjectFeed;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.douban.UserEntry;

public class ConvertUtil {
	private static ArrayList<String> names;
	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static {
		names = new ArrayList<String>();
		names.add("authors");
		names.add("pubdate");
		names.add("publisher");
		names.add("price");
		names.add("pages");
		names.add("binding");
	}

	/**
	 * 获取条目列表
	 * 
	 * @param subjectFeed
	 * @param cat
	 * @return
	 */
	public static List<Subject> ConvertSubjects(SubjectFeed subjectFeed,
			String cat) {
		List<Subject> books = new ArrayList<Subject>();

		for (SubjectEntry entry : subjectFeed.getEntries()) {
			Subject book = new Subject();
			book.setTitle(entry.getTitle().getPlainText());
			book.setDescription(getDescription(entry));
			book.setUrl(entry.getId());
			book.setImgUrl(entry.getLink("image", null).getHref());
			book.setRating(entry.getRating().getAverage() / 2);
			book.setType(cat);
			books.add(book);
		}
		return books;
	}

	/**
	 * 获取条目描述信息
	 * 
	 * @param entry
	 * @return
	 */
	private static String getDescription(SubjectEntry entry) {
		String description = "";
		List<Attribute> attributes = entry.getAttributes();
		String authors = "";
		for (Person author : entry.getAuthors()) {
			authors += "," + author.getName();
		}
		if (authors.length() > 0) {
			authors = authors.substring(1);
		}

		Map<String, String> map = new HashMap<String, String>();

		for (Attribute attribute : attributes) {
			if (names.contains(attribute.getName())) {
				map.put(attribute.getName(), attribute.getContent());
			}
		}
		map.put("authors", authors);
		for (String name : names) {
			if (map.get(name) != null) {
				if ("price".equals(name)) {
					description += "/" + map.get(name) + "元";
				} else if ("pages".equals(name)) {
					description += "/" + map.get(name) + "页";
				} else {
					description += "/" + map.get(name);
				}
			}
		}
		if (description.length() > 0) {
			description = description.substring(1);
		}

		return description;
	}

	/**
	 * 转换一个条目
	 * 
	 * @param entry
	 * @return
	 */
	public static Subject convertOneSubject(SubjectEntry entry) {
		Subject book = new Subject();
		if (entry.getTitle() != null) {
			book.setTitle(entry.getTitle().getPlainText());
		}
		book.setDescription(getDescription(entry));
		book.setUrl(entry.getId());
		if (entry.getLink("image", null) != null) {
			book.setImgUrl(entry.getLink("image", null).getHref());
		}
		if (entry.getRating() != null) {
			book.setRating(entry.getRating().getAverage() / 2);
		}
		if (entry.getSummary() != null) {
			book.setSummary(entry.getSummary().getPlainText());
		} else {
			book.setSummary("");
		}
		book.setTags(entry.getTags());
		book.setAuthorIntro(getAuthorInfo(entry));
		return book;
	}

	private static String getAuthorInfo(SubjectEntry entry) {
		String authorInfo = "";
		for (Attribute attribute : entry.getAttributes()) {
			if ("author-intro".equals(attribute.getName())) {
				authorInfo = attribute.getContent();
			}
		}
		return authorInfo;
	}

	// 转换收藏
	public static List<Subject> ConvertCollection(CollectionFeed feed,
			String cat) {
		List<Subject> books = new ArrayList<Subject>();

		for (CollectionEntry entry : feed.getEntries()) {
			Subject book = new Subject();
			com.google.gdata.data.douban.Subject subject = entry
					.getSubjectEntry();
			book.setTitle(subject.getTitle().getPlainText());
			book.setDescription(getDescription(subject));
			book.setUrl(subject.getId());
			book.setImgUrl(subject.getLink("image", null).getHref());
			book.setType(cat);
			if (entry.getRating() != null) {
				book.setRating(entry.getRating().getValue());
			}
			book.setStatus(entry.getStatus().getContent());
			book = ConvertOneCollection(book,entry);
			books.add(book);
		}
		return books;
	}

	// 组装条目描述信息
	private static String getDescription(
			com.google.gdata.data.douban.Subject subject) {
		String description = "";
		List<Attribute> attributes = subject.getAttributes();
		String authors = "";
		for (Person author : subject.getAuthors()) {
			authors += "," + author.getName();
		}
		if (authors.length() > 0) {
			authors = authors.substring(1);
		}

		Map<String, String> map = new HashMap<String, String>();

		for (Attribute attribute : attributes) {
			if (names.contains(attribute.getName())) {
				map.put(attribute.getName(), attribute.getContent());
			}
		}
		map.put("authors", authors);
		for (String name : names) {
			if (map.get(name) != null) {
				if ("price".equals(name)) {
					description += "/" + map.get(name) + "元";
				} else if ("pages".equals(name)) {
					description += "/" + map.get(name) + "页";
				} else {
					description += "/" + map.get(name);
				}
			}
		}
		if (description.length() > 0) {
			description = description.substring(1);
		}

		return description;
	}

	//转换一个收藏
	public static Subject ConvertOneCollection(Subject subject,
			CollectionEntry collection) {
		subject.setCollection(true);
		String status = collection.getStatus().getContent();
		subject.setStatus(status);

		if (collection.getContent() != null) {
			String shortComment = ((TextContent) collection.getContent())
					.getContent().getPlainText();
			subject.setMyShortComment(shortComment);
		}
		
		if (collection.getRating() != null) {
			float rate = collection.getRating().getValue()
					.intValue();
			subject.setMyRating(rate);
		}
		
		String collectionId = collection.getId();
		subject.setCollectionUrl(collectionId);
		
		String myTags = "";
		for(Tag tag : collection.getTags()){
			myTags += tag.getName() + " ";
		}
		subject.setMyTags(myTags);
		return subject;
	}

	/**
	 * 获取用户的评论列表
	 */
	public static List<Review> ConvertReviews(ReviewFeed feed, Subject subject,
			UserEntry ue) {
		List<Review> reviews = ConvertReviews(feed, null);
		for (Review review : reviews) {
			review.setAuthorId(ue.getUid());
			review.setAuthorName(ue.getTitle().getPlainText());
			review.setAuthorImageUrl(ue.getLink("icon", null).getHref());
		}
		return reviews;
	}

	/**
	 * 获取评论列表
	 */
	public static List<Review> ConvertReviews(ReviewFeed feed, Subject subject) {
		List<Review> reviews = new ArrayList<Review>();
		for (ReviewEntry entry : feed.getEntries()) {
			Review review = new Review();

			if (entry.getId() != null) {
				review.setUrl(entry.getId());
			}

			if (entry.getTitle() != null) {
				review.setTitle(entry.getTitle().getPlainText());
			}
			if (entry.getSummary() != null) {
				review.setSummary(entry.getSummary().getPlainText());
			}

			if (entry.getRating() != null) {
				review.setRating(entry.getRating().getValue());
			}

			if (entry.getUpdated() != null) {
				review.setUpdated(df.format(new Date(entry.getUpdated()
						.getValue())));
			}

			List<Person> authors = entry.getAuthors();
			if (authors != null && authors.size() > 0) {
				Person author = entry.getAuthors().get(0);
				review.setAuthorName(author.getName());
				review.setAuthorId(author.getUri());
			}

			// 如果不存在subject，则获取
			if (subject == null) {
				subject = new Subject();
				com.google.gdata.data.douban.Subject googleSubject = entry
						.getSubjectEntry();
				subject.setTitle(googleSubject.getTitle().getPlainText());
				subject.setDescription(getDescription(googleSubject));
				subject.setUrl(googleSubject.getId());
			}
			review.setSubject(subject);
			reviews.add(review);
		}
		return reviews;
	}
	
	public static List<Tag> convertTags(String tags){
		List<Tag> tagList = new ArrayList<Tag>();
		for(String vtag:tags.split(" ")){
			Tag tag = new Tag();
			tag.setName(vtag);
			tagList.add(tag);
		}
		return tagList;
	}

}
