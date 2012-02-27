package com.chenyc.douban;

import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.NetUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class FavActivity extends BaseListActivity {
	private String[] titles = new String[] { "我读...", "我看...", "我听...",
			"我评...", "我的日记...", "我的资料" ,"小组"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav);

		setListAdapter(new ArrayAdapter<String>(FavActivity.this,
				R.layout.fav_item, R.id.fav_title, titles));

		// 隐藏按钮
		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doExit();
			}
		});

		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("我的豆瓣");
	}



	// 选中事件
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (!checkLogin()) {
			Intent intent = new Intent(FavActivity.this, LoginActivity.class);
			intent.putExtra("position", position);
			startActivityForResult(intent, 1);
			return;
		}
		selectedOne(position);
	}

	//通过登录按钮成功后，返回的操作
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			selectedOne(resultCode);
		}
	}

	private void selectedOne(int position) {
		String cat = "";
		Intent i = new Intent(this, MySubjectActivity.class);
		switch (position) {
		case 0:
			cat = Subject.BOOK;
			i.putExtra("cat", cat);
			startActivity(i);
			break;
		case 1:
			cat = Subject.MOVIE;
			i.putExtra("cat", cat);
			startActivity(i);
			break;
		case 2:
			cat = Subject.MUSIC;
			i.putExtra("cat", cat);
			startActivity(i);
			break;
		case 3:
			i = new Intent(this, ReviewActivity.class);
			i.putExtra("my_review", true);
			startActivity(i);
			break;
		case 4:
			i = new Intent(this, NoteActivity.class);
			startActivity(i);
			break;
		case 5:
			i = new Intent(this, MeActivity.class);
			startActivity(i);
			break;
		case 6:
			i = new Intent(this, GroupActivity.class);
			startActivity(i);
			break;
		/**
		 * case 6: i = new Intent(this, SayActivity.class); startActivity(i);
		 * break; case 7: i=new Intent(this,MiniBlogActivity.class);
		 * startActivity(i); break; // case 8: // i=new
		 * Intent(this,GroupActivity.class); // startActivity(i); // break; //
		 * case 9: // i=new Intent(this,TopicPublishActivity.class); //
		 * startActivity(i); // break;
		 **/
		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doExit();
			return true;
		}
		return true;
	}

}
