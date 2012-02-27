package com.google.gdata.client.douban;

import java.net.URL;
import java.util.List;



/**
 * A helper class that helps building queries for the
 * Douban feeds.
 *
 * Not all feeds implement all parameters defined on
 * this class. See the documentation to get the list
 * of parameters each feed supports.
 *
 * 
 */
public class SubjectQuery extends DoubanQuery {

  private static final String TAG = "tag";
 

  

  /**
   * Constructs a new DoubanQuery object that targets a feed.  The initial
   * state of the query contains no parameters, meaning all entries
   * in the feed would be returned if the query was executed immediately
   * after construction.
   *
   * @param feedUrl the URL of the feed against which queries will be
   *   executed.
   */
  public SubjectQuery(URL feedUrl) {
    super(feedUrl);
  }

  /**
   * Gets the value of the {@code vq} parameter.
   *
   * @return current query string
   */
  public String getTag() {
    return getCustomParameterValue(TAG);
  }

 
  public void setTag(String tag) {
		if (tag != null)
			overwriteCustomParameter(TAG, tag);
	}


  void overwriteCustomParameter(String name, String value) {
    List<CustomParameter> customParams = getCustomParameters();

    // Remove any existing value.
    for (CustomParameter existingValue : getCustomParameters(name)) {
      customParams.remove(existingValue);
    }

    // Add the specified value.
    if (value != null) {
      customParams.add(new CustomParameter(name, value));
    }
  }

  String getCustomParameterValue(String parameterName) {
    List<CustomParameter> customParams = getCustomParameters(parameterName);
    if (customParams.isEmpty()) {
      return null;
    }
    return customParams.get(0).getValue();
  }
}
