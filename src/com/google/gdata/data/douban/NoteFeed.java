package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

public class NoteFeed extends BaseFeed<NoteFeed, NoteEntry> {

	public NoteFeed() {
		super(NoteEntry.class);
	}

	public NoteFeed(BaseFeed sourceFeed) {
		super(NoteEntry.class, sourceFeed);
	}

	@Override
	public void declareExtensions(ExtensionProfile extensionProfile) {
		super.declareExtensions(extensionProfile);
	}
}