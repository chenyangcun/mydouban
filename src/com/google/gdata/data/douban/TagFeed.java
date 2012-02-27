package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;


public class TagFeed extends BaseFeed<TagFeed, TagEntry> {

  public TagFeed() {
    super(TagEntry.class);
  }

  public TagFeed(BaseFeed sourceFeed) {
    super(TagEntry.class, sourceFeed);
  }

  @Override
  public void declareExtensions(ExtensionProfile extensionProfile) {
    super.declareExtensions(extensionProfile);
  }
}
