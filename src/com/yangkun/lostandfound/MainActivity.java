package com.yangkun.lostandfound;

import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.yangkun.lostandfound.base.EditPopupWindow;
import com.yangkun.lostandfound.bean.Found;
import com.yangkun.lostandfound.bean.Lost;
import com.yangkun.lostandfound.config.Constants;
import com.yangkun.lostandfound.i.IPopupItemClick;
import com.yangkun.lostandfound.receiver.MyPushMessageReceiver;

public class MainActivity extends BaseActivity implements OnClickListener,
		OnItemLongClickListener,IPopupItemClick{

	private RelativeLayout layout_action;// 标题栏
	private LinearLayout layout_all; // 标题栏中间下拉视图
	private TextView tv_lost;
	private ListView listview;
	private Button btn_add;

	private Button layout_found;
	private Button layout_lost;
	private PopupWindow popwindow_action;

	private RelativeLayout progress;
	private LinearLayout layout_no;
	private TextView tv_no;

	private List<Lost> losts;
	private LostAdapter lostAdapter;

	private List<Found> founds;
	private FoundAdapter foundAdapter;

	@Override
	public void initListener() {
		layout_all.setOnClickListener(this);
		listview.setOnItemLongClickListener(this);
		btn_add.setOnClickListener(this);
	}

	@Override
	public void initData() {
		findLostAll();
//		findFoundAll();
	}

	private void findFoundAll() {
		BmobQuery<Found> query = new BmobQuery<Found>();
		query.order("-createdAt");
		query.findObjects(this, new FindListener<Found>() {

			@Override
			public void onSuccess(List<Found> arg0) {
				MainActivity.this.founds = arg0;
				foundAdapter = new FoundAdapter();
				listview.setAdapter(foundAdapter);
				showview();
			}

			@Override
			public void onError(int arg0, String arg1) {
				showErrorview(1);
			}
		});
	}

	private void findLostAll() {
		BmobQuery<Lost> query = new BmobQuery<Lost>();
		query.order("-createdAt");
		query.findObjects(this, new FindListener<Lost>() {

			@Override
			public void onSuccess(List<Lost> arg0) {
				MainActivity.this.losts = arg0;
				lostAdapter = new LostAdapter();
				listview.setAdapter(lostAdapter);
				showview();
			}

			@Override
			public void onError(int arg0, String arg1) {
				showErrorview(0);
			}
		});
	}

	protected void showErrorview(int tag) {
		listview.setVisibility(View.GONE);
		layout_no.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		if (tag == 0) {
			tv_no.setText(getResources().getString(R.string.list_no_data_lost));
		} else {
			tv_no.setText(getResources().getString(R.string.list_no_data_found));
		}

	}

	protected void showview() {
		listview.setVisibility(View.VISIBLE);
		layout_no.setVisibility(View.GONE);
		progress.setVisibility(View.GONE);
	}

	@Override
	public void initView() {
		progress = (RelativeLayout) findViewById(R.id.progress);
		layout_no = (LinearLayout) findViewById(R.id.layout_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		layout_action = (RelativeLayout) findViewById(R.id.layout_action);
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		// 默认是失物界面
		tv_lost = (TextView) findViewById(R.id.tv_lost);
		tv_lost.setTag("lost");
		listview = (ListView) findViewById(R.id.list_lost);
		btn_add = (Button) findViewById(R.id.btn_add);
		initEditPop();
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_main);
		// 使用推送服务时的初始化操作
	    BmobInstallation.getCurrentInstallation(this).save();
	    // 启动推送服务
	    BmobPush.startWork(this, Constants.APP_ID);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		position = arg2;
		int[] location = new int[2];
		arg1.getLocationOnScreen(location);
		editpopup.showAtLocation(arg1, Gravity.RIGHT | Gravity.TOP, 
				location[0], getStateBar() + location[1]);
		return false;
	}

	@Override
	public void onClick(View v) {
		if (btn_add == v) {
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra("from", tv_lost.getTag().toString());
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		} else if (layout_all == v) {
			showListpop();
		} else if (layout_found == v) {
			tv_lost.setText(getResources().getString(R.string.found));
			tv_lost.setTag("found");
			findFoundAll();
		} else if (layout_lost == v) {
			tv_lost.setText(getResources().getString(R.string.lost));
			tv_lost.setTag("lost");
			findLostAll();
		}
	}

	private void showListpop() {
		View v = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);
		// 事件的注入
		layout_found = (Button) v.findViewById(R.id.layout_found);
		layout_lost = (Button) v.findViewById(R.id.layout_lost);
		layout_found.setOnClickListener(this);
		layout_lost.setOnClickListener(this);

		// 弹出窗口layout_action来显示layout_found和layout_lost 点击事件在onclick方法中
		popwindow_action = new PopupWindow(v, mScreenWidth, 600);
		popwindow_action.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
					popwindow_action.dismiss();
					return true;
				}
				return false;
			}
		});
		
		popwindow_action.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		popwindow_action.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popwindow_action.setTouchable(true);
		popwindow_action.setFocusable(true);
		popwindow_action.setOutsideTouchable(true);
		popwindow_action.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从顶部弹下
		popwindow_action.setAnimationStyle(R.style.MenuPop);
		popwindow_action.showAsDropDown(layout_action, 0, -dip2px(this, 2.0F));
		
	}

	// 招领适配器
	class FoundAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return founds.size();
		}

		@Override
		public Object getItem(int position) {
			return founds.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_list,
						null);
				vh = new ViewHolder();
				vh.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				vh.tv_describe = (TextView) convertView
						.findViewById(R.id.tv_describe);
				vh.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_photo);
				vh.tv_createdAt = (TextView) convertView
						.findViewById(R.id.tv_time);
				convertView.setTag(vh);
			}
			vh = (ViewHolder) convertView.getTag();
			Found found = founds.get(position);
			vh.tv_title.setText(found.getTitle());
			vh.tv_describe.setText(found.getDescribe());
			vh.tv_phone.setText(found.getPhone());
			vh.tv_createdAt.setText(found.getCreatedAt());
			final String number = vh.tv_phone.getText().toString();
			vh.tv_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + number));
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	// 失物适配器
	class LostAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return losts.size();
		}

		@Override
		public Object getItem(int position) {
			return losts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_list,
						null);
				vh = new ViewHolder();
				vh.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				vh.tv_describe = (TextView) convertView
						.findViewById(R.id.tv_describe);
				vh.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_photo);
				vh.tv_createdAt = (TextView) convertView
						.findViewById(R.id.tv_time);
				convertView.setTag(vh);
			}
			vh = (ViewHolder) convertView.getTag();
			Lost lost = losts.get(position);
			vh.tv_title.setText(lost.getTitle());
			vh.tv_describe.setText(lost.getDescribe());
			vh.tv_phone.setText(lost.getPhone());
			vh.tv_createdAt.setText(lost.getCreatedAt());
			final String number = vh.tv_phone.getText().toString();
			vh.tv_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + number));
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_describe;
		TextView tv_phone;
		TextView tv_createdAt;
	}

	private void initEditPop() {
		editpopup = new EditPopupWindow(this, 200, 48);
		editpopup.setOnPopupItemClickListner(this);
	}
	
	EditPopupWindow editpopup;
	int position;
	@Override
	public void onEdit(View v) {
		if("lost".equals(tv_lost.getTag().toString())){
			Lost lost = (Lost) lostAdapter.getItem(position);
			Intent intent = new Intent(this,AddActivity.class);
			intent.putExtra("lost", lost);
			intent.putExtra("from", "edit_lost");
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		}else if("found".equals(tv_lost.getTag().toString())){
			Found found = (Found) foundAdapter.getItem(position);
			Intent intent = new Intent(this,AddActivity.class);
			intent.putExtra("found", found);
			intent.putExtra("from", "edit_found");
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		}
	}

	
	@Override
	public void onDelete(View v) {
		if("lost".equals(tv_lost.getTag().toString())){
			Lost deletelost = (Lost) lostAdapter.getItem(position);
			deletelost.delete(this, deletelost.getObjectId(), new DeleteListener() {
				
				@Override
				public void onSuccess() {
					showToast("删除丢失信息成功");
					losts.remove(position);
					lostAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					
				}
			});
		}else if("found".equals(tv_lost.getTag().toString())){
			Found deletefound = (Found) foundAdapter.getItem(position);
			deletefound.delete(this, deletefound.getObjectId(), new DeleteListener() {
				
				@Override
				public void onSuccess() {
					showToast("查找信息删除成功");
					founds.remove(position);
					foundAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					
				}
			});
		}
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==Constants.REQUESTCODE_ADD && resultCode==RESULT_OK){
			if("lost".equals(tv_lost.getTag().toString())){
				Lost lost = data.getParcelableExtra("lost");
				String msg = data.getStringExtra("lost_op");
				if("add".equals(msg)){
					losts.add(0,lost);
				}else if("update".equals(msg)){                   
					Lost old = (Lost)lostAdapter.getItem(position);
					old.setTitle(lost.getTitle());
					old.setDescribe(lost.getDescribe());
					old.setPhone(lost.getPhone());
				}
				lostAdapter.notifyDataSetChanged();
			}else if("found".equals(tv_lost.getTag().toString())){
				Found found = data.getParcelableExtra("found");
				String msg = data.getStringExtra("found_op");
				if("add".equals(msg)){
					founds.add(0,found);
				}else if("update".equals(msg)){
					Found oldfound = (Found) foundAdapter.getItem(position);
					oldfound.setTitle(found.getTitle());
					oldfound.setDescribe(found.getDescribe());
					oldfound.setPhone(found.getPhone());
				}
				foundAdapter.notifyDataSetChanged();
			}
		}
	}
}
