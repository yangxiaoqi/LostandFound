package com.yangkun.lostandfound;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.yangkun.lostandfound.bean.Found;
import com.yangkun.lostandfound.bean.Lost;

public class AddActivity extends BaseActivity implements OnClickListener{

	private EditText edit_title, edit_phone, edit_describe;
	private Button btn_back, btn_true;

	private TextView tv_add;
	private String from = "";
	
	String old_title = "";
	String old_describe = "";
	String old_phone = "";
	@Override
	public void initListener() {
		btn_back.setOnClickListener(this);
		btn_true.setOnClickListener(this);
	}

	
	private Lost editlost;
	private Found editfound;
	@Override
	public void initData() {
		from = getIntent().getStringExtra("from");
		if("lost".equals(from)){
			tv_add.setText("添加失物信息");
		}else if("found".equals(from)){
			tv_add.setText("添加查找信息");
		}else if("edit_lost".equals(from)){
			tv_add.setText("编辑失物信息");
			editlost = getIntent().getParcelableExtra("lost");
			old_title = editlost.getTitle();
			old_describe = editlost.getDescribe();
			old_phone = editlost.getPhone();
			initViewData();
		}else if("edit_found".equals(from)){
			tv_add.setText("编辑查找信息");
			editfound = getIntent().getParcelableExtra("found");
			old_title = editfound.getTitle();
			old_describe = editfound.getDescribe();
			old_phone = editfound.getPhone();
			initViewData();
		}
	}

	
	private void initViewData() {
		edit_title.setText(old_title);
		edit_describe.setText(old_describe);
		edit_phone.setText(old_phone);
	}

	@Override
	public void initView() {
		tv_add = (TextView) findViewById(R.id.tv_add);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_true = (Button) findViewById(R.id.btn_true);
		edit_phone = (EditText) findViewById(R.id.edit_photo);
		edit_describe = (EditText) findViewById(R.id.edit_describe);
		edit_title = (EditText) findViewById(R.id.edit_title);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_add);
	}

	@Override
	public void onClick(View v) {
		if(btn_back==v){
			finish();
		}else if(btn_true==v){
			if("lost".equals(from)){
				final Lost lost = new Lost();
				lost.setTitle(edit_title.getText().toString());
				lost.setDescribe(edit_describe.getText().toString());
				lost.setPhone(edit_phone.getText().toString());
				lost.save(this,new SaveListener() {
					
					@Override
					public void onSuccess() {
						showToast("添加失物信息成功");
						Intent data = new Intent();
						data.putExtra("lost", lost);
						data.putExtra("lost_op", "add");
						setResult(RESULT_OK, data);
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}else if("found".equals(from)){
				final Found found = new Found();
				found.setTitle(edit_title.getText().toString());
				found.setDescribe(edit_describe.getText().toString());
				found.setPhone(edit_phone.getText().toString());
				found.save(this,new SaveListener() {
					
					@Override
					public void onSuccess() {
						showToast("添加招领信息成功");
						Intent data = new Intent();
						data.putExtra("found", found);
						data.putExtra("found_op", "add");
						setResult(RESULT_OK, data);
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}else if("edit_lost".equals(from)){
				editlost.setTitle(edit_title.getText().toString());
				editlost.setDescribe(edit_describe.getText().toString());
				editlost.setPhone(edit_phone.getText().toString());
				editlost.update(this,editlost.getObjectId(),new UpdateListener() {
					
					@Override
					public void onSuccess() {
						showToast("修改失物信息成功");
						Intent data = getIntent();
						data.putExtra("lost", editlost);
						data.putExtra("lost_op", "update");
						setResult(RESULT_OK, data);
						finish();						
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}else if("edit_found".equals(from)){
				editfound.setTitle(edit_title.getText().toString());
				editfound.setDescribe(edit_describe.getText().toString());
				editfound.setPhone(edit_phone.getText().toString());
				editfound.update(this,editfound.getObjectId(),new UpdateListener() {
					
					@Override
					public void onSuccess() {
						showToast("修改招领信息成功");
						Intent data = getIntent();
						data.putExtra("found", editfound);
						data.putExtra("found_op", "update");
						setResult(RESULT_OK, data);
						finish();						
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}
		}
	}

}
