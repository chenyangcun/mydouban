package com.google.gdata.data.douban;

import com.google.gdata.data.ExtensionDescription;



@ExtensionDescription.Default(
		nsAlias = Namespaces.doubanAlias, 
		nsUri = Namespaces.doubanNamespace, 
		localName = "status")
public class Status extends AbstractFreeTextExtension {
	/** Creates an empty tag. */
	public Status() {
	}

	/**
	 * Creates a tag and initializes its content.
	 * 
	 * @param status
	 *            content
	 */
	public Status(String status) {
		super(status);
	}

}
