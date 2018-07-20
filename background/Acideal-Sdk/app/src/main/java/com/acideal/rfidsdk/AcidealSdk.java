package com.acideal.rfidsdk;

/**
 * Created by Administrator on 2017/10/23.
 */

public class AcidealSdk {
    static {
        System.loadLibrary("AcidealSdkSo");
    }

    public native boolean InitReader();
    public native boolean IsInitOK();

    //GetSN return "x" when sdk isn't ready.
    public native String GetSN();

    //ant:0   hand-held device must use 0 antenna
    //dBm:0~30  The power level for the antenna port
    //dwelltime: The number of milliseconds to spend on this antenna port during a cycle.Should not be zero.
    public native boolean SetAntennaPortConfig(int ant, int dBm, int dwelltime);
    //GetAntennaPortConfig return int array which length is 3.
    // Array[0]: 0/failed, 1/success
    // Array[1]: power level,0~30dBm
    // Array[2]: dwelltime
    public native int[] GetAntennaPortConfig(int ant);

    //prf:0~3  LinkProfile
    public native boolean SetProFile(int prf);
    //GetProFile return "x" when sdk isn't ready.
    public native String GetProFile();

    public native boolean IsLowPower();
    public native boolean SetLowPower();
    //flg: 0/power_off, 1/power_on
    public static native boolean SetPowerFlag(int flg);

    //ReadTag return tag epclist separated with "_",return "null" when tag was not fonund,return "x" when sdk isn't ready.
    public native String ReadTag();
    public native boolean WriteTag(byte[] epc, int len);

    public native boolean StartInventory();
    public native boolean StopInventory();
}
