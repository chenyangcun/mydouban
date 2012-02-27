package com.chenyc.douban;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.douban.NoteEntry;

public class NoteEditActivity extends BaseActivity {

	private EditText edtTitle; // 标题
	private EditText edtContent; // 内容
	private String id;
	private String title;
	private String content;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.note_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		initView();
		setData();

	}

	// 初始化控件
	private void initView() {

		dialog = new ProgressDialog(this);
		edtTitle = (EditText) findViewById(R.id.EditTextTitle);
		edtContent = (EditText) findViewById(R.id.EditTextContent);

		if (id == null) {
			// 光标移到最上面
			edtContent.setGravity(Gravity.TOP);
		} else {
			// 光标移到最下面
			edtContent.setGravity(Gravity.BOTTOM);
		}

		// 隐藏按钮
		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);

		// 回退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goBack();
			}

		});

		// 取消按钮
		Button cancelButton = (Button) findViewById(R.id.btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goBack();
			}

		});

		// 保存按钮
		Button saveButton = (Button) findViewById(R.id.btnSave);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doPost();
			}
		});
	}

	private void doPost() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dialog.hide();
					Intent it = new Intent();
					setResult(1, it);
					finish();
				} else {
					String message = "";
					if (id == null) {
						message = "日记新增失败！";
					} else {
						message = "日记修改失败！";
					}
					Toast.makeText(NoteEditActivity.this, message,
							Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				dialog.setMessage("正在提交数据...");
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... arg0) {
				String title = edtTitle.getText().toString().trim();
				String content = edtContent.getText().toString().trim();
				// 更新
				if (id != null) {
					NoteEntry ne = new NoteEntry();
					ne.setId(id);
					try {
						getDoubanService().updateNote(ne,
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), "private",
								"no");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				// 新增
				else {
					try {
						getDoubanService().createNote(
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), "private",
								"no");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

		}.execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	// 设置数据
	private void setData() {

		Bundle extras = getIntent().getExtras();
		id = extras != null ? extras.getString("id") : null;
		title = extras != null ? extras.getString("title") : null;
		content = extras != null ? extras.getString("content") : null;
		if (title != null) {
			edtTitle.setText(title);
		}
		if (content != null) {
			edtContent.setText(content);
		}

	}

	// 回退
	private void goBack() {
		String title = edtTitle.getText().toString().trim();
		String content = edtContent.getText().toString().trim();

		// 没有内容，直接回退
		if ("".equals(title) && "".equals(content)) {
			finish();
			return;
		}

		// 未做修改，直接回退
		if (title.equals(this.title) && content.equals(this.content)) {
			finish();
			return;
		}

		new AlertDialog.Builder(NoteEditActivity.this).setTitle("提示")
				.setMessage("数据未保存，确定要回退吗？").setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								finish();
							}
						}).setNeutralButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}

						}).show();
	}

}
