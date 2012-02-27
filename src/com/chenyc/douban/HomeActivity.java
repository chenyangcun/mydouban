package com.chenyc.douban;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyc.douban.adapter.SubjectListAdapter;
import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.NetUtil;

public class HomeActivity extends BaseListActivity {
	private List<Subject> books = new ArrayList<Subject>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.subject);

		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doExit();
			}
		});
		
		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("豆瓣新书");

		showList();

	}

	private void showList() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					books = NetUtil.getDoubanNewBooks();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				closeProgressBar();
				if (result) {
					setListAdapter(new SubjectListAdapter(HomeActivity.this,
							getListView(), books));
				} else {
					Toast.makeText(HomeActivity.this, "数据加载失败！",
							Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar();
			}

		}.execute();

	}

	// 选中事件
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, SubjectViewActivity.class);
		Subject book = books.get(position);
		i.putExtra("subject", book);
		startActivity(i);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doExit();
			return true;
		}
		return true;
	}

}
