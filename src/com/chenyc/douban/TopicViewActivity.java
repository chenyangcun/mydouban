package com.chenyc.douban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chenyc.douban.entity.Topic;
import com.chenyc.douban.util.NetUtil;

public class TopicViewActivity extends BaseListActivity{

	private String url=null;
	private String title=null;
	private List<Topic> replyList=null;
	private ListAdapter listAdapter;
	private ListView listView;
	//菜单选项
	public final int REPLY=0;

	
	private List<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.topicview);
		
		//得到url和话题的标题
		Intent intent=this.getIntent();
		Bundle bund=intent.getExtras();
		url=  (String) bund.get("url");
		title= (String) bund.get("title");
		TextView topicTitle=(TextView)findViewById(R.id.topicview_topictitle);
		topicTitle.setText(title);
		
		//Log.d("e", url+"]]]]]]]]]]]");
		//加载数据
		fillData();
		listView=this.getListView();
		this.registerForContextMenu(listView);
	}
	
	//菜单选项
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,REPLY,0,R.string.topic_view_replymenu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//option菜单监听器
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id=item.getItemId();
		switch (id) {
		case REPLY:
			//跳转到话题回复页面
			Intent intent=new Intent(this,TopicReplyActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public void fillData()
	{
		new AsyncTask<Void, Void, List<Topic>>() {

			@Override
			protected List<Topic> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				if(replyList==null)
				{
					NetUtil netUtil=new NetUtil();
					replyList=netUtil.getTopic(url);
				}
				return replyList;
			}

			@Override
			protected void onPostExecute(List<Topic> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(listAdapter==null)
				{
//					listAdapter=new TopicViewAdapter(result,TopicViewActivity.this);
					findViewById(R.id.topicview_topictitle).setVisibility(View.VISIBLE);
					addtoList(result);
					listAdapter=new SimpleAdapter(TopicViewActivity.this, list,
							R.layout.topicview_item, new String[] { "author","time",
							"content" }, new int[] {
							R.id.topicview_author,R.id.topicview_time,R.id.topicview_content});
					setListAdapter(listAdapter);
					Log.d("element", "***********"+list.size());
				}
				//撤销加载对话框
				closeProgressBar();
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				showProgressBar();
			}
			
		}.execute();
	}
	/*
	//itme里面得组件
	static class Holder
	{
		TextView author;
		TextView time;
		TextView content;
	}
	private class TopicViewAdapter extends BaseAdapter
	{

		 private List<Topic> list=null;
		 private LayoutInflater lInflater=null;
		 private Holder holder;
		 public TopicViewAdapter(List<Topic> l,Context c)
		 {
			 super();
			 list=l;
			 lInflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView=lInflater.inflate(R.layout.topicview_item, null);
				holder=new Holder();
				holder.author=(TextView)findViewById(R.id.topicview_author);
				holder.time=(TextView)findViewById(R.id.topicview_time);
				holder.content=(TextView)findViewById(R.id.topicview_content);
			}
			
			holder.author.setText(list.get(position).getAuthor());
			holder.time.setText(list.get(position).getLastdate());
			holder.content.setText(list.get(position).getTitle());
			
			return convertView;
		}
		
	}  */
	
	//
	public void addtoList(List<Topic> l)
	{
		for(int i=0;i<l.size();i++)
		{
			HashMap<String,String> map=new HashMap<String,String>();
			if (i == 0) {
				String content=l.get(i).getTitle().replace("<br>", "\n").replace("<br/>", "\n")
				.replace("\"", "").replace("<a href=", "").replace("target=_blank rel=nofollow>", "\n")
				.replace("</a>", "");
				map.put("author", l.get(i).getAuthor());
				map.put("time", l.get(i).getLastdate());
				map.put("content", Html.fromHtml(content).toString());
				list.add(map);
			}
			else
			{
				map.put("author", l.get(i).getAuthor());
				map.put("time", l.get(i).getLastdate());
				map.put("content", Html.fromHtml(l.get(i).getTitle()).toString());
				list.add(map);
			}
		}
	}
}
