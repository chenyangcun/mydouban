package com.google.gdata.data.douban;

import com.google.gdata.data.BaseEntry;

import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;

@ExtensionDescription.Default(
		nsAlias = "", 
		nsUri = "http://www.w3.org/2005/Atom", 
		localName = "entry")
public class TagEntry extends BaseEntry<TagEntry> {

	public TagEntry() {
		super();
	}

	public TagEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);

		extProfile.declare(TagEntry.class, Count.class);
	}

	public Count getCount() {
		return getExtension(Count.class);
	}

	public void setCount(String count) {
		setExtension(new Count(count));
	}
}
