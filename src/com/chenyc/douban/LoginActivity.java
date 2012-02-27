package com.chenyc.douban;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chenyc.douban.entity.MyClientCookie;
import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.NetUtil;

/**
 * 登录窗口
 * 
 * @author chenyc
 * 
 */
public class LoginActivity extends BaseActivity {

	private Bitmap bitmap;
	private ImageView viewCaptcha;
	private ProgressDialog dialog;
	private Button btnLogin;
	private Button btnGetCaptcha;
	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtCaptcha;
	private String captchaId;
	private Button btnExit;
	private LinearLayout captchaLayout;

	private static final int GET_IMAGE_MESSAGE = 0x0001;
	private static final int LOGIN_MESSAGE = 0x0002;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnExit = (Button) findViewById(R.id.btnExit);
	//	btnGetCaptcha = (Button) findViewById(R.id.btnGetCaptcha);
		edtEmail = (EditText) findViewById(R.id.EditTextEmail);
		edtPassword = (EditText) findViewById(R.id.EditTextPassword);
		edtCaptcha = (EditText) findViewById(R.id.EditTextCaptchaValue);
		
		captchaLayout = (LinearLayout)findViewById(R.id.Captcha);

		//看是否保存了email和password
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		String email="",password="";
		if(sharedata!=null)
		{
			email = sharedata.getString("email", "");
			//password = sharedata.getString("password", "");
		}
		edtEmail.setText(email);
		//edtPassword.setText(password);
		
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);

		// 设置登录按钮事件
		btnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				dialog.setMessage("正在登录验证...");
				dialog.show();
				new Thread() {
					public void run() {
						String email = edtEmail.getText().toString();
						String password = edtPassword.getText().toString();
						String captchaValue = edtCaptcha.getText().toString();
						int status = -1;
						try {
							// 登录成功
							if (NetUtil.doubanLogin(email, password, captchaId,
									captchaValue)) {
								status = 1;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						Message msg_listData = new Message();
						msg_listData.what = LOGIN_MESSAGE;
						msg_listData.arg1 = status;
						handler.sendMessage(msg_listData);

					}
				}.start();

			}
		});
		
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		/**
		// 设置获取验证码事件
		btnGetCaptcha.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				//getCaptcha();
			}

		});

		// 获取验证码
		getCaptcha();
	**/
	}


	// 获取验证码
	private void getCaptcha() {
		dialog.setMessage("正在获取验证码...");
		dialog.show();
		viewCaptcha = (ImageView) findViewById(R.id.ImageViewCaptcha);

		// 获取验证码
		new Thread() {
			public void run() {
				int status = -1;
				try {
					captchaId = NetUtil.getCaptchaId();
					bitmap = NetUtil.getCaptchaImg(captchaId);
					status = 1;
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message msg_listData = new Message();
				msg_listData.what = GET_IMAGE_MESSAGE;
				msg_listData.arg1 = status;
				handler.sendMessage(msg_listData);
			}
		}.start();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case GET_IMAGE_MESSAGE:
				dialog.hide();
				if (message.arg1 > 0) {
					viewCaptcha.setImageBitmap(bitmap);
					captchaLayout.setVisibility(View.VISIBLE);
				} else {
//					new AlertDialog.Builder(LoginActivity.this).setTitle("提示")
//							.setMessage("获取验证码图片失败！").setPositiveButton("确定",
//									new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialoginterface,
//												int i) {
//											return;
//										}
//									}).show();
				}
				break;
			case LOGIN_MESSAGE:
				dialog.hide();
				if (message.arg1 > 0) {
					// 保存accessToken
					Editor sharedata = getSharedPreferences("data", 0).edit();
					sharedata.putString("accessToken", NetUtil.getAccessToken());
					sharedata.putString("tokenSecret", NetUtil.getTokenSecret());
					sharedata.putString("uid", NetUtil.getUid());
					sharedata.putString("email",edtEmail.getText().toString());
					//sharedata.putString("password",edtPassword.getText().toString());
					sharedata.commit();
					try {
						storeCookieToFile(NetUtil.getCkStore());
					} catch (Exception e) {
						e.printStackTrace();
					}
					lo1ginSuccess();
				} else {
					/**
					try {
						captchaId = NetUtil.getCaptchaId();
						bitmap = NetUtil.getCaptchaImg(captchaId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					viewCaptcha.setImageBitmap(bitmap);
					**/
					new AlertDialog.Builder(LoginActivity.this).setTitle("提示")
							.setMessage("登录验证失败，请检查用户名密码是否正确！如果有验证码，请输入验证码重试！").setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											return;
										}
									}).show();
					// 重新获取验证码
					getCaptcha();
				}
				break;
			}
			super.handleMessage(message);
		}
	};
	
	//保存Cookie
	private  void storeCookieToFile(CookieStore cookieStore) throws Exception  {
		FileOutputStream fs = this.openFileOutput("cookie.dat", MODE_PRIVATE);  
		ObjectOutputStream os =  new ObjectOutputStream(fs);   
		ArrayList<MyClientCookie> myCookies = new ArrayList<MyClientCookie>();
		for(Cookie cookie:cookieStore.getCookies()){
			myCookies.add(new MyClientCookie(cookie));
		}
		os.writeObject(myCookies.toArray());
		os.close();
	}

	//登录成功后的操作
	protected void lo1ginSuccess() {
		Bundle extras = getIntent().getExtras();
		Integer position = extras != null ? (Integer) extras.getSerializable("position")
				: null;
		if(position != null){
			Intent it = new Intent();
			setResult(position,it);
		}
		finish();
	}

}
/*
//保存cookies
					Editor sharedata2 = getSharedPreferences("cookie", 0).edit();
					List<Cookie> cookies=new NetUtil().getCkStore().getCookies();
					Log.d("element","size=="+cookies.size());
					for(int i=0;i<cookies.size();i++)
					{
						Cookie ck=cookies.get(i);
					    String name=ck.getName();
					    String value=ck.getValue();
					    
					    Log.d("element",name+"-->"+value);
					   
						Log.d("element","domain-->"+ck.getDomain());
						Log.d("element","path-->"+ck.getPath());
						Log.d("element","comment-->"+ck.getComment());
						Log.d("element","url-->"+ck.getCommentURL());
					//	Log.d("element","date-->"+ck.getExpiryDate().toString());
						Log.d("element","version-->"+ck.getVersion()+"");
//					    sharedata2.putString(i+""+name,value);
					}
					sharedata2.putString("ue",cookies.get(0).getValue());
					sharedata2.putString("bid",cookies.get(1).getValue());
					sharedata2.putString("dbcl2",cookies.get(2).getValue());
					sharedata2.putString("ck",cookies.get(3).getValue());
					sharedata2.commit();
*/