package com.chenyc.douban.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chenyc.douban.R;
import com.chenyc.douban.entity.Subject;
import com.chenyc.douban.util.NetUtil;
import com.chenyc.douban.util.ViewCache;
import com.chenyc.douban.util.AsyncImageLoader.ImageCallback;

public class SubjectListAdapter extends BaseAdapter {
	private List<Subject> subjects;
	private LayoutInflater mInflater;
	private ListView listView;

	public SubjectListAdapter(Context context, ListView listView, List<Subject> subjects) {
		this.listView = listView;
		this.subjects = subjects;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return subjects.size();
	}

	public Object getItem(int i) {
		return subjects.get(i);
	}

	public long getItemId(int i) {
		return i;
	}

	public View getView(int i, View view, ViewGroup vg) {
		ViewCache viewCache;
		if (view == null) {
			view = mInflater.inflate(R.layout.book_item, null);
			viewCache = new ViewCache(view);
			view.setTag(viewCache);
		} else {
			viewCache = (ViewCache) view.getTag();
		}
		Subject book = subjects.get(i);

		TextView txtTitle = (TextView) view.findViewById(R.id.book_title);
		txtTitle.setText(book.getTitle());

		TextView txtDescription = (TextView) view
				.findViewById(R.id.book_description);
		txtDescription.setText(book.getDescription());

		RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
		// 首页，没取到评分，则不显示RatingBar
		if (Math.abs(book.getRating() + 1) < 0.1) {
			ratingBar.setVisibility(View.INVISIBLE);
		} else {
			ratingBar.setVisibility(View.VISIBLE);
			ratingBar.setRating(book.getRating());
		}

		String imgUrl = book.getImgUrl();
		ImageView imgBook = viewCache.getImageView();
		imgBook.setTag(imgUrl);
		Drawable drawable = NetUtil.asyncImageLoader.loadDrawable(imgUrl,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
						}
					}
				});

		if (drawable != null) {
			imgBook.setImageDrawable(drawable);
		} else {
			imgBook.setImageResource(R.drawable.book);
		}

		return view;
	}
}