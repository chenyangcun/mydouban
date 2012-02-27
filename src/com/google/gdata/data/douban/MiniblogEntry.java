package com.google.gdata.data.douban;

import java.util.List;

import com.google.gdata.data.BaseEntry;

import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;

@ExtensionDescription.Default(
		nsAlias = "", 
		nsUri = "http://www.w3.org/2005/Atom", 
		localName = "entry")
public class MiniblogEntry extends BaseEntry<MiniblogEntry> {

	public MiniblogEntry() {
		super();
	}

	public MiniblogEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);

		extProfile.declare(MiniblogEntry.class, Attribute.class);
		extProfile.declareArbitraryXmlExtension(MiniblogEntry.class);
	}
	
	protected List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		return getRepeatingExtension(Attribute.class);
	}
	public void setAttributes(List<Attribute> atts) {
		if (atts == null) {
			removeExtension(Attribute.class);
		} else {
			for (Attribute att : atts)
				addRepeatingExtension(att);
		}
	}

}
