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

public class TopicPublishActivity extends BaseActivity{

	private EditText edtTitle;
	private EditText edtContent;
	private Button submit;
	private String title,content,url,publishUrl;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topicpublish);
		//String url="http://www.douban.com/group/dbapi/new_topic";
		//得到传过来的url
		Intent intent=this.getIntent();
		Bundle b=intent.getExtras();
		url=b.getString("url");
		publishUrl=url+"new_topic";
		
		
		edtTitle=(EditText)findViewById(R.id.topicpublish_title);
		edtContent=(EditText)findViewById(R.id.topicpublish_content);
		submit=(Button)findViewById(R.id.topicpublish_submit);
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				title=edtTitle.getText().toString();
				content=edtContent.getText().toString();
				if("".equals(title.trim()) || 
						"".equals(content.trim()))
				{
					Toast.makeText(TopicPublishActivity.this, "请输入标题和内容", Toast.LENGTH_SHORT);
				}
				else
				{
					sendData();
				}
			}
		});
	}
	

	public void sendData()
	{
		new AsyncTask<Void, Void, Boolean >()
		{

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				NetUtil netUtil=new NetUtil();
				boolean fal=netUtil.publishTopic(title, content,publishUrl );
				return fal;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				pd.dismiss();
				if (result == true) {
					Toast.makeText(TopicPublishActivity.this, "话题发表成功",
							Toast.LENGTH_SHORT);
					// 跳转到相应小组的话题列表
					Intent i = new Intent(TopicPublishActivity.this,TopicListActivity.class);
					i.putExtra("url", url);
					startActivity(i);
				} else {
					Toast.makeText(TopicPublishActivity.this, "话题发表失败",
							Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				showDialog("正在提交数据");
			}
			
		}.execute();
	}
	
	public void showDialog(String str)
	{
		pd=ProgressDialog.show(TopicPublishActivity.this, "信息", str); 
	}
}
