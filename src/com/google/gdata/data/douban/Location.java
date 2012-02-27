package com.google.gdata.data.douban;

import com.google.gdata.data.AttributeGenerator;
import com.google.gdata.data.AttributeHelper;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.XmlParser;


@ExtensionDescription.Default(
		nsAlias = Namespaces.doubanAlias, 
		nsUri = Namespaces.doubanNamespace, 
		localName = "location")
		
public class Location extends AbstractElementWithContent {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String v) {
		id = v;
	}

	private String content;

	/** Gets the content string. */
	public String getContent() {
		return content;
	}

	/** Sets the content string. */
	public void setContent(String content) {
		this.content = content;
	}


	@Override
	protected void putAttributes(AttributeGenerator generator) {
		generator.setContent(content);
		generator.put("id", id);
	}

	@Override
	protected void consumeAttributes(AttributeHelper helper)
			throws ParseException {
		id = helper.consume("id", false);
		content = helper.consumeContent(false);
	}
	
	

	public class AtomHandler extends XmlParser.ElementHandler {

		public AtomHandler(ExtensionProfile extProfile) {
		}

		/**
		 * Processes this element; overrides inherited method.
		 */
		
		@Override
		public void processEndElement() throws ParseException {
			content = value;
		}
		
	}
}

