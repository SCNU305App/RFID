package com.acideal.rfidsdk;

import android.os.Message;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/15.
 */

public  class SdkMsg {
    static Map<String, Integer> mEpcCountmap=MainActivity.mEpcCountmap;//= new HashMap<String, Integer>();


    //InvCallBackProc is a callback function when inventory tags, it do must not be removed !!!
    public static  void InvCallBackProc(int msgType, String strStatus)
    {
        if(msgType==2)
        {
            if(mEpcCountmap.containsKey(strStatus))
            {
                mEpcCountmap.put(strStatus , mEpcCountmap.get(strStatus)+1);
            }
            else
            {
                mEpcCountmap.put(strStatus , 1);
            }
        }
        /*
        Message tMsg=new Message();
        tMsg.what = msgType;
        tMsg.arg1 = 2;
        tMsg.arg2 = 3;
        tMsg.obj = strStatus;
        mHandler.sendMessage(tMsg);
        */
    }
}
