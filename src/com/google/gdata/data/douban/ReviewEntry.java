package com.google.gdata.data.douban;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.extensions.Rating;

@ExtensionDescription.Default(
		nsAlias = "", 
		nsUri = "http://www.w3.org/2005/Atom", 
		localName = "entry")
public class ReviewEntry extends BaseEntry<ReviewEntry> {
	
	public ReviewEntry() {
		super();
	}

	public ReviewEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
		// getCategories().add(SUBJECT_CATEGORY);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {
		super.declareExtensions(extProfile);
		
		extProfile.declareAdditionalNamespace(Namespaces.gNs);
	
		extProfile.declare(ReviewEntry.class, Subject.class);
		new Subject().declareExtensions(extProfile);
		extProfile.declareAdditionalNamespace(Namespaces.doubanNs);
		
		extProfile.declare(ReviewEntry.class, Rating
				.getDefaultDescription(false));
	}


	public Subject getSubjectEntry() {
		return getExtension(Subject.class);
	}

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

	public void setSubject(Subject subject) {
		if (subject == null) {
			removeExtension(Subject.class);
		} else {
			setExtension(subject);
		}
	}

}