package com.acideal.rfidsdk;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemClock;
import java.lang.NumberFormatException;
import java.util.Date;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Message;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnPrfGet,btnPrfSet,btnPowerGet,btnPowerSet,btnWriteTag,btnReadTag,btnClear;
    private Button btnSetLowPower,btnPowerOnOff,btnStopInv,btnStartInv;
    private TextView tvEPC,tvStatus,tvSN,tvInit,tvMsg;
    private EditText editText_EPC,editText_Prf,editText_Power,editText_DwellTime;

    private HwControl mHwControl;
    private AcidealSdk mAcidealSdk;

    private boolean mIsSetLowPower=false;
    //private boolean mIsRFShutdown=false;
    private boolean mIsStartInv=false;
    private Timer timer;
    private Date mLastTime;
    private Date mStartInvTime;
    boolean bIsStopInv=true;

    static long lInvLastTime=0;

    static Map<String, Integer> mEpcCountmap = new ConcurrentHashMap<String, Integer>();
    private ReentrantLock mReentrantLock = new ReentrantLock();

    static String sStrMsg = "";
    MainHandler   mMainHandler;
    static MainHandler mHandler;

    private BTNBroadcastReceiver receiver;

    public class BTNBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO 自动生成的方法存根
            String action = intent.getAction();
            if(action.toString().equals("acrfid.HwControl.ACTION"))
            {
                String msg = (String) intent.getExtras().get("button");
                //DebugLog.d(TAG,"SendMsg:"+msg);

                if(msg.equals("btnInventory"))//right_btn
                {
                    StartInv();
                }
                else if(msg.equals("btnReadWrite"))//left_btn
                {
                    StartReadTag();
                }
                if(msg.equals("btnKeepInv"))
                {
                    Log.d("BTNBroadcastReceiver","btnKeepInv");
                    if(mIsStartInv && !bIsStopInv)
                    {
                        FreshLastOpTime();
                        lInvLastTime+=500;
                    }

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrfGet = (Button) findViewById(R.id.button_getprf);
        btnPrfSet = (Button) findViewById(R.id.button_setprf);
        btnPowerGet = (Button) findViewById(R.id.button_getpwr);
        btnPowerSet = (Button) findViewById(R.id.button_setpwr);
        btnWriteTag = (Button) findViewById(R.id.button_writetag);
        btnReadTag = (Button) findViewById(R.id.button_readtag);
        btnClear = (Button) findViewById(R.id.button_clear);
        btnSetLowPower = (Button) findViewById(R.id.button_setlowpwr);
        btnPowerOnOff = (Button) findViewById(R.id.button_power);

        btnStopInv = (Button) findViewById(R.id.button_StopInv);
        btnStartInv = (Button) findViewById(R.id.button_StartInv);

        tvEPC = (TextView) findViewById(R.id.textView);
        tvStatus = (TextView) findViewById(R.id.textView_status);
        tvSN = (TextView) findViewById(R.id.textView_sn);
        tvInit = (TextView) findViewById(R.id.textView_init);

        editText_EPC=(EditText) findViewById(R.id.editText_EPC);
        editText_Prf=(EditText) findViewById(R.id.editText_profile);
        editText_Power=(EditText) findViewById(R.id.editText_power);
        editText_DwellTime=(EditText) findViewById(R.id.editText_dwelltime);

        //editText_Inv=(EditText) findViewById(R.id.editText_Inv);
        tvMsg = (TextView) findViewById(R.id.textView_msg);

        mMainHandler=new MainHandler();
        mHandler=mMainHandler;

        mHwControl=new HwControl(MainActivity.this);
        mHwControl.start();
        //SystemClock.sleep(300);

        if(mHwControl.get_power_ok())
        {
            tvStatus.setText("模式：正常");
        }
        else
            tvStatus.setText("模式：射频失电");

        mAcidealSdk=new AcidealSdk();
/*
        if(mAcidealSdk.InitReader())
        {
            tvInit.setText("初始化：成功");
            //Toast.makeText(MainActivity.this, "读写器初始化成功！", Toast.LENGTH_SHORT).show();
            tvSN.setText("SN: "+mAcidealSdk.GetSN());
        }
        else
        {
            tvInit.setText("初始化：失败");
            Toast.makeText(MainActivity.this, "读写器初始化失败！", Toast.LENGTH_SHORT).show();
        }
*/
        mLastTime=new Date();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                //System.out.println("-------设定要指定任务--------");
                Date now=new Date();
                long l=now.getTime()-mLastTime.getTime();
                final long min=l/(60*1000);
                if(min >= 3 && min <5 )
                {

                    if(!mIsSetLowPower) {
                        if (mHwControl.get_power_ok()) {

                            if(mAcidealSdk.SetLowPower()) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在Post中操作UI组件
                                        tvStatus.setText("模式：低功耗(自动)");
                                        Toast.makeText(MainActivity.this, "低功耗运行(自动)", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                mIsSetLowPower=true;
                            }
                        }
                    }

                }
                else if(min >= 5)
                {
                    //if(!mIsRFShutdown)
                    {
                        if (mHwControl.get_power_ok()) {
                            if (mHwControl.power_off()) {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在Post中操作UI组件
                                        tvStatus.setText("模式：射频关电(自动)");
                                        Toast.makeText(MainActivity.this, "射频关电(自动)", Toast.LENGTH_SHORT).show();
                                        btnPowerOnOff.setText("打开射频电源");
                                        tvInit.setText("初始化： ");
                                        tvSN.setText("SN: ");
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }, 3000, 10000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int runCount=0;
                    //Date now = new Date();
                    //boolean bIsStopInv=false;
                    while(true) {
                        //SystemClock.sleep(500);
                        Thread.sleep(300);

                        if (mIsStartInv) {
                            long l = new Date().getTime() - mStartInvTime.getTime();
                            System.out.println("Thread  mIsStartInv last:"+Long.toString(l) +"ms, timing:"+Long.toString(lInvLastTime)+"ms");
                            //Log.d("mAcidealSdk","mIsStartInv :"+Long.toString(l));

                            if(l > lInvLastTime && bIsStopInv==false)
                            {
                                if(mAcidealSdk.StopInventory())
                                {
                                    System.out.println("Thread  StopInventory 盘点停止 ");
                                    /*
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "盘点停止", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    */
                                    //bIsStopInv=true;
                                }
                                else
                                {
                                    System.out.println("Thread  StopInventory 盘点停止失败 ");
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Toast.makeText(MainActivity.this, "盘点停止失败！", Toast.LENGTH_SHORT).show();
                                            tvMsg.setText("盘点停止失败！");
                                        }
                                    });
                                }
                            }



                            runCount++;
                            if(runCount>2)
                            {
                                String str1 = "";
                                for (Map.Entry<String, Integer> entry : mEpcCountmap.entrySet()) {
                                    //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                                    str1 = str1 + entry.getKey() + " [" + Integer.toString(entry.getValue()) + "]    ";
                                }
                                final String str = str1;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvEPC.setText(str);
                                        //editText_Inv.setText(str);
                                    }
                                });

                                runCount=0;
                            }


                            if (l >= lInvLastTime+1000)
                            {
                                if(bIsStopInv)
                                    mIsStartInv = false;
                                //bIsStopInv=false;
                                System.out.println("Thread runtime>= lInvLastTime+1500 mIsStartInv ="+bIsStopInv +",  mIsStartInv ="+mIsStartInv);
                            }

                        }

                    }
                }catch (InterruptedException e) {
                    System.out.println("Thread  interrupted.");
                }
            }
        }).start();

        btnStartInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartInv();
            }
        });

        btnStopInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                //if(btnPowerOnOff.getText()=="打开射频电源")
                mIsStartInv=false;
                if(mAcidealSdk.StopInventory())
                {
                    Toast.makeText(MainActivity.this, "停止成功", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "停止失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSetLowPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                if(mIsStartInv)
                {
                    Toast.makeText(MainActivity.this, "盘点标签操作扔在进行，请稍后再操作！", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(mAcidealSdk.IsLowPower())
                {
                    Toast.makeText(MainActivity.this, "已是低功耗", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mHwControl.get_power_ok())
                {
                    if(mAcidealSdk.SetLowPower())
                    {
                        tvStatus.setText("模式：低功耗");
                        Toast.makeText(MainActivity.this, "设置低功耗成功！", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(MainActivity.this, "设置低功耗失败！", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "射频失电，无法操作！", Toast.LENGTH_SHORT).show();
            }
        });
        btnPowerOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                //if(btnPowerOnOff.getText()=="打开射频电源")
                if(!mHwControl.get_power_ok())
                {
                    DevPowerOnAndInit();
                }
                else
                {
                    DevPowerOff();
                }
            }
        });

        btnReadTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartReadTag();
            }
        });
        btnWriteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                if(!CheckBeforeOperation()) return;
                String sepc = editText_EPC.getText().toString().trim();
                if(sepc.indexOf("_")>0 || sepc.indexOf("x")>0)
                {
                    Toast.makeText(MainActivity.this, "写入格式错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sepc.length()%4 !=0 || sepc.length() >512 || sepc.length() <= 0)
                {
                    Toast.makeText(MainActivity.this, "写入格式错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                sepc=sepc.replace(" ", "");
                final byte bepc[] = sepc.getBytes();//String转换为byte[]
                final  int elen= sepc.length();
                //char[] bepc=sepc.toCharArray();
                //editText_Prf.setText(String.valueOf(sepc.length()));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String str1 = "写入成功";
                            if(!mAcidealSdk.WriteTag(bepc,elen))
                            {
                                str1 = "写入失败";
                            }

                            final String str = str1;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                                }
                            });
                            Thread.sleep(10);

                        }catch (InterruptedException e) {
                            System.out.println("Thread WriteTag interrupted.");
                        }
                    }
                }).start();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_EPC.setText("");
            }
        });
        btnPrfGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                if(!CheckBeforeOperation()) return;
                editText_Prf.setText(mAcidealSdk.GetProFile());
                Toast.makeText(MainActivity.this, "读取Profile完成", Toast.LENGTH_SHORT).show();
            }
        });
        btnPrfSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                if(!CheckBeforeOperation()) return;
                try {
                    int prf=Integer.valueOf( editText_Prf.getText().toString().trim() );
                    if(mAcidealSdk.SetProFile(prf))
                    {
                        Toast.makeText(MainActivity.this, "设置Profile成功！", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "设置Profile失败！", Toast.LENGTH_SHORT).show();
                    }
                }catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Toast.makeText(MainActivity.this, "设置Profile数据错误："+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnPowerGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshLastOpTime();
                if(!CheckBeforeOperation()) return;
                int[] param = new int[3];
                param = mAcidealSdk.GetAntennaPortConfig(0);
                //String str_power=String.valueOf(param[1])+ ", " +String.valueOf(param[2]) ;
                if(param[0]!=1)
                {
                    Toast.makeText(MainActivity.this, "读取功率、DwellTime错误！", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    editText_Power.setText(String.valueOf(param[1]));
                    editText_DwellTime.setText(String.valueOf(param[2]));
                    Toast.makeText(MainActivity.this, "读取功率、DwellTime完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPowerSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FreshLastOpTime();
                    if(!CheckBeforeOperation()) return;
                    int power = Integer.parseInt(editText_Power.getText().toString().trim());
                    int DwellTime = Integer.parseInt(editText_DwellTime.getText().toString().trim());
                    if (mAcidealSdk.SetAntennaPortConfig(0, power, DwellTime)) {
                        Toast.makeText(MainActivity.this, "设置功率、DwellTime成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "设置功率、DwellTime失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Toast.makeText(MainActivity.this, "设置数据错误："+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("acrfid.HwControl.ACTION");
        receiver = new BTNBroadcastReceiver();
        registerReceiver(receiver, filter);

    }

    public void StartReadTag()
    {
        FreshLastOpTime();
        if(!CheckBeforeOperation()) return;
        //tvMsg.setText("读取标签...");
        String res=mAcidealSdk.ReadTag();
        editText_EPC.setText(res);
        //res=res.replace("_", "\r\n");
        res=res.replace("_", "       ");
        tvEPC.setText(res);
        //tvMsg.setText("读取标签结束");
        tvMsg.setText("");
        //Toast.makeText(MainActivity.this, "读取标签结束", Toast.LENGTH_SHORT).show();
        if(res.equals("x"))
            Toast.makeText(MainActivity.this, "读取标签错误", Toast.LENGTH_SHORT).show();
    }

    public void StartInv()
    {
        FreshLastOpTime();
        //if(btnPowerOnOff.getText()=="打开射频电源")
        if(!CheckBeforeOperation()) return;

        mEpcCountmap.clear();

        //editText_Inv.setText("开始盘点...");
        tvEPC.setText("");
        tvMsg.setText("正在盘点...");

        mStartInvTime=new Date();
        mIsStartInv=true;
        lInvLastTime=500;
        bIsStopInv=false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str1 = "盘点成功";
                    mReentrantLock.lock();
                    if(mAcidealSdk.StartInventory())
                    {
                        //Toast.makeText(MainActivity.this, "盘点成功", Toast.LENGTH_SHORT).show();
                        System.out.println("Thread StartInventory  盘点成功 ");
                    }
                    else
                    {
                        str1 = "盘点完成";
                        System.out.println("Thread StartInventory  盘点完成 ");
                        //Toast.makeText(MainActivity.this, "盘点失败", Toast.LENGTH_SHORT).show();
                    }
                    mReentrantLock.unlock();
                    bIsStopInv=true;

                    final String str = str1;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "盘点结束", Toast.LENGTH_SHORT).show();
                            tvMsg.setText("盘点结束");
                        }
                    });
                    Thread.sleep(10);

                }catch (InterruptedException e) {
                    System.out.println("Thread StartInventory interrupted.");
                }
            }
        }).start();

    }

    public boolean DevPowerOn()
    {
        if(mHwControl.power_on())
        {
            tvStatus.setText("模式：正常");
            btnPowerOnOff.setText("关闭射频电源");

            return true;
        }
        else
        {
            //tvStatus.setText("模式：射频关电");
            //btnPowerOnOff.setText("打开射频电源");
            Toast.makeText(MainActivity.this, "射频上电失败！", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean DevInit()
    {
        if(mAcidealSdk==null)return false;
        if(mAcidealSdk.InitReader())
        {
            tvInit.setText("初始化：成功");
            tvSN.setText("SN: "+mAcidealSdk.GetSN());
            Toast.makeText(MainActivity.this, "读写器初始化成功！", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            tvInit.setText("初始化：失败");
            Toast.makeText(MainActivity.this, "读写器初始化失败！", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean DevPowerOnAndInit()
    {
        if(mHwControl.get_power_ok())
        {
            //mIsRFShutdown=false;
            if(mAcidealSdk.IsInitOK())
            {
                tvStatus.setText("模式：正常");
                btnPowerOnOff.setText("关闭射频电源");
                tvInit.setText("初始化：成功");
                tvSN.setText("SN: "+mAcidealSdk.GetSN());
                Toast.makeText(MainActivity.this, "读写初始化成功", Toast.LENGTH_SHORT).show();
            }
            else
            {
                return DevInit();
            }
        }
        else
        {
            if(DevPowerOn())
                return DevInit();
        }
        return true;
    }

    public boolean DevPowerOff()
    {
        if(mHwControl.get_power_ok())
        {
            if(mHwControl.power_off())
            {
                tvStatus.setText("模式：射频关电");
                Toast.makeText(MainActivity.this, "射频关电！", Toast.LENGTH_SHORT).show();
                btnPowerOnOff.setText("打开射频电源");
                tvInit.setText("初始化： ");
                tvSN.setText("SN: ");
            }
            else
            {
                Toast.makeText(MainActivity.this, "射频关电失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            //tvStatus.setText("模式：射频关电");
            Toast.makeText(MainActivity.this, "射频关电", Toast.LENGTH_SHORT).show();
            //btnPowerOnOff.setText("打开射频电源");
            //tvInit.setText("初始化： ");
            //tvSN.setText("SN: ");
        }
        return true;
    }

    public void FreshLastOpTime()
    {
        mLastTime=new Date();
        mIsSetLowPower=false;
    }

    public boolean CheckBeforeOperation()
    {
        if(!mHwControl.get_power_ok())
        {
            btnPowerOnOff.callOnClick();
            //Toast.makeText(MainActivity.this, "射频已关电，请开启射频电源再操作！", Toast.LENGTH_SHORT).show();
            if(!mHwControl.get_power_ok())
                return false;
        }
        if(mAcidealSdk.IsLowPower())
        {
            if(mAcidealSdk==null)return false;
            if(mAcidealSdk.InitReader())
            {
                tvInit.setText("初始化：成功");
                tvStatus.setText("模式：正常(自动)");
                tvSN.setText("SN: "+mAcidealSdk.GetSN());
                ///Toast.makeText(MainActivity.this, "读写器初始化成功！", Toast.LENGTH_SHORT).show();
            }
            else
            {
                tvInit.setText("初始化：失败");
                //Toast.makeText(MainActivity.this, "读写器初始化失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(mIsStartInv)
        {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, "盘点标签操作扔在进行，请稍后再操作！", Toast.LENGTH_SHORT).show();
//                }
//            });
            return false;
        }
        if(!bIsStopInv)
            return false;
        return true;
    }

    class MainHandler extends Handler
    {
        public MainHandler(){}
        public MainHandler(Looper L)
        {
            super(L);
        }

        public void handleMessage(Message nMsg)
        {
            super.handleMessage(nMsg);

            String tCmd=(String)nMsg.obj;
            switch(nMsg.what){
                case 1 :
                    tvStatus.setText(tCmd);
                    break;
                default: break;
            }

        }
    }


    public void onStart() {
        super.onStart();  // Always call the superclass method first

    }

    public void onPause() {
        //DevPowerOff();
        super.onPause();
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
        DevPowerOnAndInit();
    }

    public void onStop() {
        DevPowerOff();
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();  // Always call the superclass method first

    }

    public void onDestroy() {
        DevPowerOff();
        super.onDestroy();
    }

}
