package com.google.gdata.data.douban;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionPoint;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.XmlParser;
import com.google.gdata.util.XmlParser.ElementHandler;
import com.google.gdata.util.common.xml.XmlWriter;

/**
 * User entry specific for a douban user. Contains setters and getters for all
 * fields specific to user data.
 */

@ExtensionDescription.Default(
		nsAlias = "db", 
		nsUri = "http://www.douban.com/xmlns/", 
		localName = "subject")
public class Subject extends BaseEntry<Subject> {

	
	public Subject() {
		super();
	}

	public Subject(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {

		super.declareExtensions(extProfile);
		ExtensionDescription desc = Tag.getDefaultDescription();
		desc.setRepeatable(true);
		extProfile.declare(Subject.class, desc);

		extProfile.declare(Subject.class, Attribute.class);
		extProfile.declare(Subject.class, Rating
				.getDefaultDescription(false));
		
		extProfile.declareArbitraryXmlExtension(Subject.class);
	}


	protected List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		// different from the implement in SubjectEntry
		return attributes;
	}

	public List<Tag> getTags() {
		return getRepeatingExtension(Tag.class);
	}

	/** Gets the gd:rating tag. */
	public Rating getRating() {
		return getExtension(Rating.class);
	}

	/** Sets the gd:rating tag. */
	public void setRating(Rating rating) {
		if (rating == null) {
			removeExtension(Rating.class);
		} else {
			setExtension(rating);
		}
	}

	public ElementHandler getHandler(ExtensionProfile extProfile,
			String namespace, String localName, Attributes attrs) {
		try {
			return new Handler(extProfile);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class Handler extends ExtensionPoint.ExtensionHandler {

		public Handler(ExtensionProfile extProfile) throws ParseException,
				IOException {
			super(extProfile, Subject.class);
		}

		public ElementHandler getChildHandler(String namespace,
				String localName, Attributes attrs) throws ParseException,
				IOException {
			if (localName.equals("id")) {
				return new IdHandler();
			} else if (localName.equals("title")) {
				TextConstruct.ChildHandlerInfo chi = TextConstruct
						.getChildHandler(attrs);
				if (state.title != null) {
					throw new ParseException("Duplicate title.");
				}
				state.title = chi.textConstruct;
				return chi.handler;
			} else if (localName.equals("link")) {
				Link link = new Link();
				state.links.add(link);
				return link.new AtomHandler(extProfile);
			} else if (localName.equals("author")) {
				Person author = new Person();
				state.authors.add(author);
				return author.new AtomHandler(extProfile);
			} else if (localName.equals("attribute")) {
				Attribute attribute = new Attribute();
				for (int i = 0; i < attrs.getLength(); ++i) {
					if (attrs.getLocalName(i) == "name")
						attribute.setName(attrs.getValue(i));
					else if (attrs.getLocalName(i) == "index")
						attribute.setIndex(attrs.getValue(i));
					else if (attrs.getLocalName(i) == "lang")
						attribute.setLang(attrs.getValue(i));
				}
				if (attributes == null) {
					attributes = new LinkedList<Attribute>();
				}
				attributes.add(attribute);

				return attribute.new AtomHandler(extProfile);

			} else {

				return super.getChildHandler(namespace, localName, attrs);

			}
		}

		class IdHandler extends XmlParser.ElementHandler {

			public void processEndElement() throws ParseException {

				if (state.id != null) {
					throw new ParseException("Duplicate entry ID.");
				}

				if (value == null) {
					throw new ParseException("ID must have a value.");
				}

				state.id = value;
			}
		}
	}

	public void generate(XmlWriter w, ExtensionProfile extProfile)
	throws IOException {


 generateStartElement(w, Namespaces.doubanNs, "subject", null, null);

    if (state.id != null) {
    	  w.simpleElement(null, "id", null, state.id);
    }

    // Invoke ExtensionPoint.
    generateExtensions(w, extProfile);

    w.endElement(Namespaces.doubanNs, "subject");
}
}