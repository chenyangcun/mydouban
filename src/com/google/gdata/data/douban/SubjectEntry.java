package com.google.gdata.data.douban;

import java.util.List;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.extensions.Rating;

 @ExtensionDescription.Default(
		 nsAlias = "", 
		 nsUri ="http://www.w3.org/2005/Atom", 
		 localName = "entry")
public class SubjectEntry extends BaseEntry<SubjectEntry> {

	public SubjectEntry() {
		super();
	}
	
	public SubjectEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);
		ExtensionDescription desc = Tag.getDefaultDescription();
		desc.setRepeatable(true);
		extProfile.declare(SubjectEntry.class, desc);

		extProfile.declare(SubjectEntry.class, Attribute.class);
		extProfile.declare(SubjectEntry.class, Rating
				.getDefaultDescription(false));
	
		extProfile.declareArbitraryXmlExtension(SubjectEntry.class);
	}

	

	protected List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		return getRepeatingExtension(Attribute.class);
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

}