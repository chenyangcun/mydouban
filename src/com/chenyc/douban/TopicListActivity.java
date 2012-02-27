package com.chenyc.douban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chenyc.douban.entity.Topic;
import com.chenyc.douban.util.NetUtil;



public class TopicListActivity extends BaseListActivity{

	private List<Topic> topicList=null;
	private int index=1;
	private int count=10;
	private int lastitem=0;
	//传过来的小组的地址URL
	private String url=null;
	//listAdapter
	private ListView listView;
	private SimpleAdapter listAdapter;
	private List<HashMap<String, String>>  list = new ArrayList<HashMap<String, String>>();
	//编辑按钮和回退按钮
	private ImageButton editButton;
	private ImageButton backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.topiclist);
		
		//得到相应小组的URL
		Intent intent=this.getIntent();
		Bundle extra=intent.getExtras();
		url=extra.getString("url");
		editButton=(ImageButton)this.findViewById(R.id.edit_button);
		backButton=(ImageButton)this.findViewById(R.id.back_button);
		
		editButton.setOnClickListener(new EditButtonListener());
		backButton.setOnClickListener(new BackButtonListener());
		//加载数据
		fillData();
		
		this.registerForContextMenu(getListView());
		listView=this.getListView();
		
		//监听器   滚动条到底部后自动加载
		listView.setOnScrollListener( new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (lastitem==listAdapter.getCount()&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 判断滚动到底部
					if(index<topicList.size())
					{
						fillData();
						listView.setSelection(lastitem-1);
					}
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastitem=firstVisibleItem+visibleItemCount;
			}
	});
		
		
		//监听器  点击后转到TopicView页面
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v , int position, long id) {
				// TODO Auto-generated method stub
				//跳转到TopicView页面
				Intent intent=new Intent(TopicListActivity.this,TopicViewActivity.class);
				intent.putExtra("url", topicList.get(position+1).getUrl());
				intent.putExtra("title", topicList.get(position+1).getTitle());
				startActivity(intent);
			}
		});
	}
	
	//编辑按钮的监听事件
	class EditButtonListener implements OnClickListener 
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i=new Intent(TopicListActivity.this,TopicPublishActivity.class);
			i.putExtra("url", url);
	//		System.out.println(url+":::::::::::::::");
			startActivity(i);
		}
	}
	
	//回退按钮的监听事件
	class BackButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	


	//加载数据
	public void fillData()
	{
		new AsyncTask<Void,Void,List<Topic> >()
		{
			@Override
			protected List<Topic> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				if(topicList==null)
				{
					NetUtil netUtil=new NetUtil();
					//得到小组的数据
					topicList=netUtil.getTopicList(url);
				}
				List<Topic> topicl=getTopiclistByCount(index);
								
				return topicl;
			}

			@Override
			protected void onPostExecute(List<Topic> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			
				//如果小组的话题不为空
				if(result!=null)
				{
					if(listAdapter==null)
					{
						addToList(result);
						listAdapter = new SimpleAdapter(TopicListActivity.this, list,
								R.layout.topic_item, new String[] { "topic_name",
								"topic_time", "topic_title" }, new int[] {
								R.id.topic_writer, R.id.topic_time,R.id.topic_title});
						setListAdapter(listAdapter);
					}
					else
					{
						//加载剩余的数据  
						addToList(result);
						//提醒数据改变
						listAdapter.notifyDataSetChanged();
					}
				}
				else
				{
					Toast.makeText(TopicListActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				}
				//撤销加载框
				//pd.dismiss();
				closeProgressBar();
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//显示对话框
				showProgressBar("正在加载数据...");
			}
		}.execute();
	}	

	//将剩余数据加到数据列中
	public void addToList(List<Topic> tl) {
		Iterator<Topic> iterator = null;
		for (iterator = tl.iterator(); iterator.hasNext();) {
			Topic t = (Topic) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("topic_name", "发表自:"+t.getAuthor()+"  ");
			map.put("topic_reply", "回复数："+t.getReply());
			map.put("topic_time", t.getLastdate());
			map.put("topic_title", t.getTitle());
			list.add(map);
		}
	}

//分段加载数据
public List<Topic> getTopiclistByCount(int index_)
{
	List<Topic> list=new ArrayList<Topic>();
	int temp=index_+count;
	int size=topicList.size();
	for(int i=index;i<(temp<=size?temp:size);i++)
	{
		list.add(topicList.get(i));
	}
	index+=count;
	return list;
}



}

/*
 * 显示list中的数据
if(list!=null)
{
	Log.d("element", "i am list is not null"+list.size());
	for(int i=0;i<list.size();i++)
	{
		String author=list.get(i).getAuthor();
		String date =list.get(i).getLastdate();
		String title=list.get(i).getTitle();
		String url=list.get(i).getUrl();
		String reply=list.get(i).getReply();
		Log.d("element","+++++++++++++++++++++++" );
		Log.d("element",author );
		Log.d("element",title );
		Log.d("element",date );
		Log.d("element",url);
		Log.d("element",reply);
	}
}
*/
