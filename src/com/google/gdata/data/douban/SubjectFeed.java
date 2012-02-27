package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

public class SubjectFeed extends BaseFeed<SubjectFeed, SubjectEntry> {

	public SubjectFeed() {
		super(SubjectEntry.class);
	}

	public SubjectFeed(BaseFeed sourceFeed) {
		super(SubjectEntry.class, sourceFeed);
	}

	@Override
	public void declareExtensions(ExtensionProfile extensionProfile) {
		super.declareExtensions(extensionProfile);
	}
}
