package com.chenyc.douban;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chenyc.douban.util.NetUtil;

public class TopicReplyActivity extends BaseActivity{

	private EditText edtContent;
	private Button submit;
	private String content,url,replyUrl;
	private NetUtil netUtil=new NetUtil();
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topicreply);
		
		//得到传过来的值
		Intent intent=this.getIntent();
		Bundle bun=intent.getExtras();
		url=bun.getString("url");
		replyUrl=url+"add_comment#last";
		//初始化组件 
		edtContent=(EditText)this.findViewById(R.id.topicreply_content);
		submit=(Button)this.findViewById(R.id.topicreply_submit);
		
		//提交按钮的监听器
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content=edtContent.getText().toString();
				if(content.trim().equals(""))
				{
					Toast.makeText(TopicReplyActivity.this, content, Toast.LENGTH_SHORT);
				}
				else
				{
					sendReply();
				}
			}
		});
	}
	
	/**
	 * 提交回应的内容
	 * @param url
	 * @param content
	 */
	public void sendReply()
	{
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				//提交回应的数据
				boolean result=netUtil.topicReply(content, replyUrl);
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				//撤销对话框
				pd.dismiss();
				if(result)
				{
					Toast.makeText(TopicReplyActivity.this, "回应成功！", Toast.LENGTH_SHORT);
					
				}
				else{
					Toast.makeText(TopicReplyActivity.this, "回应失败，请稍候再试", Toast.LENGTH_SHORT);
				}
				//跳到相应的话题列表
				Intent intent=new Intent(TopicReplyActivity.this,TopicViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
				
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//显示对话框
				showDialog("数据正在提交中，请稍候！");
			}
			
		}.execute();
	}
	
	public void showDialog(String str)
	{
		pd=ProgressDialog.show(TopicReplyActivity.this, "信息", str);
	}
}
