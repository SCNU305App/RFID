package android.zyapi;

import android.content.Context;

public class CommonApi {
	private static CommonApi mMe = null;
	private static Context mContext = null;
	
	public CommonApi() {
	}
	
	public static CommonApi getInstance(Context context){
		mContext = context;
		if(mMe == null){
			mMe = new CommonApi();
		}
		return mMe;
	}
	
	//gpio
	public native int setGpioMode(int pin,int mode);
	public native int setGpioDir(int pin,int dir);
	public native int setGpioPullEnable(int pin,int enable);
	public native int setGpioPullSelect(int pin,int select);
	public native int setGpioOut(int pin,int out);
	public native int getGpioIn(int pin);

	
	static {
		System.loadLibrary("zyapi_common");
	}
}
