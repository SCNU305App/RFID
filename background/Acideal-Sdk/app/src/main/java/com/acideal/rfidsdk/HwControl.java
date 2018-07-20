package com.acideal.rfidsdk;
import android.content.Intent;
/**
 * Created by Administrator on 2017/11/2.
 */

import android.content.Context;
import android.os.SystemClock;
import android.zyapi.CommonApi;

import java.util.Date;

public class HwControl  extends Thread  {
    private static final String TAG = "HwControl";
    public static CommonApi mCommonApi;
    private static int mComFd;

    private Intent intent;
    private Context context;
    protected boolean isrunning = false;

    HwControl(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        mCommonApi = CommonApi.getInstance(context);
        mCommonApi.setGpioMode(63,0);
        mCommonApi.setGpioDir(63, 0);
        mCommonApi.setGpioMode(64,0);
        mCommonApi.setGpioDir(64, 0);
        mCommonApi.setGpioMode(3,0);
        mCommonApi.setGpioDir(3, 1);
        //mCommonApi.setGpioPullEnable(3,1);
        //mCommonApi.setGpioPullSelect(3,1);
        mCommonApi.setGpioOut(3,0);
        mCommonApi.setGpioMode(4,0);
        mCommonApi.setGpioDir(4, 1);
        mCommonApi.setGpioPullEnable(4,1);
        mCommonApi.setGpioPullSelect(4,1);
        //mCommonApi.setGpioOut(4,1);
        mCommonApi.setGpioOut(4,0);

        intent = new Intent();

        SystemClock.sleep(500);
        //power_off();
    }

    public static boolean get_power_ok()
    {
        if(mCommonApi != null)
        {
            int state=mCommonApi.getGpioIn(4);
            if(state == 0)
            {
                return false;
            }
            else if(state == 1)
            {
                return true;
            }
            //return true;
        }
        return false;
    }

    public boolean power_on()
    {
        if(mCommonApi != null)
        {
            mCommonApi.setGpioOut(4, 1);
            AcidealSdk.SetPowerFlag(1);
            SystemClock.sleep(500);
            return true;
        }
        return false;
    }

    public boolean power_off()
    {
        if(mCommonApi != null)
        {
            mCommonApi.setGpioOut(4, 0);
            AcidealSdk.SetPowerFlag(0);
            return true;
        }
        return false;
    }

    public boolean reset_up()
    {
        if(mCommonApi != null)
        {
            mCommonApi.setGpioOut(3, 1);
            SystemClock.sleep(500);
            return true;
        }
        return false;
    }

    public boolean reset_down()
    {
        if(mCommonApi != null)
        {
            mCommonApi.setGpioOut(3, 0);
            return true;
        }
        return false;
    }


    @Override
    public void run() {

        int Inventory=0;
        int ReadWrite=0;
        boolean startInv=false;

        while(true)
        {
            for(int i=0; i<3;i++)
            {
                SystemClock.sleep(160);
                if(mCommonApi.getGpioIn(63) == 0)
                {
                    ReadWrite++;
                }
                if(mCommonApi.getGpioIn(64) == 0)
                {
                    Inventory++;
                }
            }

            //while(mCommonApi.getGpioIn(63) == 0);
            //while(mCommonApi.getGpioIn(64) == 0);

            if(Inventory>0)
            {
                if(startInv==false)
                {
                    startInv=true;

                    intent.setAction("acrfid.HwControl.ACTION");
                    intent.putExtra("button", "btnInventory");
                    context.sendBroadcast(intent);
                }
                else
                {
                    intent.setAction("acrfid.HwControl.ACTION");
                    intent.putExtra("button", "btnKeepInv");
                    context.sendBroadcast(intent);
                }
                Inventory=0;
            }
            else
            {
                if(startInv)//btnStopInv
                {
                    startInv=false;
                }
            }

            if(ReadWrite>0)
            {
                intent.setAction("acrfid.HwControl.ACTION");
                intent.putExtra("button", "btnReadWrite");
                context.sendBroadcast(intent);
                ReadWrite=0;
            }
        }
    }

}
