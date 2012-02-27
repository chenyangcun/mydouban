package com.chenyc.douban;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class NoteViewActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置自定义标题
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.note_view);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		Bundle extras = getIntent().getExtras();
		Long id = extras != null ? extras.getLong("id") : null;
		String title = extras != null ? extras.getString("title") : null;
		String content = extras != null ? extras.getString("content") : null;
		String publishDate = extras != null ? extras.getString("publish_date")
				: null;

		TextView txtTitle = (TextView) findViewById(R.id.title);
		TextView txtContent = (TextView) findViewById(R.id.content);

		title = title + "\n(";
		SpannableString sp = new SpannableString(title + publishDate + ")");
		sp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), title
				.length() - 1, title.length() + publishDate.length() + 1,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		sp.setSpan(new AbsoluteSizeSpan(18), title.length() - 1, title.length()
				+ publishDate.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		txtTitle.setText(sp);
		txtContent.setText(content);
		
		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("豆瓣日记");

		// 回退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});
		
		//隐藏编辑按钮
		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);
		
	}
}
