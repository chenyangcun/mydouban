package com.chenyc.douban;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.ConvertUtil;
import com.chenyc.douban.util.NetUtil;
import com.chenyc.douban.util.StatusUtil;
import com.chenyc.douban.util.AsyncImageLoader.ImageCallback;
import com.google.gdata.data.douban.SubjectEntry;

public class SubjectViewActivity extends BaseActivity implements
		View.OnClickListener {
	private TextView txtTitle;
	private TextView txtDescription;
	private TextView txtSummary;
	private ImageView bookImage;
	private RatingBar ratingBar;
	private ProgressDialog dialog;
	private Button showReview1;
	private Button showReview2;
	private TextView bookStatus;
	private Subject subject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置自定义标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.book_view);
		Bundle extras = getIntent().getExtras();
		subject = extras != null ? (Subject) extras.getSerializable("subject")
				: null;

		txtTitle = (TextView) findViewById(R.id.book_title);
		txtDescription = (TextView) findViewById(R.id.book_description);
		txtSummary = (TextView) findViewById(R.id.book_summary);
		bookImage = (ImageView) findViewById(R.id.book_img);
		ratingBar = (RatingBar) findViewById(R.id.ratingbar);

		// 设置状态
		bookStatus = (TextView) findViewById(R.id.book_status);
		bookStatus.setText(StatusUtil.getStatusDesc(subject));

		LinearLayout bookToolbar = (LinearLayout) findViewById(R.id.book_toolbar);
		LinearLayout movieToolbar = (LinearLayout) findViewById(R.id.movie_toolbar);
		LinearLayout musicToolbar = (LinearLayout) findViewById(R.id.music_toolbar);

		if (Subject.BOOK.equals(subject.getType())) {
			bookToolbar.setVisibility(View.VISIBLE);
		} else if (Subject.MOVIE.equals(subject.getType())) {
			movieToolbar.setVisibility(View.VISIBLE);
		} else if (Subject.MUSIC.equals(subject.getType())) {
			musicToolbar.setVisibility(View.VISIBLE);
		}

		dialog = new ProgressDialog(this);
		TextView txtInfo = (TextView) findViewById(R.id.txtInfo);

		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("《" + subject.getTitle() + "》");

		if (subject != null) {
			fillData(subject.getUrl());
			titleText.setText("《" + subject.getTitle() + "》");
			if (Subject.BOOK.equals(subject.getType())) {
				txtInfo.setText(R.string.bookInfo);
			} else if (Subject.MOVIE.equals(subject.getType())) {
				txtInfo.setText(R.string.movieInfo);
			} else if (Subject.MUSIC.equals(subject.getType())) {
				txtInfo.setText(R.string.musicInfo);
			}

		}

		findViewById(R.id.btn_book_new_review).setOnClickListener(this);
		findViewById(R.id.btn_book_wish).setOnClickListener(this);
		findViewById(R.id.btn_book_reading).setOnClickListener(this);
		findViewById(R.id.btn_book_read).setOnClickListener(this);

		findViewById(R.id.btn_movie_new_review).setOnClickListener(this);
		findViewById(R.id.btn_movie_wish).setOnClickListener(this);
		findViewById(R.id.btn_movie_watched).setOnClickListener(this);

		findViewById(R.id.btn_music_new_review).setOnClickListener(this);
		findViewById(R.id.btn_music_wish).setOnClickListener(this);
		findViewById(R.id.btn_music_listening).setOnClickListener(this);
		findViewById(R.id.btn_music_listened).setOnClickListener(this);

		// 回退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});

		// 查看评论按钮
		showReview1 = (Button) findViewById(R.id.btnShowComment1);
		showReview2 = (Button) findViewById(R.id.btnShowComment2);
		OnClickListener showReviewClicklistener = new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(SubjectViewActivity.this,
						ReviewActivity.class);
				i.putExtra("subject", subject);
				startActivity(i);
			}

		};
		showReview1.setOnClickListener(showReviewClicklistener);
		showReview2.setOnClickListener(showReviewClicklistener);
	}

	private void fillData(String bookId) {

		new AsyncTask<String, Void, SubjectEntry>() {

			@Override
			protected SubjectEntry doInBackground(String... args) {
				String bookId = args[0];
				SubjectEntry entry = null;

				try {
					entry = NetUtil.getDoubanService().getBook(bookId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return entry;
			}

			@Override
			protected void onPostExecute(SubjectEntry result) {
				super.onPostExecute(result);
				if (result != null) {
					dialog.hide();
					Subject book = ConvertUtil.convertOneSubject(result);
					txtTitle.setText(book.getTitle());
					txtDescription.setText(book.getDescription());
					String summary = "\t\t" + book.getSummary();

					if (!"".equals(book.getAuthorIntro())) {
						summary = summary + "\n\n作者简介:" + book.getAuthorIntro();
					}

					if (!"".equals(book.getTagsToString())) {
						summary = summary + "\n\n标签:" + book.getTagsToString();
					}

					txtSummary.setText(summary);

					ratingBar.setRating(book.getRating());
					ratingBar.setVisibility(View.VISIBLE);
					bookStatus.setVisibility(View.VISIBLE);
					if (summary.length() > 200) {
						showReview2.setVisibility(View.VISIBLE);
					}
					showReview1.setVisibility(View.VISIBLE);
					String imageUrl = book.getImgUrl();
					Drawable drawable = NetUtil.asyncImageLoader.loadDrawable(
							imageUrl, new ImageCallback() {
								public void imageLoaded(Drawable imageDrawable,
										String imageUrl) {
									bookImage.setImageDrawable(imageDrawable);
								}
							});
					if (drawable != null) {
						bookImage.setImageDrawable(drawable);
					} else {
						bookImage.setImageResource(R.drawable.book);
					}
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("正在加载数据...");
				dialog.show();
			}

		}.execute(bookId);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_book_new_review:
		case R.id.btn_movie_new_review:
		case R.id.btn_music_new_review:
			intent = new Intent(this, ReviewEditActivity.class);
			intent.putExtra("subject", subject);
			intent.putExtra("id", v.getId());
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_book_read:
			
		case R.id.btn_book_reading:
		case R.id.btn_book_wish:
		case R.id.btn_movie_watched:
		case R.id.btn_movie_wish:
		case R.id.btn_music_listened:
		case R.id.btn_music_listening:
		case R.id.btn_music_wish:
			intent = new Intent(this, ShortReviewEditActivity.class);
			intent.putExtra("subject", subject);
			intent.putExtra("id", v.getId());
			startActivityForResult(intent, 1);
			break;
		}

	}

	//完成后回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//收藏成功
		if(requestCode == 1 && resultCode == 1){
			 Bundle extras = data.getExtras();
			 String status = extras != null ? (String) extras.getSerializable("status")
						: null;
			 String statusDesc = extras != null ? (String) extras.getSerializable("statusDesc")
						: null;
			 Float rating = extras != null ? (Float) extras.getSerializable("rating")
						: null;
			 
			 String tags = extras != null ? (String) extras.getSerializable("tags")
						: null;
			 
			 subject.setStatus(status);
			 subject.setMyRating(rating);
			 subject.setMyTags(tags);
			 
			 bookStatus.setText(statusDesc);
			 ratingBar.setRating(rating);
		}
	}
	
	
}
