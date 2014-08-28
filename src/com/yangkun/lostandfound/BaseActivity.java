package com.yangkun.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

import com.yangkun.lostandfound.config.Constants;

public abstract class BaseActivity extends Activity {

	protected int mScreenWidth;
	protected int mScreenHeight;

	public static final String TAG = "bmob";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bmob.initialize(this, Constants.APP_ID);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;

		setContentView();
		initView();
		initListener();
		initData();
	}

	public abstract void initListener();

	public abstract void initData();

	public abstract void initView();

	public abstract void setContentView();
	
	
	
	Toast mtoast;
	
	public void showToast(String info){
		if(!TextUtils.isEmpty(info)){
			if(mtoast==null){
				mtoast = Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT);
			}else{
				mtoast.setText(info);
			}
			mtoast.show();
		}
	}
	
	public int getStateBar(){
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statebarHeight = frame.top;
		return statebarHeight;
	}
	
	public static int dip2px(Context context,float dipValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (scale*dipValue+0.5f);
	}
}
