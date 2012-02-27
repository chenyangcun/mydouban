package com.chenyc.douban;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.chenyc.douban.util.NetUtil;
import com.google.gdata.data.douban.MiniblogEntry;
import com.google.gdata.data.douban.MiniblogFeed;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.util.ServiceException;

public class MiniBlogActivity extends BaseListActivity{

	
	private int index=1;
	private int count=10;
	private MiniBlogListAdapter listAdapter;
//	private MiniblogFeed blogs;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ListView listView;
	private ProgressDialog pd;
	/* * 保存ListView中最近一次被点击的Item的信息  09     */  
	//ListView的第一个Item的position值为0,故必须初始化mLastPosition,使其永不冲突  11      
	private int mLastPosition = -1;   
	private View mLastView;  
	
	//看是否但最后一行----当前行数
	private int lastitem=0;
	//保存加载的Miniblog数据
	private ArrayList<MiniblogEntry> blogEntryarray=null;

	//编辑按钮和回退按钮
	private ImageButton editButton;
	private ImageButton backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.miniblog);
		editButton=(ImageButton)this.findViewById(R.id.edit_button);
		backButton=(ImageButton)this.findViewById(R.id.back_button);
		
		editButton.setOnClickListener(new EditButtonListener());
		backButton.setOnClickListener(new BackButtonListener());
		fillData();
		this.registerForContextMenu(getListView());
		listView=this.getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {

			// /////////
			// 内容点击后自由伸展
			// ///////////
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position != mLastPosition) {
					// 如果点击的条目和上一次的不同，那么就展开本条目，关闭上次展开的条目
					setVisible(view);
					setGone(mLastView);
					mLastPosition = position;
					mLastView = view;
				} else {
					// 如果点击的条目和上一次的相同，那么就弹出对话框，提供更多功能选项
					// showDialog(......);
				}
//				if(position ==lastitem-1)
//				{
//					fillData();
//					//定位ListView的位置
//					listView.setSelection(lastitem-1);
//				}
			}
		});	
		
		
		/////
		// 到最后一行时自动加载
		
		listView.setOnScrollListener( new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
//				if(lastitem==listAdapter.getCount()&& OnScrollListener.SCROLL_STATE_IDLE==scrollState) 
//				{
//					fillData();
//					//定位ListView的位置
//					listView.setSelection(lastitem-1);
//				}
				if (lastitem==listAdapter.getCount()&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 判断滚动到底部
						fillData();
						listView.setSelection(lastitem-1);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastitem=firstVisibleItem+visibleItemCount;
			}
		});
	}
	
	//编辑按钮的监听事件
	class EditButtonListener implements OnClickListener 
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i=new Intent(MiniBlogActivity.this,SayActivity.class);
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
		new AsyncTask<Void, Void, MiniblogFeed>() {
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				showDialog();
			}

			@Override
			protected void onPostExecute(MiniblogFeed result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				pd.dismiss();
				if(result!=null)
				{
					if(listAdapter==null)
					{
						blogEntryarray=new ArrayList<MiniblogEntry>();
						for(int i=0;i<result.getEntries().size();i++)
						{
							blogEntryarray.add(result.getEntries().get(i));
						}
						listAdapter=new MiniBlogListAdapter(MiniBlogActivity.this, blogEntryarray);
						setListAdapter(listAdapter);
					}
					else
					{
						for(int i=0;i<result.getEntries().size();i++)
						{
							blogEntryarray.add(result.getEntries().get(i));
						}
						listAdapter.notifyDataSetChanged();
					}
				}
				else
				{
					Toast.makeText(MiniBlogActivity.this, "数据加载失败！",Toast.LENGTH_SHORT);
				}
			}
			
			@Override
			protected MiniblogFeed doInBackground(Void... params) {
				// TODO Auto-generated method stub
				MiniblogFeed blogFeed=null;
				
				try {
					UserEntry ue=NetUtil.getDoubanService().getAuthorizedUser();
					blogFeed=NetUtil.getDoubanService().getContactsMiniblogs(ue.getUid(), index, count);
					index+=count;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return blogFeed;
			}
		}.execute();
	}

	//适配器
	private class MiniBlogListAdapter extends BaseAdapter {

	//	private MiniblogFeed blogList;
		private List<MiniblogEntry> mbEntrylist;
		private LayoutInflater mInflater;

		public MiniBlogListAdapter(Context context, List<MiniblogEntry> blog) {
			super();
			mbEntrylist=blog;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public int getCount() {
			return mbEntrylist.size();
		}

		public Object getItem(int i) {
			return mbEntrylist.get(i);
		}

		public long getItemId(int i) {
			return i;
		}

		public View getView(int i, View view, ViewGroup vgroup) {

			if (view == null) {
				view = mInflater.inflate(R.layout.miniblog_item,null);
			}
			TextView author=(TextView) view.findViewById(R.id.miniblog_name);
			TextView time=(TextView)view.findViewById(R.id.miniblog_time);
			TextView content = (TextView) view.findViewById(R.id.miniblog_content);
			TextView summary=(TextView)view.findViewById(R.id.miniblog_summary);
			MiniblogEntry entry=mbEntrylist.get(i);
			
			author.setText("关于："+entry.getAuthors().get(0).getName());
			time.setText("时间: "+df.format(entry.getPublished().getValue()));
			String str=entry.getTitle().getPlainText();
			int m=str.length()<10?str.length():10;
			summary.setText(str.subSequence(0, m)+"...");
			content.setText(entry.getTitle().getPlainText());
			
			//System.out.println("0000000"+blogList.getEntries().get(i).getSummary().getPlainText());
			return view;
		}
	}

	 /*
	  * 让view可视
	  */
	 private void setVisible(View view) {
	    if(view == null)return;
	    LinearLayout layout = (LinearLayout)view;
	           layout.findViewById(R.id.miniblog_content).setVisibility(View.VISIBLE);
	           layout.findViewById(R.id.miniblog_summary).setVisibility(View.GONE);
	 }
	 
	 /*
	  * 让view不可视
	  */
	 private void setGone(View view) {
	    if(view == null)return;
	    LinearLayout layout = (LinearLayout)view;
	    layout.findViewById(R.id.miniblog_content).setVisibility(View.GONE);
	    layout.findViewById(R.id.miniblog_summary).setVisibility(View.VISIBLE);
	 } 
	 
	 //加载对话框
	 public void showDialog()
	 {
		pd=ProgressDialog.show(MiniBlogActivity.this, "信息", "加载数据中..."); 
	 }

	//选中事件
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		//System.out.println("this is select----");

	}
	
}