package com.google.gdata.client.douban;

import java.net.URL;
import java.util.List;

import com.google.gdata.client.Query;


public class DoubanQuery extends Query {

	private static final String APIKEY = "apikey";



	/**
	 * Constructs a new YouTubeQuery object that targets a feed. The initial
	 * state of the query contains no parameters, meaning all entries in the
	 * feed would be returned if the query was executed immediately after
	 * construction.
	 * 
	 * @param feedUrl
	 *            the URL of the feed against which queries will be executed.
	 */
	public DoubanQuery(URL feedUrl) {
		super(feedUrl);
	}

	@Override
	public void setFullTextQuery(String query) {
		if (query != null)
			super.setFullTextQuery(query);
	}

	public String getApiKey() {
		return getCustomParameterValue(APIKEY);
	}
	public void setApiKey(String apikey) {
		overwriteCustomParameter(APIKEY, apikey);
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
