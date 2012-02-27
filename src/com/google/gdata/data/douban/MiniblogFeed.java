package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

public class MiniblogFeed extends BaseFeed<MiniblogFeed, MiniblogEntry> {

	public MiniblogFeed() {
		super(MiniblogEntry.class);
	}

	public MiniblogFeed(BaseFeed sourceFeed) {
		super(MiniblogEntry.class, sourceFeed);
	}

	@Override
	public void declareExtensions(ExtensionProfile extensionProfile) {
		super.declareExtensions(extensionProfile);
	}
}
