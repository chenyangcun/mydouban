package com.chenyc.douban;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.layout;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.RelativeLayout.LayoutParams;

import com.chenyc.douban.adapter.SubjectListAdapter;
import com.chenyc.douban.entity.Review;
import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.ConvertUtil;
import com.chenyc.douban.util.NetUtil;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.ReviewEntry;
import com.google.gdata.data.douban.ReviewFeed;
import com.google.gdata.data.douban.UserEntry;

/**
 * 评论列表
 * 
 * @author chenyc
 * 
 */
public class ReviewActivity extends BaseListActivity {

	private static final String ORDERBY = "score";// 按评分排序
	private List<Review> reviews = new ArrayList<Review>();
	private ReviewListAdapter listAdapter;
	private int index = 1;
	private int count = 10; // 每次获取数目
	private int total; // 最大条目数
	private boolean isFilling = false; // 判断是否正在获取数据
	private boolean myReview;
	private boolean bestReview;

	private static final int DELETE_ID = 0x000002;
	private static final int EDIT_ID = 0x000003;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review);

		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		

		Bundle extras = getIntent().getExtras();
		myReview = extras != null ? extras.getBoolean("my_review")
				: false;
		bestReview = extras != null ? extras.getBoolean("best_review")
				: false;
		if(myReview){
			registerForContextMenu(getListView());
		}
		TextView titleView = (TextView) findViewById(R.id.myTitle);
		if (myReview) {
			String title = "我的评论";
			titleView.setText(title);
			fillMyReview();
		} else if (bestReview) {
			String title = "豆瓣最受欢迎的书评";
			titleView.setText(title);
			fillBestReview();

			getListView().setOnScrollListener(new OnScrollListener() {
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

				}

				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						// 判断滚动到底部
						if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
							loadMoreNewBestReview();
						}
					}
				}

			});

		} else {
			final Subject subject = extras != null ? (Subject) extras
					.getSerializable("subject") : null;
			String title = "《" + subject.getTitle() + "》的评论";
			titleView.setText(title);
			fillDataBySubject(subject);
			getListView().setOnScrollListener(new OnScrollListener() {
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

				}

				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						// 判断滚动到底部
						if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
							loadRemnantListItem(subject);
						}
					}
				}

			});
		}
	}

	// 加载更多最受欢迎书评
	protected void loadMoreNewBestReview() {
		total = 50;
		if (isFilling) {
			return;
		}
		index = index + count;
		if (index > total) {
			return;
		}
		RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);
		LayoutParams lp = (LayoutParams) loading.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		loading.setLayoutParams(lp);
		fillBestReview();
	}

	// 获取最受欢迎的书评
	private void fillBestReview() {
		new AsyncTask<Void, Void, List<Review>>() {

			@Override
			protected List<Review> doInBackground(Void... arg0) {
				return NetUtil.getBestReviews(index);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar();
				isFilling = true;
			}

			@Override
			protected void onPostExecute(List<Review> newReviews) {
				super.onPostExecute(newReviews);
				closeProgressBar();
				if (newReviews != null) {
					reviews.addAll(newReviews);
					if (listAdapter == null) {
						listAdapter = new ReviewListAdapter(
								ReviewActivity.this, getListView(), reviews);
						setListAdapter(listAdapter);
					} else {
						listAdapter.notifyDataSetChanged();
					}

					if (reviews.size() == 0) {
						Toast.makeText(ReviewActivity.this, "没有找到相关评论！",
								Toast.LENGTH_SHORT);
					}

				} else {
					Toast.makeText(ReviewActivity.this, "数据加载失败！",
							Toast.LENGTH_SHORT);
				}
				isFilling = false;
			}
		}.execute();
	}

	// 加载更多评论
	private void loadRemnantListItem(Subject subject) {
		if (isFilling) {
			return;
		}
		index = index + count;
		if (index > total) {
			return;
		}
		RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);
		LayoutParams lp = (LayoutParams) loading.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		loading.setLayoutParams(lp);

		fillDataBySubject(subject);
	}

	// 获取我的评论
	private void fillMyReview() {
		new AsyncTask<Void, Void, ReviewFeed>() {

			private UserEntry ue;

			@Override
			protected ReviewFeed doInBackground(Void... args) {
				ReviewFeed feed = null;
				try {
					ue = NetUtil.getDoubanService().getAuthorizedUser();
					feed = getDoubanService().getUserReviews(ue.getUid());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return feed;
			}

			@Override
			protected void onPostExecute(ReviewFeed feed) {
				super.onPostExecute(feed);
				closeProgressBar();
				if (feed != null) {
					reviews = ConvertUtil.ConvertReviews(feed, null, ue);
					setListAdapter(new ReviewListAdapter(ReviewActivity.this,
							getListView(), reviews));
					if (reviews.size() == 0) {
						Toast.makeText(ReviewActivity.this, "没有找到相关评论！",
								Toast.LENGTH_SHORT);
					}

				} else {
					Toast.makeText(ReviewActivity.this, "数据加载失败！",
							Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar("正在获取数据...");
			}
		}.execute();
	}

	
	// 获取评论数据
	private void fillDataBySubject(final Subject subject) {
		new AsyncTask<Subject, Void, ReviewFeed>() {

			@Override
			protected ReviewFeed doInBackground(Subject... args) {
				ReviewFeed feed = null;
				Subject subject = args[0];
				try {
					if (Subject.BOOK.equals(subject.getType())) {
						feed = getDoubanService().getBookReviews(
								subject.getId(), index, count, ORDERBY);
					} else if (Subject.MOVIE.equals(subject.getType())) {
						feed = getDoubanService().getMovieReviews(
								subject.getId(), index, count, ORDERBY);
					} else if (Subject.MUSIC.equals(subject.getType())) {
						feed = getDoubanService().getMusicReviews(
								subject.getId(), index, count, ORDERBY);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return feed;
			}

			@Override
			protected void onPostExecute(ReviewFeed feed) {
				super.onPostExecute(feed);
				closeProgressBar();
				if (feed != null) {
					total = feed.getTotalResults();
					reviews.addAll(ConvertUtil.ConvertReviews(feed, subject));
					if (listAdapter == null) {
						listAdapter = new ReviewListAdapter(
								ReviewActivity.this, getListView(), reviews);
						setListAdapter(listAdapter);
					} else {
						listAdapter.notifyDataSetChanged();
					}

					if (reviews.size() == 0) {
						Toast.makeText(ReviewActivity.this, "没有找到相关评论！",
								Toast.LENGTH_SHORT);
					}

				} else {
					Toast.makeText(ReviewActivity.this, "数据加载失败！",
							Toast.LENGTH_SHORT);
				}
				isFilling = false;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar();
				isFilling = true;
			}

		}.execute(subject);

	}

	public class ReviewListAdapter extends BaseAdapter {

		private ListView listView;
		private List<Review> reviews;
		private LayoutInflater mInflater;

		public ReviewListAdapter(Context context, ListView listView,
				List<Review> reviews) {
			this.listView = listView;
			this.reviews = reviews;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return reviews.size();
		}

		public Object getItem(int i) {
			return reviews.get(i);
		}

		public long getItemId(int i) {
			return i;
		}

		public View getView(int i, View view, ViewGroup vg) {
			if (view == null) {
				view = mInflater.inflate(R.layout.review_item, null);
			}

			Review review = reviews.get(i);

			TextView txtTitle = (TextView) view.findViewById(R.id.review_title);
			TextView txtSummary = (TextView) view
					.findViewById(R.id.review_summary);
			RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
			TextView txtAuthorName = (TextView) view
					.findViewById(R.id.author_name);

			txtTitle.setText(review.getTitle());
			String summary = review.getSummary();
			summary = summary.replaceAll("\\\n", "");
			summary = summary.replaceAll("\\\t", "");
			summary = summary.replaceAll(" ", "");
			txtSummary.setText(summary);
			ratingBar.setRating(review.getRating());
			txtAuthorName.setText("评论人:" + review.getAuthorName());
			return view;
		}
	}

	// 选中事件
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ReviewViewActivity.class);
		Review review = reviews.get(position);
		i.putExtra("review", review);
		startActivity(i);
	}

	// 上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
			menu.add(0, EDIT_ID, 0, R.string.menu_edit);
			menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = (int) info.id;
		Review review = reviews.get(id);
		switch (item.getItemId()) {
		case DELETE_ID:
			deleteReview(review);
			break;
		case EDIT_ID:
			editReview(review);
			break;

		}
		return super.onContextItemSelected(item);
	}

	// 编辑评论
	private void editReview(Review review) {
		// TODO Auto-generated method stub

	}

	public void showProgressBar(String title) {
		TextView loading = (TextView) findViewById(R.id.txt_loading);
		loading.setText(title);
		showProgressBar();
	}

	// 删除评论
	private void deleteReview(Review paramReview) {
		new AsyncTask<Review, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar("正在删除数据...");
			}

			@Override
			protected Boolean doInBackground(Review... args) {
				Review review = args[0];
				try {
					ReviewEntry reviewEntry = new ReviewEntry();
					reviewEntry.setId(review.getUrl());
					NetUtil.getDoubanService().deleteReview(reviewEntry);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					closeProgressBar();
					Toast.makeText(ReviewActivity.this, "评论删除成功！",
							Toast.LENGTH_SHORT).show();
					fillMyReview();
				} else {
					closeProgressBar();
					Toast.makeText(ReviewActivity.this, "评论删除失败！",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(paramReview);
	}
}
