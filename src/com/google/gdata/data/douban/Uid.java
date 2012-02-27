package com.google.gdata.data.douban;

import com.google.gdata.data.ExtensionDescription;


@ExtensionDescription.Default(
		nsAlias = Namespaces.doubanAlias, 
		nsUri = Namespaces.doubanNamespace, 
		localName = "uid")
public class Uid extends AbstractFreeTextExtension {
	/** Creates an empty tag. */
	public Uid() {
	}

	/**
	 * Creates a tag and initializes its content.
	 * 
	 * @param uid 
	 *            content
	 */
	public Uid(String uid) {
		super(uid);
	}

}
