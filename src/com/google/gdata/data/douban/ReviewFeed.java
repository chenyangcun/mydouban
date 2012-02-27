package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

public class ReviewFeed extends BaseFeed<ReviewFeed, ReviewEntry> {

	public ReviewFeed() {
		super(ReviewEntry.class);
	}

	public ReviewFeed(BaseFeed sourceFeed) {
		super(ReviewEntry.class, sourceFeed);
	}

	@Override
	public void declareExtensions(ExtensionProfile extensionProfile) {
		super.declareExtensions(extensionProfile);
	}
}
