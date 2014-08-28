package com.yangkun.lostandfound.bean;

import android.os.Parcel;
import android.os.Parcelable;
import cn.bmob.v3.BmobObject;

public class Lost extends BmobObject implements Parcelable {

	private String title;
	private String describe;
	private String phone;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getObjectId());
		dest.writeString(title);
		dest.writeString(describe);
		dest.writeString(phone);
		dest.writeString(getCreatedAt());
	}

	public static final Parcelable.Creator<Lost> CREATOR = new Parcelable.Creator<Lost>() {
		public Lost createFromParcel(Parcel in) {
			Lost lost = new Lost();
			lost.setObjectId(in.readString());
			lost.setTitle(in.readString());
			lost.setDescribe(in.readString());
			lost.setPhone(in.readString());
			lost.setCreatedAt(in.readString());
			return lost;
		}

		public Lost[] newArray(int size) {
			return new Lost[size];
		}
	};

}
