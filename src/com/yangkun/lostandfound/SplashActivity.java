package com.yangkun.lostandfound;

import android.content.Intent;
import android.os.Handler;

//…¡∆¡“≥
public class SplashActivity extends BaseActivity {

	private static final int GOHOME = 0x1;
	@Override
	public void initListener() {

	}

	@Override
	public void initData() {
		handler.sendEmptyMessageDelayed(GOHOME, 3000);
	}

	@Override
	public void initView() {
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_splash);
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GOHOME:
				gohome();
				break;

			default:
				break;
			}
		};
	};
	protected void gohome() {
		Intent intent = new Intent(this,MainActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
}
