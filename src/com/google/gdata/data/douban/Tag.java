package com.google.gdata.data.douban;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.google.gdata.data.Extension;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionPoint;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.XmlParser;
import com.google.gdata.util.common.xml.XmlWriter;

@ExtensionDescription.Default(nsAlias = Namespaces.doubanAlias, nsUri = Namespaces.doubanNamespace, localName = "tag")
public class Tag extends ExtensionPoint implements Extension {

	/** count of the tag */
	protected Integer count;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer v) {
		count = v;
	}

	/** Optional: if not set we use the user's default methods on this calendar */
	protected String name;

	public String getName() {
		return name;
	}
	
	public void setName(String v) {
		name = v;
	}

	/** Returns the suggested extension description. */
	public static ExtensionDescription getDefaultDescription() {
		ExtensionDescription desc = new ExtensionDescription();
		desc.setExtensionClass(Tag.class);
		desc.setNamespace(Namespaces.doubanNs);
		desc.setLocalName("tag");
		desc.setRepeatable(true);
		return desc;
	}

	public XmlParser.ElementHandler getHandler(ExtensionProfile extProfile,
			String namespace, String localName, Attributes attrs)
			throws ParseException {

		try {
			return new Handler(extProfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** <db:tag> parser. */
	private class Handler extends ExtensionPoint.ExtensionHandler {

		public Handler(ExtensionProfile extProfile) throws ParseException,
				IOException {

			super(extProfile, Tag.class);
		}

		public void processAttribute(String namespace, String localName,
				String value) throws ParseException {

			if (namespace.equals("")) {
				if (localName.equals("count")) {

					try {
						count = Integer.valueOf(value);
					} catch (NumberFormatException e) {
						throw new ParseException("Invalid db:tag/@days.", e);
					}
				} else if (localName.equals("name")) {

					try {
						name = value;
					} catch (NumberFormatException e) {
						throw new ParseException("Invalid db:Tag/@name.", e);
					}

				}
			}
		}
	}

	public void generate(XmlWriter w, ExtensionProfile extProfile)
			throws IOException {
		//System.out.println("Tag generate called");

		ArrayList<XmlWriter.Attribute> attrs = new ArrayList<XmlWriter.Attribute>(
				1);

		if (name != null) {

			attrs.add(new XmlWriter.Attribute(null, "name", name));
		}
		generateStartElement(w, Namespaces.doubanNs, "tag", attrs, null);

		//generateExtensions(w, extProfile);

		w.endElement(Namespaces.doubanNs, "tag");
	}

}
