package com.chenyc.douban;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenyc.douban.util.NetUtil;
import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.UserEntry;

/**
 * 用户信息界面
 * 
 * @author chenyc
 * 
 */
public class MeActivity extends BaseActivity {

	private DoubanService service = NetUtil.getDoubanService();

	private TextView userName;

	private TextView userAddress;

	private TextView userDescription;

	private ImageView userImage;

	private ImageButton backButton;
	private ImageButton editButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me);
		userName = (TextView) findViewById(R.id.txtUserName);
		userAddress = (TextView) findViewById(R.id.txtUserAddress);
		userDescription = (TextView) findViewById(R.id.txtUserDescription);
		userImage = (ImageView) findViewById(R.id.imgUser);

		// 隐藏按钮
		editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);
		backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("我的资料");
		fillData();
	}
	


	// 获取用户信息
	private void fillData() {
		// 开始获取用户信息
		new AsyncTask<Void, Void, UserEntry>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar();
			}

			@Override
			protected UserEntry doInBackground(Void... arg0) {
				UserEntry ue = null;
				try {
					ue = service.getAuthorizedUser();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ue;
			}

			@Override
			protected void onPostExecute(UserEntry ue) {
				closeProgressBar();
				
				if (ue == null) {
					new AlertDialog.Builder(MeActivity.this).setTitle("提示")
							.setMessage("获取用户信息失败！").setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											return;
										}
									}).show();
					return;
				}
				super.onPostExecute(ue);
				String title = ue.getTitle().getPlainText();
				String content = ((TextContent) ue.getContent()).getContent()
						.getPlainText();
				String location = ue.getLocation();
				userName.setText(title);
				userDescription.setText(content);
				userAddress.setText(location);
				
				//处理详细介绍空白的情况
				
				if("".equals(content)){
					userDescription.setText("请维护自我介绍！");
				}
				

				// 开始获取用户头像
				String iconUrl = ue.getLink("icon", null).getHref();
				new AsyncTask<String, Void, Bitmap>() {

					@Override
					protected Bitmap doInBackground(String... args) {
						String iconUrl = args[0];
						Bitmap bitmap = null;
						if (iconUrl != null) {
							try {
								bitmap = NetUtil.getNetImage(iconUrl);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return bitmap;
					}

					@Override
					protected void onPostExecute(Bitmap bitmap) {
						super.onPostExecute(bitmap);
						if (bitmap != null) {
							userImage.setImageBitmap(bitmap);
						}
					}

				}.execute(iconUrl);
			}
		}.execute();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return true;
	}

	
}
