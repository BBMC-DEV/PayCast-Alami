package kr.co.bbmc.paycastdid;

import android.app.Application;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import kr.co.bbmc.selforderutil.CommandObject;
import kr.co.bbmc.selforderutil.DidOptionEnv;
import kr.co.bbmc.selforderutil.FileUtils;

public class DidExternalVarApp extends Application  implements Thread.UncaughtExceptionHandler{
    public static String TAG =	"DidExternalVarApp";
    public static final String ACTION_SERVICE_COMMAND = "kr.co.bbmc.paycastdid.serviceCommand";
    public static final String ACTION_ACTIVITY_UPDATE = "kr.co.bbmc.paycastdid.activityupdate";
    public static int  MAX_ALARAM_COUNT_PER_SCREEN = 4; //8->4로 전시회를 위해 수정
    public static boolean LOG_SAVE = true;

    public DidOptionEnv mDidStbOpt;
    public String token = "";
    public ArrayList<CommandObject> newcommandList = new ArrayList<CommandObject>();
    public ArrayList<CommandObject> commandList = new ArrayList<CommandObject>();
    public List<DidAlarmData> alarmDataList = new ArrayList<DidAlarmData>();
    public List<BlinkAlarmData> blinkDataList = new ArrayList<BlinkAlarmData>();
    public List<DidAlarmData> completeList = new ArrayList<DidAlarmData>();
    public List<String> alarmIdList = new ArrayList<String>();

    private List<OrderListItem> mDidOrderList = new ArrayList<OrderListItem>();
    private int mWaitOrderCount = -1;
    /**
     * Storage for the original default crash handler.
     */
    private Thread.UncaughtExceptionHandler defaultHandler;
    /**
     * Installs a new exception handler.
     */
    public static void installHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof DidExternalVarApp)) {
            Thread.setDefaultUncaughtExceptionHandler(new DidExternalVarApp());
        }
    }
    public DidExternalVarApp() {
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SettingEnvPersister.initPrefs(this);

        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.w("Init APP");
    }

    public void addMenuObject(OrderListItem item)
    {
        if(mDidOrderList!=null)
            mDidOrderList.add(item);
    }
    public void removeMenuObject(OrderListItem item)
    {
        if(mDidOrderList!=null)
            mDidOrderList.remove(item);
    }
    public int getWaitOrderCount()
    {
        return mWaitOrderCount;
    }
    public void setWaitOrderCount(int count)
    {
        mWaitOrderCount = count;
        Log.d(TAG, "DID TEST setWaitOrderCount()="+mWaitOrderCount);
    }


    public List<OrderListItem> getMenuObject()
    {
        return mDidOrderList;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String errlog = String.format("Exception: %s\n%s", e.toString(), getStackTrace(e));
        // Place a breakpoint here to catch application crashes
        FileUtils.writeDebug(errlog, "PayCastDid");

        Log.wtf(TAG, errlog);

        //android.os.Process.killProcess(android.os.Process.myPid());
        // Call the default handler
        defaultHandler.uncaughtException(t, e);

    }
    /**
     * Convert an exception into a printable stack trace.
     * @param e the exception to convert
     * @return the stack trace
     */
    private String getStackTrace(Throwable e) {
        final Writer sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);
        String stacktrace = sw.toString();
        pw.close();

        return stacktrace;
    }

}
