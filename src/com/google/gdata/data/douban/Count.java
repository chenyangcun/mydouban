package com.google.gdata.data.douban;

import com.google.gdata.data.ExtensionDescription;


@ExtensionDescription.Default(
		nsAlias = Namespaces.doubanAlias, 
		nsUri = Namespaces.doubanNamespace, 
		localName = "count")
public class Count extends AbstractFreeTextExtension {
	
	/** Creates an empty tag. */
	public Count() {
	}

	/**
	 * Creates a tag and initializes its content.
	 * 
	 * @param count
	 *            content
	 */
	public Count(String count) {
		super(count);
	}

}