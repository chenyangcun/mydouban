package com.google.gdata.data.douban;

import java.util.List;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.extensions.Rating;

//TODO: link elements inauthor can not be processed 
@ExtensionDescription.Default(nsAlias = "", nsUri = "http://www.w3.org/2005/Atom", localName = "entry")
public class CollectionEntry extends BaseEntry<CollectionEntry> {

	public CollectionEntry() {
		super();
	}

	public CollectionEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);
		ExtensionDescription desc = Tag.getDefaultDescription();
		desc.setRepeatable(true);
		extProfile.declare(CollectionEntry.class, desc);

		extProfile.declare(CollectionEntry.class, Attribute.class);
		extProfile.declare(CollectionEntry.class, Status.class);

		extProfile.declare(CollectionEntry.class, Subject.class);
		extProfile.declare(CollectionEntry.class, Rating
				.getDefaultDescription(false));
		extProfile.declareArbitraryXmlExtension(CollectionEntry.class);
	}

	public Status getStatus() {
		return getExtension(Status.class);
	}

	public List<Attribute> getAttributes() {
		return getRepeatingExtension(Attribute.class);
	}

	public Subject getSubjectEntry() {
		return getExtension(Subject.class);
	}

	public List<Tag> getTags() {
		return getRepeatingExtension(Tag.class);
	}

	/** Gets the gd:rating tag. */
	public Rating getRating() {
		return getExtension(Rating.class);
	}

	/** Sets the gd:rating tag. */
	public void setRating(Rating rating) {
		if (rating == null) {
			removeExtension(Rating.class);
		} else {
			setExtension(rating);
		}
	}

	public void setSubjectEntry(Subject subject) {
		if (subject == null) {
			removeExtension(Subject.class);
		} else {
			setExtension(subject);
		}

	}

	public void setStatus(Status status) {
		if (status == null) {
			removeExtension(Status.class);
		} else {
			setExtension(status);
		}

	}

	public void setTags(List<Tag> tags) {
		if (tags == null) {
			removeExtension(Tag.class);
		} else {
			for (Tag tag : tags)
				addRepeatingExtension(tag);
		}
	}

}
