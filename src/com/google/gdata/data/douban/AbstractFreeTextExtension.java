package com.google.gdata.data.douban;

import com.google.gdata.data.AbstractExtension;
import com.google.gdata.data.AttributeGenerator;
import com.google.gdata.data.AttributeHelper;
import com.google.gdata.util.ParseException;

public abstract class AbstractFreeTextExtension extends AbstractExtension {
	  private String content;

	  /** Creates an empty tag. */
	  protected AbstractFreeTextExtension() {
	  }

	  /**
	   * Creates a tag and initializes its content.
	   *
	   * @param content
	   */
	  protected AbstractFreeTextExtension(String content) {
	    this.content = content;
	  }

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
	    super.putAttributes(generator);

	    if (content != null) {
	      generator.setContent(content);
	    }
	  }


	  @Override
	  protected void consumeAttributes(AttributeHelper helper)
	      throws ParseException {
	    super.consumeAttributes(helper);

	    content = helper.consumeContent(true);
	  }
	}

