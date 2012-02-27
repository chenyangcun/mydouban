package com.google.gdata.data.douban;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionProfile;

/**
 * User entry specific for a douban user. Contains setters and getters for all
 * fields specific to user data.
 */
@ExtensionDescription.Default(nsAlias = "", nsUri = "http://www.w3.org/2005/Atom", localName = "entry")
public class UserEntry extends BaseEntry<UserEntry> {

	public UserEntry() {
		super();
	}

	/**
	 * Constructs a new UserEntry by doing a shallow copy of data from an
	 * existing BaseEntry intance.
	 */
	public UserEntry(BaseEntry sourceEntry) {
		super(sourceEntry);
	}

	@Override
	public void declareExtensions(ExtensionProfile extProfile) {
		super.declareExtensions(extProfile);

		extProfile.declare(UserEntry.class, Location.class);
		extProfile.declare(UserEntry.class, Uid.class);
		extProfile.declareAdditionalNamespace(Namespaces.doubanNs);
	}

	/** Gets the plaintext user location. */
	public String getLocation() {
		Location loc = getExtension(Location.class);
		return loc == null ? null : loc.getContent();

	}

	/** Sets the plaintext user location. */
	public void setLocation(String location) {
		setExtension(new Location());
	}
	
	/** Gets the plaintext user uid. */
	public String getUid() {
		Uid uid = getExtension(Uid.class);
		return uid == null ? null : uid.getContent();

	}

	/** Sets the plaintext user uid. */
	public void setUid(String uid) {
		setExtension(new Uid(uid));
	}

}
