package com.google.gdata.client.douban;

import java.net.URL;
import java.util.List;



public class ReviewQuery extends DoubanQuery {

  private static final String ORDERBY = "orderby";
 

  

  /**
   * Constructs a new DoubanQuery object that targets a feed.  The initial
   * state of the query contains no parameters, meaning all entries
   * in the feed would be returned if the query was executed immediately
   * after construction.
   *
   * @param feedUrl the URL of the feed against which queries will be
   *   executed.
   */
  public ReviewQuery(URL feedUrl) {
    super(feedUrl);
  }

  /**
   * Gets the value of the {@code vq} parameter.
   *
   * @return current query string
   */
  public String getOrderby() {
    return getCustomParameterValue(ORDERBY);
  }

 
  public void setOrderby(String orderby) {
		if (orderby != null)
			overwriteCustomParameter(ORDERBY, orderby);
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
