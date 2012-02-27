package com.chenyc.douban;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyc.douban.entity.Review;
import com.chenyc.douban.entity.Subject;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.douban.ReviewEntry;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.extensions.Rating;

public class ReviewEditActivity extends BaseActivity {
	private final int SUCCESS = 1;
	private ProgressDialog dialog;
	private Review review;
	private EditText reviewContent;
	private RatingBar reviewRatingbar;
	private EditText reviewTitle;
	private Subject subject;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.review_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar3);
		this.reviewTitle = (EditText) findViewById(R.id.EdtReviewTitle);
		;
		this.reviewContent = (EditText) findViewById(R.id.EdtReviewContent);
		this.reviewRatingbar = (RatingBar) findViewById(R.id.ratingbar);
		Bundle extras = getIntent().getExtras();
		subject = extras != null ? (Subject) extras.getSerializable("subject")
				: null;
		review = extras != null ? (Review) extras.getSerializable("review")
				: null;
		String title = "评论《" + this.subject.getTitle() + "》";
		((TextView) findViewById(R.id.myTitle)).setText(title);
		initView();
		setData();
	}

	private void doPost() {
		String title = this.reviewTitle.getText().toString().trim();
		String content = this.reviewContent.getText().toString().trim();
		int rating = (int) this.reviewRatingbar.getRating();
		if ("".equals(title)) {
			Toast.makeText(this, "评论标题不能为空！", 0).show();
			return;
		}
		if (rating == 0) {
			Toast.makeText(this, "评分不能为空！", 0).show();
			return;
		}
		if ("".equals(content)) {
			Toast.makeText(this, "评论内容不能为空！", 0).show();
			return;
		}
		if (content.length() < 50) {
			Toast.makeText(this, "评论内容不能小于50个字符！", 0).show();
			return;
		}

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dialog.hide();
					String message = "";
					if (review == null) {
						message = "评论新增成功！";
					} else {
						message = "评论修改成功！";
					}
					Toast.makeText(ReviewEditActivity.this, message,
							Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					setResult(SUCCESS, it);
					finish();
				} else {
					String message = "";
					if (review == null) {
						message = "评论新增失败！";
					} else {
						message = "评论修改失败！";
					}
					Toast.makeText(ReviewEditActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected void onPreExecute() {
				dialog.setMessage("正在提交数据...");
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... arg0) {
				String title = reviewTitle.getText().toString().trim();
				String content = reviewContent.getText().toString().trim();
				int ratingValue = (int) reviewRatingbar.getRating();
				Rating rating = new Rating();
				rating.setValue(ratingValue);
				// 更新
				if (review != null) {
					try {
						ReviewEntry reviewEntry = new ReviewEntry();
						reviewEntry.setId(review.getId());
						getDoubanService().updateReview(reviewEntry,
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), rating);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				// 新增
				else {
					try {
						SubjectEntry subjectEntry = new SubjectEntry();
						subjectEntry.setId(subject.getId());
						getDoubanService().createReview(subjectEntry,
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), rating);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

		}.execute();
	}

	private void initView() {
		dialog = new ProgressDialog(this);

		Button btnSave = (Button) findViewById(R.id.btnSave);
		// 保存
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				doPost();
			}

		});
		//取消
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				finish();
			}

		});
		//返回
		ImageButton backButton = (ImageButton)findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				goBack();
			}

		});
		

	}

	private void setData() {
		if (this.review != null) {
			reviewTitle.setText(review.getTitle());
			reviewContent.setText(review.getContent());
			reviewRatingbar.setRating(review.getRating());
		}
	}

	protected void goBack() {
		String title = this.reviewTitle.getText().toString().trim();
		String content = this.reviewContent.getText().toString().trim();
		if (("".equals(title)) && ("".equals(content))) {
			finish();
			return;
		}

		if (this.review != null) {
			String reviewTitle = this.review.getTitle();
			String reviewContent = this.review.getContent();
			if (title.equals(reviewTitle) && content.equals(reviewContent)) {
				finish();
				return;
			}
		}
		new AlertDialog.Builder(ReviewEditActivity.this).setTitle("提示")
				.setMessage("数据未保存，确定要回退吗？").setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								finish();
							}
						}).setNeutralButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}

						}).show();
	}

	protected void onPause() {
		super.onPause();
		if (this.dialog != null)
			this.dialog.dismiss();
	}
}
