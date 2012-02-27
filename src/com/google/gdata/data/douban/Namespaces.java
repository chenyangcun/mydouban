package com.google.gdata.data.douban;

import com.google.gdata.util.common.xml.XmlWriter;

public class Namespaces {


  public static final String userURL = "http://api.douban.com/people";
  public static final String userURLSlash = "http://api.douban.com/people/";
  public static final String bookSubjectURL = "http://api.douban.com/book/subject";
  public static final String musicSubjectURL = "http://api.douban.com/music/subject";
  public static final String movieSubjectURL = "http://api.douban.com/movie/subject";
  public static final String collectionURL = "http://api.douban.com/collection";
  public static final String bookSubjectsURL = "http://api.douban.com/book/subjects";
  
  public static final String noteURL = "http://api.douban.com/note";
  public static final String musicSubjectsURL = "http://api.douban.com/music/subjects";
  public static final String movieSubjectsURL = "http://api.douban.com/movie/subjects";
  public static final String reviewURL = "http://api.douban.com/review";
  public static final String reviewCreateURL = "http://api.douban.com/reviews";
  public static final String noteCreateURL = "http://api.douban.com/notes";
  public static final String sayingCreateURL = "http://api.douban.com/miniblog/saying";

  
  public static final String doubanNamespace = "http://www.douban.com/xmlns/";
  public static final String doubanAPI = "http://api.douban.com/2007";
  
  public static final String doubanAlias = "db";

  
 
  /** Google data (GD) namespace */
  public static final String g = "http://schemas.google.com/g/2005";
  public static final String gPrefix = g + "#";
  public static final String gAlias = "gd";
  
  public static final String atomAlias = "atom";
  public static final String atomNamespace = "http://www.w3.org/2005/Atom";
  
  public static final String gKind = doubanAPI + "#" + "kind";

  /** Google data XML writer namespace. */
  public static final XmlWriter.Namespace gNs =
    new XmlWriter.Namespace(gAlias, g);
  
    
  public static final XmlWriter.Namespace doubanNs =
      new XmlWriter.Namespace(doubanAlias, doubanNamespace);
  
  public static final XmlWriter.Namespace atomNS =
	  new XmlWriter.Namespace(atomAlias, atomNamespace);
public static final String collectionCreateURL = "http://api.douban.com/collection";

  
  private Namespaces() {
  }

}
