package com.chenyc.douban;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.chenyc.douban.adapter.SubjectListAdapter;
import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.ConvertUtil;
import com.chenyc.douban.util.NetUtil;
import com.google.gdata.data.douban.SubjectFeed;

public class SearchActivity extends BaseActivity {
	private List<Subject> books = new ArrayList<Subject>();
	private List<Subject> movies = new ArrayList<Subject>();
	private List<Subject> musics = new ArrayList<Subject>();

	private ViewFlipper viewFlipper;

	private GestureDetector mGestureDetector;

	private static final int SWIPE_MAX_OFF_PATH = 100;

	private static final int SWIPE_MIN_DISTANCE = 100;

	private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	private int bookIndex = 1;
	private int movieIndex = 1;
	private int musicIndex = 1;
	private int count = 10; // 每次获取数目
	private boolean isFilling = false; // 判断是否正在获取数据
	protected SubjectListAdapter bookListAdapter;
	protected SubjectListAdapter movieListAdapter;
	protected SubjectListAdapter musicListAdapter;

	private int bookTotal; // 最大条目数
	private int movieTotal; // 最大条目数
	private int musicTotal; // 最大条目数

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.search);

		initView(R.id.search_book, R.string.book_search_hint, "book");
		initView(R.id.search_movie, R.string.movie_search_hint, "movie");
		initView(R.id.search_music, R.string.music_search_hint, "music");

		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

		mGestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
							return false;

						if ((e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
								&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
							viewFlipper.setInAnimation(AnimationUtils
									.loadAnimation(SearchActivity.this,
											R.anim.push_right_in));
							viewFlipper.setOutAnimation(AnimationUtils
									.loadAnimation(SearchActivity.this,
											R.anim.push_left_out));
							viewFlipper.showNext();
						} else if ((e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE
								&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
							viewFlipper.setInAnimation(AnimationUtils
									.loadAnimation(SearchActivity.this,
											R.anim.push_left_in));
							viewFlipper.setOutAnimation(AnimationUtils
									.loadAnimation(SearchActivity.this,
											R.anim.push_right_out));
							viewFlipper.showPrevious();
						}
						return true;
					}
				});

	}

	private void initView(int layoutId, int hintId, final String cat) {
		final View searchView = findViewById(layoutId);
		EditText searchText = (EditText) searchView
				.findViewById(R.id.search_text);
		searchText.setHint(hintId);
		ImageButton searchButton = (ImageButton) searchView
				.findViewById(R.id.search_button);
		searchView.setTag(cat);

		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				doSearch(searchView, cat);
			}
		});

		TextView titleView = (TextView) searchView.findViewById(R.id.myTitle);
		if (Subject.BOOK.equals(cat)) {
			titleView.setText("图书搜索");
		} else if (Subject.MOVIE.equals(cat)) {
			titleView.setText("电影搜索");
		} else if (Subject.MUSIC.equals(cat)) {
			titleView.setText("音乐搜索");
		}

		ImageButton prevButton = (ImageButton) searchView
				.findViewById(R.id.prev_button);

		prevButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(
						SearchActivity.this, R.anim.push_left_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
						SearchActivity.this, R.anim.push_right_out));
				viewFlipper.showPrevious();
			}
		});

		ImageButton nextButton = (ImageButton) searchView
				.findViewById(R.id.next_button);

		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(
						SearchActivity.this, R.anim.push_right_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
						SearchActivity.this, R.anim.push_left_out));
				viewFlipper.showNext();
			}

		});

		ImageButton backButton = (ImageButton) searchView
				.findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doExit();
			}
		});

		ListView listView = (ListView) searchView
				.findViewById(android.R.id.list);
		if (Subject.BOOK.equals(cat)) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i = new Intent(SearchActivity.this,
							SubjectViewActivity.class);
					Subject subject = books.get(position);
					i.putExtra("subject", subject);
					startActivity(i);

				}
			});
		} else if (Subject.MOVIE.equals(cat)) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i = new Intent(SearchActivity.this,
							SubjectViewActivity.class);
					Subject subject = movies.get(position);
					i.putExtra("subject", subject);
					startActivity(i);

				}
			});
		} else if (Subject.MUSIC.equals(cat)) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i = new Intent(SearchActivity.this,
							SubjectViewActivity.class);
					Subject subject = musics.get(position);
					i.putExtra("subject", subject);
					startActivity(i);

				}
			});
		}

		listView.setOnScrollListener(new OnScrollListener() {

			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						loadRemnantListItem(searchView);
					}
				}
			}
		});

	}

	// 获取更多条目
	private void loadRemnantListItem(View searchView) {
		if (isFilling) {
			return;
		}
		String cat = (String) searchView.getTag();
		if (Subject.BOOK.equals(cat)) {
			bookIndex = bookIndex + count;
			if (bookIndex > bookTotal) {
				return;
			}
		} else if (Subject.MOVIE.equals(cat)) {
			movieIndex = movieIndex + count;
			if (movieIndex > movieTotal) {
				return;
			}
		} else if (Subject.MUSIC.equals(cat)) {
			musicIndex = musicIndex + count;
			if (musicIndex > musicTotal) {
				return;
			}
		}
		RelativeLayout loading = (RelativeLayout) searchView
				.findViewById(R.id.loading);
		LayoutParams lp = (LayoutParams) loading.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		loading.setLayoutParams(lp);
		
		fillData(searchView, cat);
	}

	// 在这里先处理下你的手势左右滑动事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean flag = mGestureDetector.onTouchEvent(ev);
		if (!flag) {
			flag = super.dispatchTouchEvent(ev);
		}
		return flag;
	}

	private void doSearch(final View searchView, final String cat) {
		EditText searchText = (EditText) searchView
				.findViewById(R.id.search_text);

		String searchTitle = searchText.getText().toString();
		if ("".equals(searchTitle.trim())) {
			return;
		}
		if (Subject.BOOK.equals(cat)) {
			bookIndex = 1;
			bookListAdapter = null;
			books.clear();
		} else if (Subject.MOVIE.equals(cat)) {
			movieIndex = 1;
			movieListAdapter = null;
			movies.clear();
		} else if (Subject.MUSIC.equals(cat)) {
			musicIndex = 1;
			musicListAdapter = null;
			musics.clear();
		}
		fillData(searchView, cat);
	}

	private void fillData(final View searchView, final String cat) {
		new AsyncTask<View, Void, SubjectFeed>() {

			@Override
			protected SubjectFeed doInBackground(View... args) {
				View searchView = args[0];
				String cat = (String) searchView.getTag();
				EditText searchText = (EditText) searchView
						.findViewById(R.id.search_text);

				String title = searchText.getText().toString();
				SubjectFeed feed = null;
				try {
					if (Subject.BOOK.equals(cat)) {
						feed = NetUtil.getDoubanService().findBook(title, "",
								bookIndex, count);
						bookTotal = feed.getTotalResults();
					} else if (Subject.MOVIE.equals(cat)) {
						feed = NetUtil.getDoubanService().findMovie(title, "",
								movieIndex, count);
						movieTotal = feed.getTotalResults();
					} else if (Subject.MUSIC.equals(cat)) {
						feed = NetUtil.getDoubanService().findMusic(title, "",
								musicIndex, count);
						musicTotal = feed.getTotalResults();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return feed;
			}

			@Override
			protected void onPostExecute(SubjectFeed result) {
				super.onPostExecute(result);
				closeProgressBar(searchView);
				if (result != null) {
					ListView listView = (ListView) searchView
							.findViewById(android.R.id.list);
					if (Subject.BOOK.equals(cat)) {
						books.addAll(ConvertUtil.ConvertSubjects(result, cat));
						if (bookListAdapter == null) {
							bookListAdapter = new SubjectListAdapter(
									SearchActivity.this, listView, books);
							listView.setAdapter(bookListAdapter);
						} else {
							bookListAdapter.notifyDataSetChanged();
						}
					} else if (Subject.MOVIE.equals(cat)) {
						movies.addAll(ConvertUtil.ConvertSubjects(result, cat));
						if (movieListAdapter == null) {
							movieListAdapter = new SubjectListAdapter(
									SearchActivity.this, listView, movies);
							listView.setAdapter(movieListAdapter);
						} else {
							movieListAdapter.notifyDataSetChanged();
						}
					} else if (Subject.MUSIC.equals(cat)) {
						musics.addAll(ConvertUtil.ConvertSubjects(result, cat));
						if (musicListAdapter == null) {
							musicListAdapter = new SubjectListAdapter(
									SearchActivity.this, listView, musics);
							listView.setAdapter(musicListAdapter);
						} else {
							musicListAdapter.notifyDataSetChanged();
						}
					}

				}
				isFilling = false;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				isFilling = true;
				showProgressBar(searchView);

			}

		}.execute(searchView);
	}

	public void showProgressBar(View view) {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		RelativeLayout loading = (RelativeLayout) view
				.findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);
		loading.setLayoutAnimation(controller);
	}

	public void closeProgressBar(View view) {

		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		RelativeLayout loading = (RelativeLayout) view
				.findViewById(R.id.loading);

		loading.setLayoutAnimation(controller);

		loading.setVisibility(View.INVISIBLE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doExit();
			return true;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

}
