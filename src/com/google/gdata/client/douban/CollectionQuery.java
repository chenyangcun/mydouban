package com.google.gdata.client.douban;

import java.net.URL;
import java.util.List;


public class CollectionQuery extends SubjectQuery {

	private static final String CAT = "cat";
	private static final String STATUS = "status";


	/**
	 * Constructs a new YouTubeQuery object that targets a feed. The initial
	 * state of the query contains no parameters, meaning all entries in the
	 * feed would be returned if the query was executed immediately after
	 * construction.
	 * 
	 * @param feedUrl
	 *            the URL of the feed against which queries will be executed.
	 */
	public CollectionQuery(URL feedUrl) {
		super(feedUrl);
	}

	
	public String getStatus() {
		return getCustomParameterValue(STATUS);
	}
	public void setStatus(String tag) {
		overwriteCustomParameter(STATUS, tag);
	}
	public String getCat() {
		return getCustomParameterValue(CAT);
	}
	public void setCat(String tag) {
		overwriteCustomParameter(CAT, tag);
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
