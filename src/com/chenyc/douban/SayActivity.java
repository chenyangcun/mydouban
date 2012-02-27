package com.chenyc.douban;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.util.ServiceException;

public class SayActivity extends BaseActivity{

	private Button button;
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.say);
		
		button=(Button) this.findViewById(R.id.say_submit);
		editText=(EditText) this.findViewById(R.id.say_edittext);
		button.setOnClickListener(new buttonListener());
	//	editText.setOnClickListener(new editTextListener());
	}
	
	class buttonListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str=editText.getText().toString();
			try {
				if (str.length() <= 140) {
					getDoubanService().createSaying(new PlainTextConstruct(str));
					Toast.makeText(SayActivity.this, "提交成功",
									Toast.LENGTH_SHORT).show();
					// 跳转界面
					Intent i = new Intent(SayActivity.this,
							MiniBlogActivity.class);
					startActivity(i);
				} else {
					Toast.makeText(SayActivity.this, "字数不能超过140个",
							Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(SayActivity.this, "提交失败，请检查网络设置",Toast.LENGTH_SHORT ).show();
			}
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
}
