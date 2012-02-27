package com.chenyc.douban;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chenyc.douban.entity.Review;
import com.chenyc.douban.util.NetUtil;

/**
 * 查看评论
 * 
 * @author chenyc
 * 
 */
public class ReviewViewActivity extends BaseActivity {

	private TextView txtReviewTitle;
	private TextView txtReviewContent;
	private TextView txtReviewComment;
	private ImageView userImageView;
	private TextView txtSubjectTitle;
	private TextView txtUserInfo;
	private ProgressDialog dialog;
	private RatingBar ratingBar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review_view);

		txtReviewTitle = (TextView) findViewById(R.id.review_title);
		txtReviewContent = (TextView) findViewById(R.id.review_content);
		txtReviewComment = (TextView) findViewById(R.id.review_comments);
		userImageView = (ImageView) findViewById(R.id.user_img);
		txtSubjectTitle = (TextView) findViewById(R.id.subject_title);
		txtUserInfo = (TextView) findViewById(R.id.user_info);
		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
		ratingBar.setVisibility(View.INVISIBLE);
		dialog = new ProgressDialog(this);

		// 回退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});

		Bundle extras = getIntent().getExtras();
		Review review = extras != null ? (Review) extras
				.getSerializable("review") : null;
		if (review != null) {
			TextView titleView = (TextView) findViewById(R.id.myTitle);
			titleView.setText("《" + review.getSubject().getTitle() + "》的评论");
			fillData(review);
		}
	}

	// 获取详细评论
	private void fillData(Review review) {
		new AsyncTask<Review, Void, Review>() {

			@Override
			protected Review doInBackground(Review... args) {
				Review review = args[0];
				try {
					review = NetUtil.getReviewContentAndComments(review);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return review;
			}

			@Override
			protected void onPostExecute(Review review) {
				dialog.hide();
				super.onPostExecute(review);
				txtReviewTitle.setText(review.getTitle());
				txtReviewContent.setText(Html.fromHtml(review.getContent()));
				txtReviewComment.setText(Html.fromHtml(review.getComments()));
				if (review.getAuthorImage() != null) {
					userImageView.setImageBitmap(review.getAuthorImage());
				}
				txtUserInfo.setText(" 评论人：" + review.getAuthorName());
				txtSubjectTitle.setText("《" + review.getSubject().getTitle()
						+ "》的评论");
				ratingBar.setRating(review.getRating());
				ratingBar.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("正在加载数据...");
				dialog.show();
			}

		}.execute(review);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
