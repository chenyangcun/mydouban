package com.google.gdata.client.douban;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.HttpClientPool;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthHttpClient;

import org.apache.commons.httpclient.HttpClient;

import com.google.gdata.client.Query;
import com.google.gdata.client.Service;
import com.google.gdata.client.Query.CustomParameter;
import com.google.gdata.client.http.GoogleGDataRequest;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.MiniblogEntry;
import com.google.gdata.data.douban.MiniblogFeed;
import com.google.gdata.data.douban.Namespaces;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.NoteFeed;
import com.google.gdata.data.douban.ReviewEntry;
import com.google.gdata.data.douban.ReviewFeed;
import com.google.gdata.data.douban.Status;
import com.google.gdata.data.douban.Subject;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.SubjectFeed;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.douban.TagEntry;
import com.google.gdata.data.douban.TagFeed;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class DoubanService extends Service {

	protected String apiKey;
	protected String apiParam;
	protected String secret;
	protected boolean private_read;

	public static OAuthClient CLIENT = new OAuthHttpClient(
			new HttpClientPool() {
				// This trivial 'pool' simply allocates a new client every time.
				// More efficient implementations are possible.
				public HttpClient getHttpClient(URL server) {
					return new HttpClient();
				}
			});
	protected OAuthAccessor accessor;
	protected OAuthAccessor requestAccessor;
	protected List<Map.Entry<String, String>> parameters;
	protected OAuthConsumer client;

	static String requestTokenURL = "http://www.douban.com/service/auth/request_token";
	static String userAuthorizationURL = "http://www.douban.com/service/auth/authorize";
	static String accessTokenURL = "http://www.douban.com/service/auth/access_token";

	/**
	 * 构造豆瓣服务实例，仅支持只读操作
	 * 
	 * @param applicationName
	 *            应用名称
	 * @param apiKey
	 *            douban的api key
	 */
	public DoubanService(String applicationName, String apiKey) {
		ExtensionProfile profile = getExtensionProfile();
		this.apiKey = apiKey;
		this.apiParam = "apikey=" + apiKey;

		profile.addDeclarations(new UserEntry());
		profile.addDeclarations(new SubjectEntry());
		profile.addDeclarations(new ReviewEntry());
		profile.addDeclarations(new CollectionEntry());
		profile.addDeclarations(new TagEntry());
		profile.addDeclarations(new NoteEntry());
		profile.addDeclarations(new MiniblogEntry());

		requestFactory = new GoogleGDataRequest.Factory();
		this.accessor = null;

		if (applicationName != null) {
			requestFactory.setHeader("User-Agent", applicationName + " "
					+ getServiceVersion());
		} else {
			requestFactory.setHeader("User-Agent", getServiceVersion());
		}
		this.private_read = false;
	}

	/**
	 * 构造豆瓣服务实例，需要oauth授权，支持读写操作
	 * 
	 * @param applicationName
	 *            应用名称
	 * @param apiKey
	 *            douban的api key
	 * @param secret
	 *            douban的api私钥
	 */
	public DoubanService(String applicationName, String apiKey, String secret) {
		this(applicationName, apiKey);

		this.apiKey = apiKey;
		this.secret = secret;
		OAuthServiceProvider provider = new OAuthServiceProvider(
				requestTokenURL, userAuthorizationURL, accessTokenURL);

		this.client = new OAuthConsumer(null, apiKey, secret, provider);
		this.accessor = new OAuthAccessor(this.client);

		this.requestAccessor = new OAuthAccessor(this.client);

		this.client.setProperty("oauth_signature_method", OAuth.HMAC_SHA1);
		this.private_read = false;
	}

	/**
	 * 构造豆瓣服务实例，支持使用私钥验证的只读操作
	 * 
	 * @param applicationName
	 *            应用名称
	 * @param apiKey
	 *            douban的api key
	 * @param secret
	 *            douban的api私钥
	 * @param private_read
	 *            是否支持私钥验证的只读操作
	 */
	public DoubanService(String applicationName, String apiKey, String secret,
			boolean private_read) {
		this(applicationName, apiKey);

		this.apiKey = apiKey;
		this.secret = secret;
		OAuthServiceProvider provider = new OAuthServiceProvider(
				requestTokenURL, userAuthorizationURL, accessTokenURL);

		this.client = new OAuthConsumer(null, apiKey, secret, provider);
		this.accessor = new OAuthAccessor(this.client);

		this.requestAccessor = new OAuthAccessor(this.client);

		this.client.setProperty("oauth_signature_method", OAuth.HMAC_SHA1);
		this.private_read = private_read;
	}

	/**
	 * 获取授权的URL 需要授权的用户通过该URL进行授权，授权成功后跳转到callback指向的URL
	 * 
	 * @param callback
	 *            如果包含这个参数，认证成功后浏览器会被重定向到该URL
	 */
	public String getAuthorizationUrl(String callback) {
		String authorization_url = null;
		try {
			CLIENT.getRequestToken(this.requestAccessor);

			authorization_url = accessor.consumer.serviceProvider.userAuthorizationURL
					+ "?" + "oauth_token=" + this.requestAccessor.requestToken;
			if (callback != null)
				authorization_url += "&oauth_callback=" + callback;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authorization_url;
	}

	public String getRequestToken() {
		return this.requestAccessor.requestToken;
	}

	public void setRequestToken(String token) {
		this.requestAccessor.requestToken = token;
	}

	public String getRequestTokenSecret() {
		return this.requestAccessor.tokenSecret;
	}

	public void setRequestTokenSecret(String tokenSecret) {
		this.requestAccessor.tokenSecret = tokenSecret;
	}

	/**
	 * 设置访问token
	 * 
	 * 
	 * @param oauth_token
	 *            用户授权后得到的访问token
	 * @param oauth_token_secret
	 *            用户授权后得到的访问token的秘钥
	 */
	public ArrayList<String> setAccessToken(String oauth_token,
			String oauth_token_secret) {
		this.accessor.accessToken = oauth_token;
		this.accessor.tokenSecret = oauth_token_secret;
		ArrayList<String> tokens = new ArrayList<String>(2);
		tokens.add(this.accessor.accessToken);
		tokens.add(this.accessor.tokenSecret);
		return tokens;
	}

	/**
	 * 获取访问token
	 * 
	 */
	public ArrayList<String> getAccessToken() {
		OAuthMessage result;

		try {
			result = CLIENT.invoke(this.requestAccessor,
					accessor.consumer.serviceProvider.accessTokenURL, OAuth
							.newList("oauth_token",
									this.requestAccessor.requestToken));

			Map<String, String> responseParameters = OAuth.newMap(result
					.getParameters());

			this.accessor.accessToken = responseParameters.get("oauth_token");
			this.accessor.tokenSecret = responseParameters
					.get("oauth_token_secret");

			ArrayList<String> tokens = new ArrayList<String>(2);
			tokens.add(this.accessor.accessToken);
			tokens.add(this.accessor.tokenSecret);
			return tokens;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String toString(Object from) {
		return (from == null) ? null : from.toString();
	}

	@SuppressWarnings("unchecked")
	public GDataRequest createFeedRequest(Query query) throws IOException,
			ServiceException {
		GDataRequest request = null;
		OAuthMessage oauthRequest = null;
		setTimeouts(request);

		if (this.accessor == null) {
			List<CustomParameter> customParams = query.getCustomParameters();
			customParams.add(new CustomParameter("apikey", this.apiKey));
			request = super.requestFactory.getRequest(query, super
					.getContentType());
			return request;
		}

		request = super.requestFactory
				.getRequest(query, super.getContentType());
		try {
			Collection<Map.Entry<String, String>> p = new ArrayList<Map.Entry<String, String>>();

			p.add(new OAuth.Parameter("oauth_version", "1.0"));
			String methodType = "GET";
			GDataRequest.RequestType type = GDataRequest.RequestType.QUERY;

			switch (type) {

			case INSERT:
				methodType = "POST";
				break;
			case UPDATE:
				methodType = "PUT";
				break;

			case DELETE:
				methodType = "DELETE";
				break;

			}
			if (this.private_read == true) {
				oauthRequest = this.requestAccessor.newRequestMessage(
						methodType, query.getUrl().toString(), p);
			} else {
				oauthRequest = this.accessor.newRequestMessage(methodType,
						query.getUrl().toString(), p);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters = oauthRequest.getParameters();

		String url = "OAuth realm=\"\"";

		for (Map.Entry parameter : parameters) {
			url += ", ";
			url += OAuth.percentEncode(toString(parameter.getKey()));
			url += "=\"";
			url += OAuth.percentEncode(toString(parameter.getValue()));
			url += "\"";
		}

		request.setHeader("Authorization", url);
		return request;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GDataRequest createRequest(GDataRequest.RequestType type,
			URL requestUrl, ContentType contentType) throws IOException,
			ServiceException {
		GDataRequest request = null;
		OAuthMessage oauthRequest = null;

		if (this.accessor == null) {
			String url = requestUrl.toString();
			if (url.indexOf('?') == -1) {
				url = url + "?" + this.apiParam;
			} else {
				url = url + "&" + this.apiParam;
			}
			request = super.createRequest(type, new URL(url), contentType);
			return request;
		}

		request = super.createRequest(type, requestUrl, contentType);
		try {
			Collection<Map.Entry<String, String>> p = new ArrayList<Map.Entry<String, String>>();

			p.add(new OAuth.Parameter("oauth_version", "1.0"));
			String methodType = "GET";

			switch (type) {

			case INSERT:
				methodType = "POST";
				break;
			case UPDATE:
				methodType = "PUT";
				break;

			case DELETE:
				methodType = "DELETE";
				break;
			}

			if (this.private_read == true) {
				oauthRequest = this.requestAccessor.newRequestMessage(
						methodType, requestUrl.toString(), p);
			} else {
				oauthRequest = this.accessor.newRequestMessage(methodType,
						requestUrl.toString(), p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters = oauthRequest.getParameters();

		String url = "OAuth realm=\"\"";

		for (Map.Entry parameter : parameters) {
			url += ", ";
			url += OAuth.percentEncode(toString(parameter.getKey()));
			url += "=\"";
			url += OAuth.percentEncode(toString(parameter.getValue()));
			url += "\"";
		}

		request.setHeader("Authorization", url);
		return request;
	}

	public <E extends BaseEntry<?>> E getEntry(String entryUrl,
			Class<E> entryClass) throws IOException, ServiceException {
		return super.getEntry(new URL(entryUrl), entryClass);
	}

	/**
	 * 得到用户信息
	 * 
	 * @param userId
	 *            用户的id字符串
	 */
	public UserEntry getUser(String userId) throws IOException,
			ServiceException {
		String url = Namespaces.userURL + "/" + userId;
		return getEntry(url, UserEntry.class);
	}

	public UserEntry getAuthorizedUser() throws IOException, ServiceException {
		String url = Namespaces.userURL + "/%40me";
		return getEntry(url, UserEntry.class);
	}

	/**
	 * 查找用户信息
	 * 
	 * @param q
	 *            查询关键字
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public UserFeed findUser(String q, int startIndex, int maxResult)
			throws IOException, ServiceException {
		DoubanQuery query = new DoubanQuery(new URL(Namespaces.userURL));
		query.setFullTextQuery(q);
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, UserFeed.class);
	}

	/**
	 * 获得用户的朋友信息
	 * 
	 * @param userId
	 *            要查询的用户id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public UserFeed getUserFriends(String userId, int startIndex, int maxResult)
			throws IOException, ServiceException {
		String url = Namespaces.userURL + "/" + userId + "/friends";
		DoubanQuery query = new DoubanQuery(new URL(url));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		return getFeed(query, UserFeed.class);
	}

	/**
	 * 获得图书条目信息
	 * 
	 * @param bookId
	 *            要查询的图书id字符串
	 */
	public SubjectEntry getBook(String bookId) throws IOException,
			ServiceException {
		String url = bookId;
		if (url.lastIndexOf("http") != 0) {
			url = Namespaces.bookSubjectURL + "/" + bookId;
		}
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得图书条目信息
	 * 
	 * @param bookId
	 *            要查询的图书id数值
	 */
	public SubjectEntry getBook(int bookId) throws IOException,
			ServiceException {
		String url = Namespaces.bookSubjectURL + "/" + bookId;
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得音乐条目信息
	 * 
	 * @param musicId
	 *            要查询的音乐id字符串
	 */
	public SubjectEntry getMusic(String musicId) throws IOException,
			ServiceException {
		String url = musicId;
		if (url.lastIndexOf("http") != 0) {
			url = Namespaces.musicSubjectURL + "/" + musicId;
		}
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得音乐条目信息
	 * 
	 * @param musicId
	 *            要查询的音乐id数值
	 */
	public SubjectEntry getMusic(int musicId) throws IOException,
			ServiceException {
		String url = Namespaces.musicSubjectURL + "/" + musicId;
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得电影条目信息
	 * 
	 * @param movieId
	 *            要查询的电影id字符串
	 */
	public SubjectEntry getMovie(String movieId) throws IOException,
			ServiceException {
		String url = movieId;
		if (url.lastIndexOf("http") != 0) {
			url = Namespaces.movieSubjectURL + "/" + movieId;
		}
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得电影条目信息
	 * 
	 * @param movieId
	 *            要查询的电影id数值
	 */
	public SubjectEntry getMovie(int movieId) throws IOException,
			ServiceException {
		String url = Namespaces.movieSubjectURL + "/" + movieId;
		return getEntry(url, SubjectEntry.class);
	}

	/**
	 * 获得日记信息
	 * 
	 * @param noteId
	 *            要查询的日记id字符串
	 */
	public NoteEntry getNote(String noteId) throws IOException,
			ServiceException {
		String url = Namespaces.noteURL + "/" + noteId;
		return getEntry(url, NoteEntry.class);
	}

	/**
	 * 获得用户的日记信息
	 * 
	 * @param userId
	 *            要查询的用户id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public NoteFeed getUserNotes(String userId, int startIndex, int maxResult)
			throws IOException, ServiceException {
		String url = Namespaces.userURL + "/" + userId + "/notes";
		DoubanQuery query = new DoubanQuery(new URL(url));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, NoteFeed.class);
	}

	/**
	 * 创建日记
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param privacy
	 *            隐私设置（public|private|friend）
	 * @param can_reply
	 *            是否可以回复（yes|no）
	 */
	public NoteEntry createNote(TextConstruct title, TextConstruct content,
			String privacy, String can_reply) throws IOException,
			ServiceException {
		String url = Namespaces.noteCreateURL;
		NoteEntry ne = new NoteEntry();

		if (title != null) {
			ne.setTitle(title);
		}
		if (content != null) {
			ne.setContent(content);
		}
		ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
		Attribute a1 = new Attribute();
		a1.setName("privacy");
		a1.setContent(privacy);
		Attribute a2 = new Attribute();
		a2.setName("can_reply");
		a2.setContent(can_reply);
		atts.add(a1);
		atts.add(a2);
		ne.setAttributes(atts);

		return insert(new URL(url), ne);
	}

	/**
	 * 更新日记
	 * 
	 * @param ne
	 *            更新的日记条目
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param privacy
	 *            隐私设置（public|private|friend）
	 * @param can_reply
	 *            是否可以回复（yes|no）
	 */
	public NoteEntry updateNote(NoteEntry ne, TextConstruct title,
			TextConstruct content, String privacy, String can_reply)
			throws MalformedURLException, IOException, ServiceException {

		if (title != null) {
			ne.setTitle(title);
		}
		if (content != null) {
			ne.setContent(content);
		}
		ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
		Attribute a1 = new Attribute();
		a1.setName("privacy");
		a1.setContent(privacy);
		Attribute a2 = new Attribute();
		a2.setName("can_reply");
		a2.setContent(can_reply);
		atts.add(a1);
		atts.add(a2);
		ne.setAttributes(atts);

		return update(new URL(ne.getId()), ne);
	}

	/**
	 * 删除日记
	 * 
	 * @param ne
	 *            删除的日记条目
	 */
	public void deleteNote(NoteEntry ne) throws MalformedURLException,
			IOException, ServiceException {
		delete(new URL(ne.getId()));
	}

	/**
	 * 获得用户的迷你博客信息
	 * 
	 * @param userId
	 *            要查询的用户id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public MiniblogFeed getUserMiniblogs(String userId, int startIndex,
			int maxResult) throws IOException, ServiceException {
		String url = Namespaces.userURL + "/" + userId + "/miniblog";
		DoubanQuery query = new DoubanQuery(new URL(url));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, MiniblogFeed.class);
	}

	/**
	 * 获得友邻的迷你博客信息
	 * 
	 * @param userId
	 *            要查询的用户id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public MiniblogFeed getContactsMiniblogs(String userId, int startIndex,
			int maxResult) throws IOException, ServiceException {
		String url = Namespaces.userURL + "/" + userId + "/miniblog/contacts";
		DoubanQuery query = new DoubanQuery(new URL(url));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, MiniblogFeed.class);
	}

	/**
	 * 创建我说
	 * 
	 * @param content
	 *            我说的内容
	 */
	public MiniblogEntry createSaying(TextConstruct content)
			throws IOException, ServiceException {
		String url = Namespaces.sayingCreateURL;
		MiniblogEntry me = new MiniblogEntry();

		if (content != null) {
			me.setContent(content);
		}

		return insert(new URL(url), me);
	}

	/**
	 * 删除我说
	 * 
	 * @param me
	 *            删除的我说条目
	 */
	public void deleteMiniblog(MiniblogEntry me) throws MalformedURLException,
			IOException, ServiceException {
		delete(new URL(me.getId()));
	}

	/**
	 * 查找图书
	 * 
	 * @param q
	 *            查询关键字
	 * @param tag
	 *            标记的tag
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed findBook(String q, String tag, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(
				Namespaces.bookSubjectsURL));
		query.setFullTextQuery(q);
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setTag(tag);

		return query(query, SubjectFeed.class);
	}

	/**
	 * 查找电影
	 * 
	 * @param q
	 *            查询关键字
	 * @param tag
	 *            标记的tag
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed findMovie(String q, String tag, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(
				Namespaces.movieSubjectsURL));
		query.setFullTextQuery(q);
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setTag(tag);

		return query(query, SubjectFeed.class);
	}

	/**
	 * 查找音乐
	 * 
	 * @param q
	 *            查询关键字
	 * @param tag
	 *            标记的tag
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed findMusic(String q, String tag, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(
				Namespaces.musicSubjectsURL));
		query.setFullTextQuery(q);
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setTag(tag);

		return query(query, SubjectFeed.class);
	}

	/**
	 * @deprecated 受限制的api，如果需要使用请联系douban开发团队 得到图书的相关条目信息
	 * 
	 * @param bookId
	 *            图书id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed getBookRelated(String bookId, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(Namespaces.bookSubjectURL
				+ "/" + bookId + "/related"));

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, SubjectFeed.class);
	}

	/**
	 * @deprecated 受限制的api，如果需要使用请联系douban开发团队 得到电影的相关条目信息
	 * 
	 * @param movieId
	 *            电影id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed getMovieRelated(String movieId, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(
				Namespaces.movieSubjectURL + "/" + movieId + "/related"));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, SubjectFeed.class);

	}

	/**
	 * @deprecated 受限制的api，如果需要使用请联系douban开发团队 得到音乐的相关条目信息
	 * 
	 * @param musicId
	 *            音乐id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public SubjectFeed getMusicRelated(String musicId, int startIndex,
			int maxResult) throws IOException, ServiceException {
		SubjectQuery query = new SubjectQuery(new URL(
				Namespaces.musicSubjectURL + "/" + musicId + "/related"));

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, SubjectFeed.class);

	}

	/**
	 * 得到评论信息
	 * 
	 * @param reviewId
	 *            评论id
	 * 
	 */
	public ReviewEntry getReview(String reviewId) throws IOException,
			ServiceException {
		String url = Namespaces.reviewURL + "/" + reviewId;
		return getEntry(url, ReviewEntry.class);
	}

	/**
	 * 获得用户的评论信息
	 * 
	 * @param userId
	 *            要查询的用户id
	 */
	public ReviewFeed getUserReviews(String userId) throws IOException,
			ServiceException {
		String url = Namespaces.userURL + "/" + userId + "/reviews";
		DoubanQuery query = new DoubanQuery(new URL(url));

		return getFeed(query, ReviewFeed.class);
	}

	/**
	 * 获得图书的评论信息
	 * 
	 * @param bookId
	 *            要查询的图书id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public ReviewFeed getBookReviews(String bookId, int startIndex,
			int maxResult, String orderby) throws IOException, ServiceException {
		ReviewQuery query = new ReviewQuery(new URL(Namespaces.bookSubjectURL
				+ "/" + bookId + "/reviews"));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setOrderby(orderby);

		return query(query, ReviewFeed.class);
	}

	/**
	 * 获得电影的评论信息
	 * 
	 * @param movieId
	 *            要查询的电影id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public ReviewFeed getMovieReviews(String movieId, int startIndex,
			int maxResult, String orderby) throws IOException, ServiceException {
		ReviewQuery query = new ReviewQuery(new URL(Namespaces.movieSubjectURL
				+ "/" + movieId + "/reviews"));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setOrderby(orderby);
		return query(query, ReviewFeed.class);
	}

	/**
	 * 获得音乐的评论信息
	 * 
	 * @param musicId
	 *            要查询的音乐id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public ReviewFeed getMusicReviews(String musicId, int startIndex,
			int maxResult, String orderby) throws IOException, ServiceException {
		ReviewQuery query = new ReviewQuery(new URL(Namespaces.musicSubjectURL
				+ "/" + musicId + "/reviews"));
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setOrderby(orderby);
		return query(query, ReviewFeed.class);
	}

	/**
	 * 获得用户的收藏信息
	 * 
	 * @param userId
	 *            用户id
	 * @param cat
	 *            类别
	 * @param tag
	 *            标记的tag
	 * @param status
	 *            收藏的状态
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public CollectionFeed getUserCollections(String userId, String cat,
			String tag, String status, int startIndex, int maxResult)
			throws IOException, ServiceException {

		CollectionQuery query = new CollectionQuery(new URL(Namespaces.userURL
				+ "/" + userId + "/collection"));
		query.setCat(cat);
		query.setTag(tag);
		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		query.setStatus(status);

		return query(query, CollectionFeed.class);
	}

	/**
	 * 获得特定的收藏信息
	 * 
	 * @param cid
	 *            收藏id
	 */
	public CollectionEntry getCollection(String cid) throws IOException,
			ServiceException {

		String url = Namespaces.collectionURL + "/" + cid;
		return getEntry(url, CollectionEntry.class);
	}

	/**
	 * 获取图书的标签信息
	 * 
	 * @param bookId
	 *            图书Id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public TagFeed getBookTags(String bookId, int startIndex, int maxResult)
			throws IOException, ServiceException {
		TagQuery query = new TagQuery(new URL(Namespaces.bookSubjectURL + "/"
				+ bookId + "/tags"));

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, TagFeed.class);
	}

	/**
	 * 获取电影的标签信息
	 * 
	 * @param movieId
	 *            电影Id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public TagFeed getMovieTags(String movieId, int startIndex, int maxResult)
			throws IOException, ServiceException {
		TagQuery query = new TagQuery(new URL(Namespaces.movieSubjectURL + "/"
				+ movieId + "/tags"));

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, TagFeed.class);
	}

	/**
	 * 获取音乐的标签信息
	 * 
	 * @param musicId
	 *            音乐Id
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public TagFeed getMusicTags(String musicId, int startIndex, int maxResult)
			throws IOException, ServiceException {
		TagQuery query = new TagQuery(new URL(Namespaces.musicSubjectURL + "/"
				+ musicId + "/tags"));

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);

		return query(query, TagFeed.class);
	}

	/**
	 * 获取用户的标签信息
	 * 
	 * @param userId
	 *            用户Id
	 * @param cat
	 *            类别（movie|music|book ）
	 * @param startIndex
	 *            开始索引
	 * @param maxResult
	 *            最大返回结果数目
	 */
	public TagFeed getUserTags(String userId, String cat, int startIndex,
			int maxResult) throws IOException, ServiceException {
		TagQuery query = new TagQuery(new URL(Namespaces.userURL + "/" + userId
				+ "/tags"));
		query.setCat(cat);

		query.setStartIndex(startIndex);
		query.setMaxResults(maxResult);
		return query(query, TagFeed.class);
	}

	/**
	 * 为一个条目创建评论
	 * 
	 * @param subjectEntry
	 *            要创建评论的条目
	 * @param title
	 *            评论的标题
	 * @param content
	 *            内容（至少50字）
	 * @param rating
	 *            对条目的打分(1-5)
	 */
	public ReviewEntry createReview(SubjectEntry subjectEntry,
			TextConstruct title, TextConstruct content, Rating rating)
			throws IOException, ServiceException {
		String url = Namespaces.reviewCreateURL;
		ReviewEntry re = new ReviewEntry();
		Subject subject = new Subject();

		subject.setId(subjectEntry.getId());
		re.setSubject(subject);
		if (title != null) {
			re.setTitle(title);
		}
		if (content != null) {
			re.setContent(content);
		}
		if (rating != null) {
			re.setRating(rating);
		}

		return insert(new URL(url), re);
	}

	/**
	 * 更新一个评论
	 * 
	 * @param reviewEntry
	 *            待更新的评论
	 * @param title
	 *            标题
	 * @param content
	 *            评论内容（至少50字）
	 * @param rating
	 *            评分（1-5）
	 */
	public ReviewEntry updateReview(ReviewEntry reviewEntry,
			TextConstruct title, TextConstruct content, Rating rating)
			throws IOException, ServiceException {
		ReviewEntry re = new ReviewEntry(reviewEntry);
		if (title != null) {
			re.setTitle(title);
		}
		if (content != null) {
			re.setContent(content);
		}
		if (rating != null) {
			re.setRating(rating);
		}

		return update(new URL(reviewEntry.getId()), re);
	}

	/**
	 * 删除一条评论
	 * 
	 * @param reviewEntry
	 *            待删除的评论
	 */
	public void deleteReview(ReviewEntry reviewEntry) throws IOException,
			ServiceException {
		delete(new URL(reviewEntry.getId()));
	}

	/**
	 * 创建一个条目的收藏
	 * 
	 * @param status
	 *            待收藏条目的状态（book:wish|reading|read movie:wish|watched
	 *            tv:wish|watching|watched music:wish|listening|listened）
	 * @param se
	 *            被搜藏的条目
	 * @param tags
	 *            收藏的标签
	 * @param rating
	 *            对条目的打分(1-5)
	 */
	public CollectionEntry createCollection(Status status, SubjectEntry se,
			List<Tag> tags, Rating rating) throws MalformedURLException,
			IOException, ServiceException {
		String url = Namespaces.collectionCreateURL;
		CollectionEntry ceNew = new CollectionEntry();
		Subject subject = new Subject();
		subject.setId(se.getId());
		ceNew.setSubjectEntry(subject);

		if (status != null) {
			ceNew.setStatus(status);
		}
		if (tags != null) {
			ceNew.setTags(tags);
		}
		if (rating != null) {
			ceNew.setRating(rating);
		}

		return insert(new URL(url), ceNew);
	}

	/**
	 * 更新收藏
	 * 
	 * @param ce
	 *            要更新的收藏
	 * @param status
	 *            待收藏条目的状态（book:wish|reading|read movie:wish|watched
	 *            tv:wish|watching|watched music:wish|listening|listened）
	 * @param tags
	 *            收藏的标签
	 * @param rating
	 *            对条目的打分(1-5)
	 */
	public CollectionEntry updateCollection(CollectionEntry ce, Status status,
			List<Tag> tags, Rating rating) throws MalformedURLException,
			IOException, ServiceException {

		CollectionEntry ceNew = new CollectionEntry();
		ceNew.setId(ce.getId());

		Subject subject = new Subject();
		subject.setId(ce.getSubjectEntry().getId());
		ceNew.setSubjectEntry(subject);

		if (status != null) {
			ceNew.setStatus(status);
		} else {
			ceNew.setStatus(ce.getStatus());
		}
		if (tags != null) {
			ceNew.setTags(tags);
		} else {
			ceNew.setTags(ce.getTags());
		}
		if (rating != null) {
			ceNew.setRating(rating);
		} else {
			ceNew.setRating(ce.getRating());
		}
		return update(new URL(ce.getId()), ceNew);
	}

	/**
	 * 删除一条收藏
	 * 
	 * @param ce
	 *            待删除的收藏
	 */
	public void deleteCollection(CollectionEntry ce)
			throws MalformedURLException, IOException, ServiceException {
		delete(new URL(ce.getId()));
	}

}