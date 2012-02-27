package com.chenyc.douban;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chenyc.douban.entity.Group;
import com.chenyc.douban.util.NetUtil;

public class GroupActivity extends BaseActivity{

	//小组信息
	private List<Group> groupList;
	//网格布局
	private GridView gridView;
	//图片信息
	private List<Bitmap> imgList=null; 
	//小组名信息
	private List<String> nameList=null;
	//适配器
	private ImageAdapter imageAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.group);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar3);
	
		
		//获取网格布局
		gridView=(GridView)findViewById(R.id.groupview);
		//获取用户的小组信息
		//加载数据
		fillData();
		
		//事件监听
		gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				String url=groupList.get(position).getUrl();
				Intent intent=new Intent(GroupActivity.this,TopicListActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		}
		);
	}
	
	public void fillData()
	{
		new AsyncTask<Void,Void, List<Bitmap>>(){

			@Override
			protected List<Bitmap> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				groupList=NetUtil.getGroup(1);
				nameList=getGroupName();
				List<Bitmap> list;
				//加载图片信息
				list=getGroupImage();
				return list;
			}

			@Override
			protected void onPostExecute(List<Bitmap> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				//pd.dismiss();
				closeProgressBar();
				
				if(result!=null)
				{
					if(imageAdapter==null)
					{
						//添加元素
						imageAdapter=new ImageAdapter(GroupActivity.this,result,nameList);
						gridView.setAdapter(imageAdapter);
					}
				}
				else
				{
					Toast.makeText(GroupActivity.this, "加载数据失败", Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//showDialog();
				showProgressBar("正在加载数据...");
			}
		}.execute();
	}
	

 class ImageAdapter extends BaseAdapter
	{
		// 定义Context
		//private Context		mContext;
	 private LayoutInflater mInflater;
		// 定义整型数组 即图片源
		private List<Bitmap>	list; 
		private List<String>	namel;
		public ImageAdapter(Context c,List<Bitmap> listGroup,List<String> nameList)
		{
			//mContext = c;
			mInflater=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			list=listGroup;
			namel=nameList;
		}

		// 获取图片的个数
		public int getCount()
		{
			return list.size();
		}

		// 获取图片在库中的位置
		public Object getItem(int position)
		{
			return position;
		}

		// 获取图片ID
		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView imageView;
			TextView textView;
			if (convertView == null)
			{
				// 给ImageView设置资源
			//	imageView = new ImageView(mContext);
				convertView=mInflater.inflate(R.layout.group_item, null);
				// 设置布局 图片120×120显示
				//imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				// 设置显示比例类型
				//imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			else
			{
				//imageView = (ImageView) convertView;
			}
			imageView=(ImageView)convertView.findViewById(R.id.group_image);
			textView=(TextView)convertView.findViewById(R.id.group_item);
			imageView.setImageBitmap(list.get(position));
			textView.setText(namel.get(position));
			
			// 设置显示比例类型
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			return convertView;
		}
	}
	
	
//得到小组的图片信息
public List<Bitmap> getGroupImage()
{
	if(groupList.size()>0)
	{
		imgList=new ArrayList<Bitmap>();
		for(int i=0;i<groupList.size();i++)
		{
			String url=groupList.get(i).getImgUrl();
			try {
				//获得图片信息
				Bitmap bitm=NetUtil.getNetImage(url);
				//添加到图片队列
				imgList.add(bitm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return imgList;
	}
	else
	{
		//....................
		return null;
	}
}

//得到小组的名称
public List<String> getGroupName()
{
	List<String> list=new ArrayList<String>();
	if(groupList.size()>0)
	{
		for(int i=0;i<groupList.size();i++)
		{
			String name=groupList.get(i).getName();
			list.add(name);
		}
		return list;
	}
	else
	{
		//....................
		return null;
	}
}

}
/*
//组装cookies
SharedPreferences sharedata = getSharedPreferences("cookie", 0);
BasicClientCookie bc1=new BasicClientCookie("ue",sharedata.getString("ue",null));
BasicClientCookie bc2=new BasicClientCookie("bid",sharedata.getString("bid",null));
BasicClientCookie bc3=new BasicClientCookie("dbcl2",sharedata.getString("dbcl2",null));
BasicClientCookie bc4=new BasicClientCookie("ck",sharedata.getString("ck",null));

CookieStore cs=new BasicCookieStore();
cs.addCookie(bc1);
cs.addCookie(bc2);
cs.addCookie(bc3);
cs.addCookie(bc4);

Log.d("element","------------");
List<Cookie> cookies=cs.getCookies();
for(int i=0;i<cookies.size();i++)
{
	Cookie ck=cookies.get(i);
    String name=ck.getName();
    String value=ck.getValue();
    Log.d("element",name+"-->"+value);
}
//if(NetUtil.getCkStore()==null)
//NetUtil.setCkStore(cs);
 * 
 */
