package com.google.gdata.data.douban;

import java.util.List;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.extensions.Rating;

 @ExtensionDescription.Default(
		 nsAlias = "", 
		 nsUri ="http://www.w3.org/2005/Atom", 
		 localName = "entry")
public class NoteEntry extends BaseEntry<NoteEntry> {

	public NoteEntry() {
		super();
	}
	
	public NoteEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);

		extProfile.declare(NoteEntry.class, Attribute.class);
		extProfile.declareArbitraryXmlExtension(NoteEntry.class);
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