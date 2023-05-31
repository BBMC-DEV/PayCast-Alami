package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class SystemUtils {
    private final static String TAG = "SystemUtils";

    public static void shutdown_sys() //power off
    {
        Process chperm;
        Runtime runtime = Runtime.getRuntime();
        try {
            chperm = runtime.exec(new String[]{"su", "-c", "reboot -p"});


            chperm.waitFor();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "1 ERROR =" + e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "2 ERROR =" + e);
        }
    }

    public static void reboot_sys(Context c) //reboot
    {
        PowerManager rpm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        rpm.reboot(null);
    }


    public static void executeMonitorOn(Context c) //monitor on
    {
/*   test 20180713
        PushWakeLock.acquireCpuWakeLock(c);

        // WakeLock 해제.
        PushWakeLock.releaseCpuLock();
*/
        Runtime runtime = Runtime.getRuntime();

        Process ipProcess = null;
        int exitValue = -1;
        boolean isScreenOn = false;

        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isInteractive();
        }
        else
        {
            isScreenOn = pm.isScreenOn();
        }
        if(isScreenOn==false) {
            try {
                ipProcess = runtime.exec("/system/bin/input keyevent 26");
                exitValue = ipProcess.waitFor();
//                Log.d(TAG, "input keyevent 26 exitValue=" + exitValue+ " isScreenOn="+isScreenOn);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
