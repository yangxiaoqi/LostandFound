package com.yangkun.lostandfound.receiver;

import com.yangkun.lostandfound.MainActivity;
import com.yangkun.lostandfound.R;

import cn.bmob.push.PushConstants;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyPushMessageReceiver extends BroadcastReceiver {
	NotificationManager nm;
	public static final int NOTIFICATION_ID = 0x1;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
			String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			Toast.makeText(context,"客户端发送的消息"+ message, Toast. LENGTH_LONG).show();
			nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification noti = new Notification();
			noti.icon = R.drawable.ic_launcher;
			noti.tickerText = "bmob推送消息";
			RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notimanager);
			view.setTextViewText(R.id.textView_noti_title, "您有一条新消息");
			view.setTextViewText(R.id.textView_noti_mes, message);
			view.setImageViewResource(R.id.imageView_noti, R.drawable.ic_launcher);
			Intent newintent = new Intent(context,MainActivity.class);
			newintent.putExtra("msg", message);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(R.id.noticlick, pendingIntent);
			noti.contentView = view;
			nm.notify(NOTIFICATION_ID,noti);
		}
	}


}
