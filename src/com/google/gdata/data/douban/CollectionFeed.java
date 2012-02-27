package com.google.gdata.data.douban;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

public class CollectionFeed extends BaseFeed<CollectionFeed, CollectionEntry> {

 
  public CollectionFeed() {
    super(CollectionEntry.class);
  }

 
  public CollectionFeed(BaseFeed sourceFeed) {
    super(CollectionEntry.class, sourceFeed);
  }

  @Override
  public void declareExtensions(ExtensionProfile extensionProfile) {
    super.declareExtensions(extensionProfile);
  }
}

