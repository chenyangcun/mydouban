package com.google.gdata.data.douban;


import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;


public class UserFeed extends BaseFeed<UserFeed, UserEntry> {

  
  public UserFeed() {
    super(UserEntry.class);
  }

  public UserFeed(BaseFeed sourceFeed) {
    super(UserEntry.class, sourceFeed);
  }

  @Override
  public void declareExtensions(ExtensionProfile extensionProfile) {
    super.declareExtensions(extensionProfile);
  }
}
