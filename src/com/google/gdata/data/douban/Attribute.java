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
		localName = "attribute", 
		isRepeatable = true)
public class Attribute extends AbstractElementWithContent {

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String v) {
		name = v;
	}

	protected String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String v) {
		index = v;
	}

	protected String lang;

	public String getLang() {
		return lang;
	}

	public void setLang(String v) {
		lang = v;
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

	/**
	 * Returns the suggested extension description with configurable
	 * repeatabilty.
	 */
	public static ExtensionDescription getDefaultDescription(boolean repeatable) {
		ExtensionDescription desc = ExtensionDescription
				.getDefaultDescription(Attribute.class);
		desc.setRepeatable(repeatable);
		return desc;
	}

	/** Returns the suggested extension description and is repeatable. */
	public static ExtensionDescription getDefaultDescription() {
		return getDefaultDescription(true);
	}

	@Override
	protected void putAttributes(AttributeGenerator generator) {
		generator.setContent(content);
		generator.put("name", name);
		generator.put("index", index);
		generator.put("lang", lang);
	}

	@Override
	protected void consumeAttributes(AttributeHelper helper)
			throws ParseException {
		name = helper.consume("name", true);
		index = helper.consume("index", false);
		lang = helper.consume("lang", false);
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
