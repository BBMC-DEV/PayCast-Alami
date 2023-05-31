package kr.co.bbmc.paycastdid.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bbmc.paycastdid.model.DidAlarmData;
import kr.co.bbmc.paycastdid.model.DidAlarmMenuData;
import kr.co.bbmc.paycastdid.DidCmdAsyncTask;
import kr.co.bbmc.paycastdid.DidExternalVarApp;
import kr.co.bbmc.paycastdid.R;
import kr.co.bbmc.selforderutil.AuthKeyFile;
import kr.co.bbmc.selforderutil.CommandObject;
import kr.co.bbmc.selforderutil.DidOptionEnv;
import kr.co.bbmc.selforderutil.DownFileInfo;
import kr.co.bbmc.selforderutil.FileUtils;
import kr.co.bbmc.selforderutil.NetworkUtil;
import kr.co.bbmc.selforderutil.OptUtil;
import kr.co.bbmc.selforderutil.PlayerCommand;
import kr.co.bbmc.selforderutil.ProductInfo;
import kr.co.bbmc.selforderutil.PropUtil;
import kr.co.bbmc.selforderutil.ServerReqUrl;
import kr.co.bbmc.selforderutil.Utils;
import kr.co.bbmc.selforderutil.XmlOptionParser;

public class DidMainService extends Service {
    private static final String TAG = "DidMainService";
    private static final boolean LOG = true;

    private static int AUTH_TIME_INTERVAL = 100000;
    private static int ALARM_DELAY_TIME_INTERVAL = 2000;
    private static int MIN_COMMAND_CHECK_INTERVAL = 1000;
    private int MAX_RETRY_COUNT =2;

    private final IBinder mBinder = new LocalBinder();

    private DidExternalVarApp mDidExterVarApp;
    private static PropUtil mPropUtil;
    private XmlOptionParser mXmlOptUtil;
    private DidOptionEnv mDidStbOpt;
    private Timer mAuthTimer = null;  //auth timer
    private ArrayList<String> mAuthList = new ArrayList<String>();
    private boolean mAuthVaild = false;
    private Callbacks mActivity;
    private List<CommandObject> commandList = new ArrayList<CommandObject>();
    private List<CommandObject> newcommandList = new ArrayList<CommandObject>();
    private List<DownFileInfo> downloadList = new ArrayList<DownFileInfo>();
    private BroadcastReceiver mAgentCmdReceiver;
    private Timer mAlarmDelayTimer = null;  //alarm delay timer
    private static Timer mFcmTimer;
    private static Timer mCommandTimer;
    private static Timer mCmdCheckTimer;

    private static fcmCommandTask mFcmCommandTask;
    private static commandTimerTask mCommandTimerTask;
    private static onCheckCmdTimerTask mCheckCmTimerTask;
    /*  Async Task  */
    private static DidCmdAsyncTask mCommandAsynTask = null;
    private static CommandCheckAsynTask mCheckCmAsynTask = null;
    private static DidUpdateWaitingCountAsyncTask mWaitingCountAsynTask = null;

    private static int MAX_FCM_TIMER = 60*1000;   //1 min
//    private static int MAX_FCM_TIMER = 2*60*1000;   //2 min

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    //returns the instance of the service
    public class LocalBinder extends Binder {
        public DidMainService getServiceInstance(){
            return DidMainService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        File bbmcDefault = new File(FileUtils.BBMC_DEFAULT_DIR);
        if (!bbmcDefault.exists()) {
            bbmcDefault.mkdir();
        }
        bbmcDefault = new File(FileUtils.BBMC_PAYCAST_DIRECTORY);
        if (!bbmcDefault.exists())
            bbmcDefault.mkdir();

        bbmcDefault = new File(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY);
        if (!bbmcDefault.exists())
            bbmcDefault.mkdir();

        bbmcDefault = new File(FileUtils.BBMC_PAYCAST_BG_DIRECTORY);
        if (!bbmcDefault.exists())
            bbmcDefault.mkdir();

        mXmlOptUtil = new XmlOptionParser();

        File dir = FileUtils.makeDirectory(FileUtils.BBMC_DIRECTORY);

        mDidExterVarApp = (DidExternalVarApp) getApplication();
        mDidExterVarApp.installHandler();

        mDidStbOpt = new DidOptionEnv();
        /*  Agent option    */
        File f = FileUtils.makeDidAgentOptionFile(dir, FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), getApplication(), mDidStbOpt);
        OptUtil.ReadOptions(FileUtils.PayCastDid, true, FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), getApplicationContext());
        mDidExterVarApp.mDidStbOpt = mXmlOptUtil.parseDidAgentOptionXML(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), mDidStbOpt, getApplicationContext());


        mPropUtil = new PropUtil();
        mPropUtil.init(getApplicationContext());

        mAuthTimer = new Timer();
        mAuthTimer.schedule(new AuthTimerTask(), 2000);
        mAuthList = FileUtils.searchByFilefilter(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY, "deviceid", "txt");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(mDidExterVarApp.ACTION_SERVICE_COMMAND);
        mAgentCmdReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(LOG)
                    Log.e(TAG, "broadcast receiver() action="+intent.getAction()+" action="+mDidExterVarApp.ACTION_SERVICE_COMMAND);
                if (intent.getAction().equalsIgnoreCase(mDidExterVarApp.ACTION_SERVICE_COMMAND)) {
                    Bundle b = intent.getExtras();
                    if(LOG)
                        Log.e(TAG, "broadcast receiver() 1");
                    if (b != null) {
                        PlayerCommand c = new PlayerCommand();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        c.command = b.getString("command");
                        c.requestDateTime = b.getString("requestDateTime");
                        c.executeDateTime = b.getString("executeDateTime");
                        if(LOG)
                            Log.e(TAG, "broadcast receiver() 2 cmd="+c.command);
                        if (c.command.equals(getString(R.string.paycast_did_fcm_receive))) {
                            if(LOG)
                                Log.d(TAG, "agent cmd receiver "+getString(R.string.paycast_did_fcm_receive));
                            onCheckCommandForFcm();
                            //onSetFcmCommandTimer();
                        }
                    }
                }
            }
        };
        registerReceiver(mAgentCmdReceiver, intentFilter);
    }
    private int mAuthRetry = -1;
    private class AuthTimerTask extends TimerTask {
        @Override
        public void run() {
            mAuthTimer.cancel();
            if(!NetworkUtil.isConnected(getApplicationContext()))
            {
                if(LOG) {
                    Log.e(TAG, "AuthTimerTask() NETWROK RETURN");
                    Log.e(TAG, " NETWORK NOT CONNECT");
                }
                mAuthTimer = new Timer();
                mAuthTimer.schedule(new AuthTimerTask(), 2000);
                return;
            }
            if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
            {
                if(LOG)
                    Log.e(TAG, "AuthTimerTask() NETWROK RETURN");
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
                return;

            }
            else
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_stability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
            }
            if(LOG)
                Log.d(TAG, " NETWORK CONNECT");
            boolean deviceFile = false;
            if(mAuthList.size()> 1) {
                for(int i = 0; i < mAuthList.size(); i++) {
                    deviceFile = AuthKeyFile.readKeyFile(getApplicationContext(), mAuthList.get(i));
                    if(deviceFile)
                        break;
                }
            }
            else {
                deviceFile = AuthKeyFile.readKeyFile(getApplicationContext(), FileUtils.BBMC_PAYCAST_DATA_DIRECTORY+"Deviceid.txt");
            }
            if(deviceFile == false)
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
//                                command.command = getString(R.string.str_command_player_restart);
                command.command = getString(R.string.str_command_player_not_exist_deviceid_file);

                if(mActivity!=null)
                    mActivity.updateClient(command);
/*
                final Intent bIntent = sendPlayerCommand(command);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(bIntent);
                    }

                }, 3000);
*/
            }

            /*  FCM Token registration  */
            if((mDidExterVarApp.token!=null)&&(!mDidExterVarApp.token.isEmpty())) {
                AuthKeyFile.onSetFcmToken(mDidExterVarApp.token);
                sendRegistrationToServer(mDidExterVarApp.token);
            }

            final String authServer = AuthKeyFile.getAuthValidationServer();
            final String tokenSaveUrl = ServerReqUrl.getServerSaveTokenUrl(mDidExterVarApp.mDidStbOpt.serverHost, mDidExterVarApp.mDidStbOpt.serverPort, getApplicationContext());
            final String tokenParam = AuthKeyFile.getFcmTokenParam();
            Logger.w("tokenParam1 : " + tokenParam);
            if((tokenParam!=null)&&(!tokenParam.isEmpty()))
            {
                new Thread() {
                    public void run() {
                        if (!NetworkUtil.isConnected(getApplicationContext())) {
                            //                        sendShowMessage(R.string.Msg_InvalidStbStatusAlert);
                            if(LOG) {
                                Log.e(TAG, "Msg_InvalidStbStatusAlert token");
                                Log.e(TAG, "Msg_InvalidStbStatusAlert() NETWROK RETURN");
                            }

                            return;
                        }
                        if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
                        {
                            PlayerCommand command = new PlayerCommand();
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            command.executeDateTime = simpleDateFormat.format(currentTime);
                            command.requestDateTime = simpleDateFormat.format(currentTime);
                            command.command = getString(R.string.paycast_did_network_instability);
                            if(mActivity!=null)
                                mActivity.updateClient(command);
                            if(LOG)
                                Log.e(TAG, "Msg_InvalidStbStatusAlert() NETWROK RETURN");
                            return;

                        }
                        else
                        {
                            PlayerCommand command = new PlayerCommand();
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            command.executeDateTime = simpleDateFormat.format(currentTime);
                            command.requestDateTime = simpleDateFormat.format(currentTime);
                            command.command = getString(R.string.paycast_did_network_stability);
                            if(mActivity!=null)
                                mActivity.updateClient(command);
                        }

                        String response = NetworkUtil.HttpResponseString(tokenSaveUrl, tokenParam, getApplicationContext(), false);
                        if(LOG)
                            Log.d(TAG, "Token response = " + response);
                    }
                }.start();
            }

            String param = AuthKeyFile.getAuthKeyParam(getApplicationContext(), getString(R.string.majorVersion));
            String targetStr = param.replaceAll("authkey=", "authkey=P3w/fD98P3wyfD8=");
            final String finalParam = targetStr;
            Logger.w("final param2: " + finalParam);

            new Thread() {
                public void run() {
                    if(!NetworkUtil.isConnected(getApplicationContext()))
                    {
//                        sendShowMessage(R.string.Msg_InvalidStbStatusAlert);
                        if(LOG) {
                            Log.e(TAG, "Msg_InvalidStbStatusAlert");
                            Log.e(TAG, "Msg_InvalidStbStatusAlert() NETWROK RETURN");
                        }
                        return;
                    }
                    if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
                    {
                        PlayerCommand command = new PlayerCommand();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        command.executeDateTime = simpleDateFormat.format(currentTime);
                        command.requestDateTime = simpleDateFormat.format(currentTime);
                        command.command = getString(R.string.paycast_did_network_instability);
                        if(mActivity!=null)
                            mActivity.updateClient(command);
                        if(LOG)
                            Log.e(TAG, "Msg_InvalidStbStatusAlert() NETWROK RETURN");
                        return;

                    }
                    else
                    {
                        PlayerCommand command = new PlayerCommand();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        command.executeDateTime = simpleDateFormat.format(currentTime);
                        command.requestDateTime = simpleDateFormat.format(currentTime);
                        command.command = getString(R.string.paycast_did_network_stability);
                        if(mActivity!=null)
                            mActivity.updateClient(command);
                    }

                    String response = NetworkUtil.HttpResponseString(authServer, finalParam, getApplicationContext(), false);
                    Log.d(TAG, "AUTH response = " + response);

                    if(LOG)
                        Log.d(TAG, "AUTH response = " + response);

                    // 사전 인증 시의 결과는 다음과 같이 성공 flag | AuthKey | UserName 으로 전달됨
                    // S:?|AAAABBBBCCCC|Username
                    if((response!=null)&&(!response.isEmpty())) {
                        PlayerCommand command = new PlayerCommand();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        command.executeDateTime = simpleDateFormat.format(currentTime);
                        command.requestDateTime = simpleDateFormat.format(currentTime);
                        command.command = getString(R.string.paycast_did_network_stability);
                        if(mActivity!=null)
                            mActivity.updateClient(command);

                        mAuthRetry = -1;
                        if (response.startsWith("S:")) {
                            ArrayList<String> tokens = new ArrayList<>();
                            //= response.split("|" );

                            while (response.length() > 0) {
                                int index = response.indexOf("|");
                                String s = new String();
                                if (index <= 0) {
                                    if (response.length() > 0) {
                                        index = 0;
                                        s = response;
                                        tokens.add(s);
                                    }
                                    break;
                                } else
                                    s = response.substring(0, index);
                                String temp = response.substring(index + 1, response.length());
                                response = temp;
                                tokens.add(s);
                                if (temp.length() <= 0)
                                    break;
                            }
                            ProductInfo productKey = AuthKeyFile.getProductInfo();
                            if (AuthKeyFile.writeKeyFile(productKey.getAuthDeviceId(), tokens.get(2), tokens.get(1),
                                    productKey.getAuthMacAddress(), Integer.parseInt(getString(R.string.majorVersion)), tokens.get(0).substring(2))) {
                                mAuthVaild = true;
                                if(LOG)
                                    Log.d(TAG, "Msg_ProductRegCompleteAlert");

                            } else {
                                mAuthVaild = false;
                                if(LOG)
                                    Log.d(TAG, "Msg_WrongAuthUrl ");
                            }
                        } else if (response.equals("Y")) {
                            AuthKeyFile.writeKeyFile("?", "?", "?", "?", 2, "?");
                            mAuthVaild = false;
                        } else if (response.equals("N")) {
                            if(LOG)
                                Log.d(TAG, "Auth ok");
                            mAuthVaild = true;
                        } else {
                            Log.d(TAG, "FALSE 진입3");
                            mAuthVaild = false;
                            if(LOG)
                                Log.d(TAG, "Msg_WrongAuthUrl ");
                        }
                    }
                    else if((response==null)||(response.isEmpty()))
                    {
                        if(mAuthRetry>=MAX_RETRY_COUNT)
                        {
                            PlayerCommand command = new PlayerCommand();
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            command.executeDateTime = simpleDateFormat.format(currentTime);
                            command.requestDateTime = simpleDateFormat.format(currentTime);
                            command.command = getString(R.string.paycast_did_network_instability);
                            if(mActivity!=null)
                                mActivity.updateClient(command);
                            mAuthRetry=-1;

                        }
                        mAuthRetry++;
                        mAuthTimer.cancel();
                        mAuthTimer = new Timer();
                        mAuthTimer.schedule(new AuthTimerTask(), 2000);
                        return;
                    }
                    if (mAuthVaild == false) {
//                        if((mAuthRetry>mMaxRetryNum))
                        {
                            Log.d(TAG, "FALSE 진입");
                            PlayerCommand command = new PlayerCommand();
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            command.executeDateTime = simpleDateFormat.format(currentTime);
                            command.requestDateTime = simpleDateFormat.format(currentTime);
//                                command.command = getString(R.string.str_command_player_restart);
                            command.command = getString(R.string.str_command_player_auth_fail);
                            if(mActivity!=null)
                                mActivity.updateClient(command);
                        }
                        if (mAuthTimer != null)
                            mAuthTimer.cancel();
                        mAuthTimer = new Timer();
                        mAuthTimer.schedule(new AuthTimerTask(), AUTH_TIME_INTERVAL);
                    } else {
                        if (mAuthTimer != null)
                            mAuthTimer.cancel();


                        mDidExterVarApp.mDidStbOpt.deviceId = AuthKeyFile.getProductInfo().getAuthDeviceId();

                        PlayerCommand command = new PlayerCommand();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        command.executeDateTime = simpleDateFormat.format(currentTime);
                        command.requestDateTime = simpleDateFormat.format(currentTime);
                        command.command = getString(R.string.str_command_player_deviceid);
                        command.addInfo = mDidExterVarApp.mDidStbOpt.deviceId;

                        try {
                            FileUtils.updateFile(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY +FileUtils.getFilename(FileUtils.PayCastDid), getApplication(), mDidExterVarApp.mDidStbOpt);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        if(mActivity!=null)
                            mActivity.updateClient(command);
/*
                        final Intent bIntent = sendPlayerCommand(command);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendBroadcast(bIntent);
                            }
                        }, 100);
*/

                        String stbRes = NetworkUtil.getNetworkInfoFromServer(getApplicationContext(), mDidExterVarApp.mDidStbOpt.serverHost, mDidExterVarApp.mDidStbOpt.serverPort, mDidExterVarApp.mDidStbOpt.deviceId, mDidExterVarApp.mDidStbOpt.serverUkid);
                        if ((stbRes != null) && (!stbRes.isEmpty())) {
                            ParseXML(stbRes);
                            onSetFcmCommandTimer();
                            if ((mWaitingCountAsynTask == null)||(mWaitingCountAsynTask.isCancelled())||mWaitingCountAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                                mWaitingCountAsynTask = new DidUpdateWaitingCountAsyncTask();
                                mWaitingCountAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                    }
                }
            }.start();

        }
    }
    private int tokenRetry = -1;
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        ProductInfo pInfo = AuthKeyFile.getProductInfo();
        if(pInfo!=null)
        {
            String serverUrl = AuthKeyFile.getAuthRegFCMTokenServer();
            String queryString = AuthKeyFile.getAuthTokenParam();
            String ssl = PropUtil.configValue(getApplicationContext().getString(kr.co.bbmc.selforderutil.R.string.serverSSLEnabled), getApplicationContext());

            String response = NetworkUtil.sendFCMTokenToAuthServer(serverUrl, queryString);
            if((response!=null)&&(!response.isEmpty()))
            {
                tokenRetry = -1;
                if(LOG)
                    Log.d(TAG, "sendRegistrationToServer() response="+ response);
            }
            else if((response==null)||(response.isEmpty()))
            {
                tokenRetry++;
                if(LOG)
                    Log.d(TAG, "sendRegistrationToServer() tokenRetry="+ tokenRetry);
                if(tokenRetry>=MAX_RETRY_COUNT)
                {
                    PlayerCommand command = new PlayerCommand();
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    command.executeDateTime = simpleDateFormat.format(currentTime);
                    command.requestDateTime = simpleDateFormat.format(currentTime);
                    command.command = getString(R.string.paycast_did_network_instability);

                    if(mActivity!=null)
                        mActivity.updateClient(command);
                    tokenRetry = -1;
                }
            }
        }
    }
    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity){
        this.mActivity = (Callbacks)activity;
    }

    public interface Callbacks{
        public void updateClient(PlayerCommand cmd);
    }
    public void ParseXML(String fls) {
        CommandObject cmd = null;
        boolean addCmd = false;

        if((fls==null)||(fls.isEmpty()))
            return;

        try {
            Log.d(TAG, "ENTER PARSER");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new ByteArrayInputStream(fls.getBytes());
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean isItemTag = false;
            DownFileInfo downloadInfo = null;

            DidOptionEnv tempDidOpt = new DidOptionEnv();

            copyStbOption(mDidExterVarApp.mDidStbOpt, tempDidOpt);
            boolean parErr = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        //Log.d(TAG, "START_TAG.name=" + name);
                        if (name.equals(getResources().getString(R.string.server))) {

                            String ref = parser.getAttributeValue(null, "ref");
                            //Log.d(TAG, "ref:" + ref);
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                if (attrName.equals(getResources().getString(R.string.ftpActiveMode))) {
                                    tempDidOpt.ftpActiveMode = Boolean.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "FtpActiveMode:" + mStbOpt.ftpActiveMode);
                                } else if (attrName.equals(getResources().getString(R.string.ftpHost))) {
                                    String ftphost = parser.getAttributeValue(i);
                                    if(ftphost!=null&&!ftphost.isEmpty())
                                        tempDidOpt.ftpHost = ftphost;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.FtpHost:" + mStbOpt.ftpHost);
                                } else if (attrName.equals(getResources().getString(R.string.ftpPassword))) {
                                    String ftppw = parser.getAttributeValue(i);
                                    if((ftppw!=null)&&(!ftppw.isEmpty()))
                                        tempDidOpt.ftpPassword = ftppw;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.FtpPassword:" + mStbOpt.ftpPassword);
                                } else if (attrName.equals(getResources().getString(R.string.ftpPort))) {
                                    int ftpport = Integer.valueOf(parser.getAttributeValue(i));
                                    if(ftpport > 0)
                                        tempDidOpt.ftpPort = ftpport;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.FtpPort:" + mStbOpt.ftpPort);
                                } else if (attrName.equals(getResources().getString(R.string.ftpUser))) {
                                    String ftpUser = parser.getAttributeValue(i);
                                    if((ftpUser!=null)&&(!ftpUser.isEmpty()))
                                        tempDidOpt.ftpUser = ftpUser;
                                    else
                                        parErr = true;
                                } else if (attrName.equals(getResources().getString(R.string.serverHost))) {
                                    String serverHost = parser.getAttributeValue(i);
                                    if((serverHost!=null)&&(!serverHost.isEmpty()))
                                        tempDidOpt.serverHost = serverHost;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.ServerHost:" + mStbOpt.serverHost);
                                } else if (attrName.equals(getResources().getString(R.string.serverPort))) {
                                    int serverPort = Integer.valueOf(parser.getAttributeValue(i));
                                    if(serverPort > 0)
                                        tempDidOpt.serverPort = serverPort;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.ServerPort:" + mStbOpt.serverPort);
                                } else if (attrName.equals(getResources().getString(R.string.serverUkid))) {
                                    String serverUkid = parser.getAttributeValue(i);
                                    if((serverUkid!=null)&&(!serverUkid.isEmpty()))
                                        tempDidOpt.serverUkid = serverUkid;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.ServerUkid:" + mStbOpt.serverUkid);
                                } else if (attrName.equals(getResources().getString(R.string.stbId))) {
                                    int stbId = Integer.valueOf(parser.getAttributeValue(i));

                                    if(stbId > 0)
                                        tempDidOpt.stbId = stbId;
                                    if (tempDidOpt.stbId > 0)
                                    {
                                        if(tempDidOpt.stbStatus == 0)
                                            tempDidOpt.stbStatus = 5;
                                    }
                                    else
                                        parErr = true;

                                    //Log.d(TAG, "Server.StbId:" + mStbOpt.stbId);
                                } else if (attrName.equals(getResources().getString(R.string.stbName))) {
                                    String stbName = parser.getAttributeValue(i);
                                    if((stbName!=null)&&(!stbName.isEmpty()))
                                        tempDidOpt.stbName = stbName;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.StbName:" + mStbOpt.stbName);
                                } else if (attrName.equals(getResources().getString(R.string.stbServiceType))) {
                                    String stbServiceType = parser.getAttributeValue(i);
                                    if((stbServiceType!=null)&&!stbServiceType.isEmpty())
                                        tempDidOpt.stbServiceType = stbServiceType;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.StbServiceType:" + mStbOpt.stbServiceType);
                                } else if (attrName.equals(getResources().getString(R.string.stbUdpPort))) {
                                    int stbUdpPort = Integer.valueOf(parser.getAttributeValue(i));
                                    if(stbUdpPort > 0)
                                        tempDidOpt.stbUdpPort = stbUdpPort;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.store_name))) {
                                    String storeName = parser.getAttributeValue(i);
                                    if((storeName!=null)&&(!storeName.isEmpty()))
                                        tempDidOpt.storeName = storeName;
                                    //else
                                    //    parErr = true;

                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.store_addr))) {
                                    String storeAddr = parser.getAttributeValue(i);
                                    if((storeAddr!=null)&&(!storeAddr.isEmpty()))
                                        tempDidOpt.storeAddr = storeAddr;
                                    //else
                                    //    parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.business_num))) {
                                    String storeBusinessNum = parser.getAttributeValue(i);
                                    if((storeBusinessNum!=null)&&(!storeBusinessNum.isEmpty()))
                                        tempDidOpt.storeBusinessNum = storeBusinessNum;
                                    //else
                                    //    parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.store_tel))) {
                                    String storeTel = parser.getAttributeValue(i);
                                    if((storeTel!=null)&&(!storeTel.isEmpty()))
                                        tempDidOpt.storeTel = storeTel;
                                    //else
                                    //    parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.merchant_num))) {
                                    String storeMerchantNum = parser.getAttributeValue(i);
                                    if((storeMerchantNum!=null)&&(!storeMerchantNum.isEmpty()))
                                        tempDidOpt.storeMerchantNum = storeMerchantNum;
                                    //else
                                    //    parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.store_catid))) {
                                    String storeCatId = parser.getAttributeValue(i);
                                    if((storeCatId!=null)&&(!storeCatId.isEmpty()))
                                        tempDidOpt.storeCatId = storeCatId;
                                    //else
                                    //parErr = true;
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(getResources().getString(R.string.represent))) {
                                    String storeRepresent = parser.getAttributeValue(i);
                                    if((storeRepresent!=null)&&(!storeRepresent.isEmpty()))
                                        tempDidOpt.storeRepresent = storeRepresent;
                                    //else
                                    //    parErr = true;
                                    //Log.d(TAG, "Server.storeRepresent:" + mAgentExterVarApp.mStbOpt.storeRepresent);
                                } else if (attrName.equals(getResources().getString(R.string.store_id))) {
                                    String storeId = parser.getAttributeValue(i);
                                    if((storeId!=null)&&(!storeId.isEmpty()))
                                        tempDidOpt.storeId = storeId;
                                    else
                                        parErr = true;
                                    //Log.d(TAG, "Server.storeId:" + mAgentExterVarApp.mStbOpt.storeId);
                                } else if (attrName.equals(getResources().getString(R.string.store_operating_time))) {
                                    String operatingTime = parser.getAttributeValue(i);
                                    if((operatingTime!=null)&&(!operatingTime.isEmpty()))
                                        tempDidOpt.operatingTime = operatingTime;
                                    //else
                                    //    parErr = true;
                                } else if (attrName.equals(getResources().getString(R.string.store_introduction_msg))) {
                                    String introMsg = parser.getAttributeValue(i);
                                    if((introMsg!=null)&&(!introMsg.isEmpty()))
                                        tempDidOpt.introMsg = introMsg;
                                    //else
                                    //    parErr = true;
                                } else if (attrName.equals(getResources().getString(R.string.server_device_id))) {
                                    String deviceId = parser.getAttributeValue(i);
                                    Log.d(TAG, "DEVIDEID : " + deviceId);
                                    if((deviceId!=null)&&(!deviceId.isEmpty()))
                                        tempDidOpt.deviceId = deviceId;
                                    else
                                        parErr = true;
                                }
                            }
                            if(parErr == false) {
                                mDidExterVarApp.mDidStbOpt = tempDidOpt;
                                FileUtils.updateFile(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), getApplication(), mDidExterVarApp.mDidStbOpt);
                            }
                        }
                        if (name.equals("command")) {

//                            String ref = parser.getAttributeValue(null, "ref");
                            if(LOG) {
                                Log.d(TAG, "count:" + parser.getAttributeCount());
                                Log.d(TAG, "Text:" + parser.getText());
                            }
                            if (parser.getAttributeCount() == 0) {
                                cmd = null;
                            } else
                                cmd = new CommandObject();

                            for (int i = 0; i < parser.getAttributeCount(); i++)
                            {
                                String attrName = parser.getAttributeName(i);
                                if (attrName.equals("rcCommandId")) {
                                    cmd.rcCommandid = parser.getAttributeValue(i);
                                } else if (attrName.equals("command")) {
                                    cmd.command = parser.getAttributeValue(i);
                                } else if (attrName.equals("execTime")) {
                                    cmd.execTime = parser.getAttributeValue(i);
                                } else if (attrName.equals("CDATA")) {
                                    cmd.prameter = parser.getAttributeValue(i);
                                } else if (attrName.equalsIgnoreCase("wait")) {
                                    cmd.prameter = parser.getAttributeValue(i);
                                }

                            }

                            if((commandList.size()==0)&&(cmd.rcCommandid != null) && !cmd.command.isEmpty())
                                addCmd = true;
                            for (CommandObject c : commandList) {
                                if ((cmd.rcCommandid != null) && !cmd.command.isEmpty()) {
                                    if (c.rcCommandid.equals(cmd.rcCommandid)) {
                                        addCmd = false;
                                        break;
                                    }
                                    else
                                        addCmd = true;
                                } else {
                                    addCmd = false;
                                    break;
                                }
                            }
                            if (cmd == null) {
                                addCmd = false;
                            }
                        }
                        if (name.equals("![CDATA")) {
                            Log.d(TAG, "![CDATA!!  = " + name);
                        }
                        if (name.equals(getResources().getString(R.string.item))) {
                            downloadInfo = new DownFileInfo();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                //Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals(getResources().getString(R.string.foldername))) {
                                    downloadInfo.folderName = parser.getAttributeValue(i);
//                                    downloadInfo.folderName = downloadInfo.folderName.replace("Schedule", "Menu");
                                    Log.d(TAG, "folderName:" + downloadInfo.folderName);
                                } else if (attrName.equals(getResources().getString(R.string.filename))) {
                                    downloadInfo.fileName = parser.getAttributeValue(i);
                                    Log.d(TAG, "fileName:" + downloadInfo.fileName);
                                } else if (attrName.equals(getResources().getString(R.string.filelength))) {
                                    downloadInfo.fileLength = Long.parseLong(parser.getAttributeValue(i));
                                    Log.d(TAG, "filelength:" + downloadInfo.fileLength);
                                } else if (attrName.equals(getResources().getString(R.string.stbfileid))) {
                                    downloadInfo.stbfileid = Integer.valueOf(parser.getAttributeValue(i));
                                    if (downloadInfo.stbfileid == -1) {
                                        downloadInfo.scheduleContent = false;
                                    } else {
                                        downloadInfo.scheduleContent = true;
                                        downloadInfo.downFileId = downloadInfo.stbfileid;
                                    }
                                    Log.d(TAG, "stbfileid:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(getResources().getString(R.string.kfileid))) {
                                    downloadInfo.kfileid = Integer.valueOf(parser.getAttributeValue(i));
                                    if (downloadInfo.stbfileid == -1)
                                        downloadInfo.downFileId = downloadInfo.kfileid;
                                    Log.d(TAG, "kfileid:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(getResources().getString(R.string.kroot))) {
                                    downloadInfo.kroot = parser.getAttributeValue(i);
                                    Log.d(TAG, "kroot:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(getResources().getString(R.string.playatonce))) {
                                    downloadInfo.playatonce = parser.getAttributeValue(i);
                                    Log.d(TAG, "playatonce:" + parser.getAttributeValue(i));
                                }
                            }
                            boolean addDown = true;
                            for (DownFileInfo d : downloadList) {
                                if (d.fileName.equals(downloadInfo.fileName) && d.folderName.equals(downloadInfo.folderName)) {
                                    addDown = false;
                                    break;
                                }
                            }
                            if (addDown) {
                                if ((downloadInfo.fileName != null) && (downloadInfo.fileLength > 0))
                                    downloadList.add(downloadInfo);
                            }
                        }
                        else if (name.equalsIgnoreCase("wait")) {
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attr = parser.getAttributeValue(null, "waiting");
                                if (attr != null) {
                                    cmd.prameter = attr;
                                }
                            }
                        }
                    }
                    break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (LOG)
                            Log.d(TAG, "TEXT = " + text);
                        if ((text != null) && !text.isEmpty()) {
                            if (cmd != null)
                            {
                                cmd.prameter = text;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name = parser.getName();
                        if (LOG)
                            Log.d(TAG, "END_TAG.name=" + name + "");
                        if (name.equals("command")) {
                            if (cmd != null) {
                                if (addCmd) {
                                    if (mDidExterVarApp.newcommandList == null)
                                        mDidExterVarApp.newcommandList = new ArrayList<>();
                                    boolean newAddcmd = true;
                                    if(mDidExterVarApp.newcommandList.size()>0)
                                    {
                                        for(int c = 0; c<mDidExterVarApp.newcommandList.size(); c++)
                                        {
                                            CommandObject cmdObj = mDidExterVarApp.newcommandList.get(c);
                                            if(cmdObj.command.equalsIgnoreCase(cmd.command))
                                            {
                                                if(cmdObj.rcCommandid.equalsIgnoreCase(cmd.rcCommandid)) {
                                                    newAddcmd = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if(newAddcmd) {
                                        mDidExterVarApp.newcommandList.add(cmd);
                                        Utils.LOG("DID TEST"+"--------------------");
                                        Utils.LOG("DID TEST"+getString(R.string.Log_CmdAdditionalCommand) + " #: " + cmd.rcCommandid);
                                        Utils.LOG("DID TEST"+getString(R.string.Log_CmdCommandName) + ": " + cmd.command);
                                        Utils.LOG("DID TEST"+getString(R.string.Log_CmdExecTime) + ": " + cmd.execTime);
                                        Utils.LOG("DID TEST"+getString(R.string.Log_CmdParameter) + ": " + cmd.prameter);
                                        Utils.LOG("DID TEST"+getString(R.string.Log_CmdTotalCommandCount) + ": " + mDidExterVarApp.newcommandList.size());
                                        Utils.LOG("DID TEST"+"--------------------");
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        Log.d(TAG, "EVENT TYPE = " + eventType);
                        break;
                }
//                Log.d(TAG, "parser.NEXT="+parser.nextToken());
                eventType = parser.next();

            }


        } catch (Exception e) {
            Log.d(TAG, "2 Error in ParseXML()", e);
        }

    }
    public void onSetFcmStart()
    {
        Log.d(TAG, "onSetFcmStart()");
    }
    private void copyStbOption(DidOptionEnv source, DidOptionEnv dest)
    {
        dest.stbUdpPort=source.stbUdpPort;
        dest.serverPort= source.serverPort;
        dest.stbServiceType=source.stbServiceType;
        dest.serverHost=source.serverHost;
        dest.serverUkid=source.serverUkid;
        dest.storeId=source.storeId;    //매장 번호
        dest.deviceId=source.deviceId;     //deviceId;

    }

    public void onCheckCommandForFcm() {
        boolean needCheckCmdFlag = true;
        long now = System.currentTimeMillis();
        Date nowDate = new Date(now);

        if ((mCheckCmAsynTask == null) || (mCheckCmAsynTask.isCancelled()) || mCheckCmAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
/*
            if((mCommandAsynTask==null)||(mCommandAsynTask.isCancelled())||mCommandAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                needCheckCmdFlag = true;
            }
            else
                needCheckCmdFlag = false;
*/
/*
            if (mCheckCmAsynTask != null)
            {
                Date oldDate = mCheckCmAsynTask.getExecutionTime();
                long diff = nowDate.getTime() - oldDate.getTime();

                if (diff < MIN_COMMAND_CHECK_INTERVAL) {
                    needCheckCmdFlag = false;
                    Log.e(TAG, "DID TEST onCheckCommandForFcm() DIFF="+diff);
                }
            }
*/
            if (needCheckCmdFlag) {
                mCheckCmAsynTask = new CommandCheckAsynTask();
                mCheckCmAsynTask.setExecutionTime(nowDate);
                mCheckCmAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            needCheckCmdFlag = true;
        }
    }

    public void onSetFcmCommandTimer()
    {

//        if(mFcmTimer==null)
        {
            if(LOG)
                Log.e("DID TEST", "DID TEST onSetFcmCommandTimer() 1 ");
            if(LOG)
                Log.e(TAG, " fcmCheckTask() 1 onSetFcmCommandTimer");
/*

            mFcmTimer = new Timer("mFcmTimer");
            if(mCommandTimer==null) {
                mCommandTimer = new Timer("mCommandTimer");
                mCommandTimerTask = new commandTimerTask();
                mCommandTimer.scheduleAtFixedRate(mCommandTimerTask, 0, 5000);  //3000
            }
*/
            if(mCmdCheckTimer==null) {
                mCmdCheckTimer = new Timer("mCmdCheckTimer");
                mCheckCmTimerTask = new onCheckCmdTimerTask();
                mCmdCheckTimer.scheduleAtFixedRate(mCheckCmTimerTask, 0, 5000);  //6000
            }
/*
            if(mFcmCommandTask==null)
                mFcmCommandTask = new fcmCommandTask();
            mFcmTimer.schedule(mFcmCommandTask, MAX_FCM_TIMER);
*/

        }
/*
        else {

            if(mFcmTimer!=null) {
                mFcmTimer.cancel();
                mFcmCommandTask.cancel();
            }
            mFcmTimer = new Timer("mFcmTimer");
            mFcmCommandTask = new fcmCommandTask();
            mFcmTimer.schedule(mFcmCommandTask, MAX_FCM_TIMER);

            if(LOG)
                Log.e(TAG, " fcmCheckTask() 2 onSetFcmCommandTimer");
            Log.e("DID TEST", "DID TEST onSetFcmCommandTimer() 2 ");

        }
*/

    }
    public void _onSetFcmCommandTimer()
    {

        if(mFcmTimer==null)
        {
            if(LOG)
                Log.e("DID TEST", "DID TEST onSetFcmCommandTimer() 1 ");
            if(LOG)
                Log.e(TAG, " fcmCheckTask() 1 onSetFcmCommandTimer");

            mFcmTimer = new Timer("mFcmTimer");
/*
            if(mCommandTimer==null) {
                mCommandTimer = new Timer("mCommandTimer");
                mCommandTimerTask = new commandTimerTask();
                mCommandTimer.scheduleAtFixedRate(mCommandTimerTask, 0, 5000);  //3000
            }
*/

            if(mCmdCheckTimer==null) {
                mCmdCheckTimer = new Timer("mCmdCheckTimer");
                mCheckCmTimerTask = new onCheckCmdTimerTask();
                mCmdCheckTimer.scheduleAtFixedRate(mCheckCmTimerTask, 0, 5000);  //6000
            }

            if(mFcmCommandTask==null)
                mFcmCommandTask = new fcmCommandTask();
            mFcmTimer.schedule(mFcmCommandTask, MAX_FCM_TIMER);

        }

        else {
            if(mFcmTimer!=null) {
                mFcmTimer.cancel();
                mFcmCommandTask.cancel();
            }
            mFcmTimer = new Timer("mFcmTimer");
            mFcmCommandTask = new fcmCommandTask();
            mFcmTimer.schedule(mFcmCommandTask, MAX_FCM_TIMER);

            if(LOG) {
                Log.e(TAG, " fcmCheckTask() 2 onSetFcmCommandTimer");
                Log.e("DID TEST", "DID TEST onSetFcmCommandTimer() 2 ");
            }

        }

    }
    private class fcmCommandTask extends TimerTask {
        private boolean cancelFlag = false;

        @Override
        public boolean cancel() {
            cancelFlag = true;
            if(LOG)
                Log.e(TAG, "DID TEST fcmCheckTask() fcm timer cancel");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: fcmCheckTask() fcm timer cancel\n");
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            return super.cancel();
        }

        @Override
        public void run() {
            if(cancelFlag)
                return;
            if(mCmdCheckTimer!=null)
                mCmdCheckTimer.cancel();
            if(mCheckCmTimerTask!=null)
                mCheckCmTimerTask.cancel();
            mCmdCheckTimer = null;
            if(mCommandTimer!=null)
                mCommandTimer.cancel();
            if(mCommandTimerTask!=null)
                mCommandTimerTask.cancel();
            mCommandTimer = null;
            mFcmTimer = null;
            mFcmCommandTask = null;
            if(LOG)
                Log.e(TAG, "DID TEST fcmCheckTask() fcm timer clear");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: fcmCheckTask() fcm timer clear\n");
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
        }
    }
    private class commandTimerTask extends TimerTask {
        private boolean cancelFlag = false;

        @Override
        public boolean cancel() {
            cancelFlag = true;
            return super.cancel();
        }

        @Override
        public void run() {
            if(cancelFlag)
                return;
            if(LOG)
                Log.e(TAG, "commandTimerTask() 1");
            if ((mCommandAsynTask == null)||(mCommandAsynTask.isCancelled())||mCommandAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                mCommandAsynTask = new DidCmdAsyncTask();
                mCommandAsynTask.setApplication( mDidExterVarApp.mDidStbOpt, getApplication(), new DidCmdAsyncTask.onExecuteCmdListener() {
                    @Override
                    public String exeCommand(CommandObject ci) {
                        String result ="N";
                        result = onDidExeCommand(ci);
                        if(result.equalsIgnoreCase("Y"))
                        {
                            switch (ci.command) {
                                case "StoreStay.bbmc":              // 주문 알림 목록
                                case "StoreComplete.bbmc":                 // 주문 최종 알림 완료(DID 삭제)
                                    if (mActivity != null) {
                                        PlayerCommand command = new PlayerCommand();
                                        Date currentTime = Calendar.getInstance().getTime();
                                        SimpleDateFormat simpleDateFormat =
                                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        command.executeDateTime = simpleDateFormat.format(currentTime);
                                        command.requestDateTime = simpleDateFormat.format(currentTime);
                                        command.command = getString(R.string.paycast_orderlist_update);

                                        mActivity.updateClient(command);
                                    }
                                    break;
                                case "StoreWaitPeople.bbmc":   //대기자 수 update
                                    break;
                                default:
                                    break;
                            }
                        }
                        return result;
                    }

                    @Override
                    public ArrayList<CommandObject> getNewCommandList() {
                        if(mDidExterVarApp.newcommandList==null)
                            mDidExterVarApp.newcommandList = new ArrayList<CommandObject>();
                        return mDidExterVarApp.newcommandList;
                    }
                });
                mCommandAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    }
    private class onCheckCmdTimerTask extends TimerTask {
        private boolean cancelFlag = false;
        private boolean needCheckCmdFlag = true;

        @Override
        public void run() {
            if(LOG)
                Log.d(TAG, "DID TEST onCheckCmdTimerTask() cancelFlag="+cancelFlag);
            if(cancelFlag)
                return;
            long now = System.currentTimeMillis();
            Date nowDate = new Date(now);
            //Log.d(TAG, "DID TEST onCheckCmdTimerTask() 1");
            if ((mCheckCmAsynTask == null)||(mCheckCmAsynTask.isCancelled())||mCheckCmAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                if(mCheckCmAsynTask!=null) {
                    Date oldDate = mCheckCmAsynTask.getExecutionTime();
                    long diff = nowDate.getTime()-oldDate.getTime();

                    if(diff < MIN_COMMAND_CHECK_INTERVAL) {
                        if(LOG)
                            Log.e(TAG, "DID TEST onCheckCmdTimerTask() diff="+diff);
                        needCheckCmdFlag = false;
                    }
                }
/*
                if((mCommandAsynTask==null)||(mCommandAsynTask.isCancelled())||mCommandAsynTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    needCheckCmdFlag = true;
                }
                else
                    needCheckCmdFlag = false;
*/

                //Log.d(TAG, "DID TEST onCheckCmdTimerTask() 2 needCheckCmdFlag="+needCheckCmdFlag);
                if(needCheckCmdFlag) {
                    mCheckCmAsynTask = new CommandCheckAsynTask();
                    mCheckCmAsynTask.setExecutionTime(nowDate);
                    mCheckCmAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //Log.d(TAG, "DID TEST onCheckCmdTimerTask() 3");
                }
                needCheckCmdFlag = true;
            }
        }

        @Override
        public boolean cancel() {
            cancelFlag = true;
            return super.cancel();
        }
    }
    private int cmdChkRetry = -1;
    class CommandCheckAsynTask extends AsyncTask {
        public Date executionTime;

        public void setExecutionTime(Date date)
        {
            executionTime = date;
        }

        public Date getExecutionTime()
        {
            return executionTime;
        }

        @SuppressLint("WrongThread")
        @Override
        protected Object doInBackground(Object[] objects) {
            String reqUrl = null;
            String infoStr = null;

            if (!NetworkUtil.isConnected(getApplicationContext())) {
                if(LOG)
                    Log.e(TAG, "CommandCheckAsynTask() NETWROK RETURN");
                Utils.LOG(getString(R.string.Msg_InvalidStbStatusAlert));
                return null;
            }
            if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
            {
                if(LOG)
                    Log.e(TAG, "CommandCheckAsynTask() NETWROK RETURN");
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
                return null;

            }
            else
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_stability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
            }

            if(LOG)
                Log.d("DID TEST", "DID TEST  CommandCheckAsynTask() server command check start");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: CommandCheckAsynTask() server command check start\n");
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            reqUrl = ServerReqUrl.getServerDidInfoUrl(mDidExterVarApp.mDidStbOpt.serverHost, mDidExterVarApp.mDidStbOpt.serverPort, getApplicationContext());
//            infoStr = String.valueOf("?storeId=" + mAgentExterVarApp.getMenuObject().storeId);
            infoStr = String.valueOf("?storeId=" + mDidExterVarApp.mDidStbOpt.storeId+"&deviceId="+mDidExterVarApp.mDidStbOpt.deviceId);
            URI url = null;
            String tempString = reqUrl + infoStr;
            if(LOG)
                Log.d(TAG, "1 tempString=" + tempString);
            String res = NetworkUtil.onGetStoreChgSync(tempString);
            if(LOG)
                Log.d(TAG, "DID TEST CommandCheckAsynTask() 1 RES=" + res);
            if ((res != null)&&(!res.isEmpty())) {
                ParseXML(res);
            }
            if(LOG)
                Log.d("DID TEST", "DID TEST CommandCheckAsynTask() server command check end");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: CommandCheckAsynTask() server command check end %s\n", res);
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            if((res!=null)&&(!res.isEmpty())) {
                cmdChkRetry=-1;
                if (((mCommandAsynTask == null) || (mCommandAsynTask.getStatus() == Status.FINISHED)) || mCommandAsynTask.isCancelled()) {
                    mCommandAsynTask = new DidCmdAsyncTask();
                    mCommandAsynTask.setApplication(mDidExterVarApp.mDidStbOpt, getApplication(), new DidCmdAsyncTask.onExecuteCmdListener() {
                        @Override
                        public String exeCommand(CommandObject ci) {
                            String result = "N";
                            if(LOG)
                                Log.d("DID TEST", "DID TEST DidCmdAsyncTask() exeCommand ci=" + ci.rcCommandid);
                            result = onDidExeCommand(ci);
                            if ((result != null) && result.equalsIgnoreCase("Y")) {
                                switch (ci.command) {
                                    case "StoreStay.bbmc":              // 주문 알림 목록
                                    case "StoreComplete.bbmc":                 // 주문 최종 알림 완료(DID 삭제)
                                        if (mActivity != null) {
                                            PlayerCommand command = new PlayerCommand();
                                            Date currentTime = Calendar.getInstance().getTime();
                                            SimpleDateFormat simpleDateFormat =
                                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            command.executeDateTime = simpleDateFormat.format(currentTime);
                                            command.requestDateTime = simpleDateFormat.format(currentTime);
                                            command.command = getString(R.string.paycast_orderlist_update);
                                            command.addInfo = ci.command;

                                            mActivity.updateClient(command);
                                        }
                                        break;
                                    case "StoreWaitPeople.bbmc":   //대기자 수 update
                                        break;
                                    default:
                                        break;
                                }
                            }
                            return result;
                        }

                        @Override
                        public ArrayList<CommandObject> getNewCommandList() {
                            if (mDidExterVarApp.newcommandList == null)
                                mDidExterVarApp.newcommandList = new ArrayList<CommandObject>();
                            return mDidExterVarApp.newcommandList;
                        }
                    });
                    mCommandAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            else
            {
                cmdChkRetry++;
                if(cmdChkRetry>=MAX_RETRY_COUNT)
                {
                    PlayerCommand command = new PlayerCommand();
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    command.executeDateTime = simpleDateFormat.format(currentTime);
                    command.requestDateTime = simpleDateFormat.format(currentTime);
                    command.command = getString(R.string.paycast_did_network_instability);

                    if(mActivity!=null)
                        mActivity.updateClient(command);
                    cmdChkRetry = -1;
                }

                if(mDidExterVarApp.LOG_SAVE) {
                    String errlog = String.format("DID TEST: CommandCheckAsynTask() response!!!!! null\n");
                    FileUtils.writeDebug(errlog, "PayCastDid");
                }
            }
            return null;
        }
    }
    private String onDidExeCommand(CommandObject ci)
    {
        String result = "N";
        String errlog = "";

        if (ci != null) {
            switch (ci.command) {
                case "StoreStay.bbmc":              // 주문 알림 목록
                    if(LOG) {
                        Log.e(TAG, "onDidExeCommand StoreStay.bbmc start");
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreStay.bbmc start");
                    }
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreStay.bbmc start\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    result = executeStoreStay(ci);
/*
                    if(result.equalsIgnoreCase("Y"))
                    {
                        Log.e("RecyclerViewAdapter", " storeStay.bbmc alarm ci.id="+ci.rcCommandid);
                        Log.e("DID TEST", "DID TEST onAlarmSound()ci.id="+ci.rcCommandid);
                        if(mDidExterVarApp.LOG_SAVE) {
                            errlog = String.format("DID TEST: onAlarmSound()\n");
                            FileUtils.writeDebug(errlog, "PayCastDid");
                        }
                        onAlarmSound(getApplication());
                    }
*/
                    if(LOG) {
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreStay.bbmc END");
                        Log.e(TAG, "onDidExeCommand StoreStay.bbmc RESULT=" + result);
                    }
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreStay.bbmc END\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    break;
                case "StoreComplete.bbmc":                 // 주문 최종 알림 완료(DID 삭제)
                    if(LOG)
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreComplete.bbmc start");
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreComplete.bbmc start\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    result = executeCompleteList(ci);
                    if(LOG) {
                        Log.e(TAG, "onDidExeCommand StoreComplete.bbmc RESULT=" + result);
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreComplete.bbmc END");
                    }
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreComplete.bbmc END\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    break;
                case "StoreWaitPeople.bbmc" :   //대기자 수 update
                    if(LOG)
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreWaitPeople.bbmc start");
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreWaitPeople.bbmc start\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    result = executeUpdateWaitingCount(ci);
                    if(LOG) {
                        Log.d("DID TEST", "DID TEST onDidExeCommand StoreWaitPeople.bbmc END");
                        Log.e(TAG, "onDidExeCommand StoreWaitPeople.bbmc RESULT=" + result);
                    }
                    if(mDidExterVarApp.LOG_SAVE) {
                        errlog = String.format("DID TEST: onDidExeCommand StoreWaitPeople.bbmc END\n");
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    break;

                default:
                    break;
            }
        }

        return result;
    }
    private String executeStoreStay(CommandObject ci)
    {
        String result = "N";
        if(!NetworkUtil.isConnected(getApplicationContext())) {
            if(LOG)
                Log.e(TAG, "executeStoreStay() NETWROK RETURN");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: executeStoreStay() NETWROK RETURN\n");
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            return result;
        }
        if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
        {
            PlayerCommand command = new PlayerCommand();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            command.executeDateTime = simpleDateFormat.format(currentTime);
            command.requestDateTime = simpleDateFormat.format(currentTime);
            command.command = getString(R.string.paycast_did_network_instability);
            if(mActivity!=null)
                mActivity.updateClient(command);
            if(LOG)
                Log.e(TAG, "executeStoreStay() NETWROK RETURN");
            return result;

        }
        else
        {
            PlayerCommand command = new PlayerCommand();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            command.executeDateTime = simpleDateFormat.format(currentTime);
            command.requestDateTime = simpleDateFormat.format(currentTime);
            command.command = getString(R.string.paycast_did_network_stability);
            if(mActivity!=null)
                mActivity.updateClient(command);
        }

        String response = NetworkUtil.getCookalarmListFromServer(getApplicationContext(), mDidExterVarApp.mDidStbOpt);
        if(LOG)
            Log.e(TAG, "executeStoreStay() storeStay 1 response="+response);
        if((response!=null)&&(!response.isEmpty()))
        {
            waitCountRetry = -1;
            result =onParseCookAlarm(response);
            if(LOG)
                Log.e(TAG, "executeStoreStay() storeStay 2 result="+result);
            if(result.equalsIgnoreCase("Y")) {
                String param = "";
                if(LOG)
                    Log.e(TAG, "executeStoreStay() storeStay 3 datalist="+mDidExterVarApp.alarmDataList.size()+" alarmIdList.size()="+mDidExterVarApp.alarmIdList.size());
                if(mDidExterVarApp.alarmDataList.size()==0)
                    return "Y";
                if(mDidExterVarApp.alarmIdList.size()==0)
                    return "Y";
                for(int i=0; i<mDidExterVarApp.alarmIdList.size(); i++ )
                {
                    String didAlarmData = mDidExterVarApp.alarmIdList.get(i);
                    param += (didAlarmData+"|");
                }
                onAlarmSound(getApplication());

                if(LOG)
                    Log.e(TAG, "storeStay report="+param);
                result = NetworkUtil.reportCookCompleteListToServer(getApplicationContext(), mDidExterVarApp.mDidStbOpt, param);
                mDidExterVarApp.alarmIdList.clear();
                mDidExterVarApp.alarmIdList = new ArrayList<>();
            }
        }
        else if((response==null)||(response.isEmpty()))
        {
            waitCountRetry++;
            if(waitCountRetry>=MAX_RETRY_COUNT)
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);

                if(mActivity!=null)
                    mActivity.updateClient(command);
                waitCountRetry = -1;
            }

        }
        if(LOG)
            Log.e(TAG, "executeStoreStay() LIST="+mDidExterVarApp.alarmDataList.size());
        return result;

    }
    private int mCompletCmdRetry = -1;
    private String executeCompleteList(CommandObject ci)
    {
        String result = "N";
        if(!NetworkUtil.isConnected(getApplicationContext())) {
            if(LOG)
                Log.e(TAG, "executeCompleteList() NETWROK RETURN");
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("executeCompleteList() NETWROK RETURN\n");
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            return result;
        }
        if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
        {
            if(LOG)
                Log.e(TAG, "executeCompleteList() NETWROK RETURN");
            PlayerCommand command = new PlayerCommand();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            command.executeDateTime = simpleDateFormat.format(currentTime);
            command.requestDateTime = simpleDateFormat.format(currentTime);
            command.command = getString(R.string.paycast_did_network_instability);
            if(mActivity!=null)
                mActivity.updateClient(command);
            return result;

        }
        else
        {
            PlayerCommand command = new PlayerCommand();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            command.executeDateTime = simpleDateFormat.format(currentTime);
            command.requestDateTime = simpleDateFormat.format(currentTime);
            command.command = getString(R.string.paycast_did_network_stability);
            if(mActivity!=null)
                mActivity.updateClient(command);

        }

        String response = NetworkUtil.getCookCompleteListFromServer(getApplicationContext(), mDidExterVarApp.mDidStbOpt);
        if(LOG)
            Log.e(TAG, "executeCompleteList() LIST="+mDidExterVarApp.alarmDataList.size());
        //wait count
        if((ci!=null)&&(ci.prameter!=null)&&(!ci.prameter.isEmpty())&&ci.prameter.length()<10)
        {
            int waitCount = Integer.valueOf(ci.prameter);
            if ((waitCount >= 0) && (waitCount < 10000))
                mDidExterVarApp.setWaitOrderCount(waitCount);
        }

        if((response!=null)&&(!response.isEmpty()))
        {
            /*
            mDidExterVarApp.completeList.clear();
            mDidExterVarApp.completeList = new ArrayList<DidAlarmData>();
            */
            mCompletCmdRetry = -1;
            result =onParseCookComplete(response);
            if(result!=null) {
                if (result.equalsIgnoreCase("Y")) {
                    String param = "";
                    if(LOG)
                        Log.e(TAG, "reportCookCompleteListToServer() completList.size()=" + mDidExterVarApp.completeList.size() + " alarmIdList.size()=" + mDidExterVarApp.alarmIdList.size());
                    if (mDidExterVarApp.completeList.size() == 0)
                        return "Y";
                    if (mDidExterVarApp.alarmIdList.size() == 0)
                        return "Y";
                    for (int i = 0; i < mDidExterVarApp.completeList.size(); i++) {
                        DidAlarmData didCompItem = mDidExterVarApp.completeList.get(i);
                        if(LOG)
                            Log.d(TAG, "DID TEST alarmDataList.remove order_seq=" + didCompItem.orderSeq);
                        FileUtils.writeDebug("DID TEST completeList() alarmDataList.remove order_seq=" + didCompItem.orderSeq, "PayCastDid");
                        mDidExterVarApp.alarmDataList.remove(didCompItem);
                    }
                    for (int i = 0; i < mDidExterVarApp.alarmIdList.size(); i++) {
                        String item = mDidExterVarApp.alarmIdList.get(i);
                        param += (item + "|");
                    }
                    mDidExterVarApp.alarmIdList.clear();
                    mDidExterVarApp.alarmIdList = new ArrayList<>();

                    result = NetworkUtil.reportCookCompleteListToServer(getApplicationContext(), mDidExterVarApp.mDidStbOpt, param);
                }
            }
        }
        else
        {
            mCompletCmdRetry++;
            if(mCompletCmdRetry>=MAX_RETRY_COUNT)
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);

                if(mActivity!=null)
                    mActivity.updateClient(command);
                mCompletCmdRetry = -1;
            }

        }
        return result;
    }
    private int waitCountRetry = -1;
    private String executeUpdateWaitingCount(CommandObject ci)
    {
        String result = "N";


        if((ci!=null)&&(ci.prameter!=null)&&(!ci.prameter.isEmpty())&&ci.prameter.length()<10)
        {
            int waitCount = Integer.valueOf(ci.prameter);
            if(LOG)
                Log.e(TAG, "onDidExeCommand StoreWaitPeople.bbmc count="+waitCount);
            if(mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: onDidExeCommand StoreWaitPeople.bbmc count=%d\n", waitCount);
                FileUtils.writeDebug(errlog, "PayCastDid");
            }

            if ((waitCount >= 0) && (waitCount < 10000)) {
                waitCountRetry = -1;
                result = "Y";
                mDidExterVarApp.setWaitOrderCount(waitCount);
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_waitingcount_update);

                if(mActivity!=null)
                    mActivity.updateClient(command);
                else {
                    if(LOG)
                        Log.e(TAG, "Error mActivity null!!!!!");
                    result = "N";
                }
            }
        }
        else
        {
            if(!NetworkUtil.isConnected(getApplicationContext())) {
                if(LOG)
                    Log.e(TAG, "executeUpdateWaitingCount() NETWROK RETURN");
                return result;
            }
            if(NetworkUtil.getWifiRssi(getApplicationContext())<(-70))
            {
                if(LOG)
                    Log.e(TAG, "executeUpdateWaitingCount() NETWROK RETURN");
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
                return result;

            }
            else
            {
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_stability);
                if(mActivity!=null)
                    mActivity.updateClient(command);
            }

            if(ci==null)
            {
                String serverUrl = "";

                if ( mDidExterVarApp.mDidStbOpt.serverPort == 80) {
                    serverUrl = String.format("http://" +  mDidExterVarApp.mDidStbOpt.serverHost);
                } else {
                    serverUrl = String.format("http://" + mDidExterVarApp.mDidStbOpt.serverHost + ":" + mDidExterVarApp.mDidStbOpt.serverPort);
                }
                // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
                String ssl = PropUtil.configValue(getString(R.string.serverSSLEnabled), getApplication());

                if ((ssl!=null) && (Boolean.valueOf(ssl))){
                    serverUrl = serverUrl.replace("http://", "https://");
                }


                String encodedQueryString = String.valueOf("storeId=" + mDidExterVarApp.mDidStbOpt.storeId + "&deviceId=" + mDidExterVarApp.mDidStbOpt.deviceId);

                String response = NetworkUtil.getCookOrderCountServer(serverUrl + "/cookordercount", encodedQueryString);
                if((response!=null)&&(response.length()<10))
                {
                    waitCountRetry = -1;
                    int waitCount = Integer.valueOf(response);
                    if ((waitCount >= 0) && (waitCount < 10000)) {
                        result = "Y";
                        mDidExterVarApp.setWaitOrderCount(waitCount);
                        if(waitCount==0) {
                            if((mDidExterVarApp.alarmDataList!=null)&&(mDidExterVarApp.alarmDataList.size() >0)) {
                                if(mDidExterVarApp.alarmDataList!=null)
                                    mDidExterVarApp.alarmDataList.clear();
                                if(mDidExterVarApp.alarmIdList!=null)
                                    mDidExterVarApp.alarmIdList.clear();

                                mDidExterVarApp.alarmDataList = new ArrayList<>();
                                mDidExterVarApp.alarmIdList = new ArrayList<>();

                                PlayerCommand command = new PlayerCommand();
                                Date currentTime = Calendar.getInstance().getTime();
                                SimpleDateFormat simpleDateFormat =
                                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                command.executeDateTime = simpleDateFormat.format(currentTime);
                                command.requestDateTime = simpleDateFormat.format(currentTime);
                                command.command = getString(R.string.paycast_orderlist_update);

                                if(mActivity!=null)
                                    mActivity.updateClient(command);

                            }
                        }
                        PlayerCommand command = new PlayerCommand();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        command.executeDateTime = simpleDateFormat.format(currentTime);
                        command.requestDateTime = simpleDateFormat.format(currentTime);
                        command.command = getString(R.string.paycast_waitingcount_update);

                        if(mActivity!=null)
                            mActivity.updateClient(command);
                        else {
                            if(LOG)
                                Log.e(TAG, "Error mActivity null!!!!!");
                            result = "N";
                        }
                    }
                }
                else {
                    if (response == null)
                    {
                        waitCountRetry++;
                        if(waitCountRetry>=MAX_RETRY_COUNT)
                        {
                            PlayerCommand command = new PlayerCommand();
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            command.executeDateTime = simpleDateFormat.format(currentTime);
                            command.requestDateTime = simpleDateFormat.format(currentTime);
                            command.command = getString(R.string.paycast_did_network_instability);

                            if(mActivity!=null)
                                mActivity.updateClient(command);
                            waitCountRetry = -1;
                        }
                    }
                }
            }
        }
        return result;

    }
    private String onParseCookAlarm(String res)
    {
        CommandObject cmd = null;
        boolean parErr = false;
        //ArrayList<DidAlarmData> alarmDataList = new ArrayList<DidAlarmData>();

        if((res==null)||(res.isEmpty()))
            return "";
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new ByteArrayInputStream(res.getBytes());
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean isItemTag = false;

//            DidAlarmData tempDidOptData = new DidAlarmData();

            //copyStbOption(mDidExterVarApp.mDidStbOpt, tempDidOpt);
            DidAlarmData tempDidOptData = null;
            List<String> alarmIdList = new ArrayList<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        //Log.d(TAG, "START_TAG.name=" + name);
//                        if (name.equals(getResources().getString(R.string.paycast_tag)))
                        if (name.equals("SEQ"))
                        {
                            tempDidOptData = new DidAlarmData();

                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
                            tempDidOptData.updateDate = dateFormat.format(date);

                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                if (attrName.equals("orderSeq")) {
                                    String orderSeq = parser.getAttributeValue(i);
                                    if(orderSeq!=null&&!orderSeq.isEmpty())
                                        tempDidOptData.orderSeq = orderSeq;
                                    else
                                        parErr = true;
                                    if(LOG)
                                        Log.d(TAG, "orderSeq:" + parser.getAttributeValue(i));
                                } else if (attrName.equals("menucount")) {
                                    String menucount = parser.getAttributeValue(i);
                                    if(menucount!=null&&!menucount.isEmpty())
                                        tempDidOptData.menuCount = menucount;
                                    else
                                        parErr = true;
                                } else if (attrName.equals("cookAlarmId")) {
                                    String alarmid = parser.getAttributeValue(i);
                                    if ((alarmid != null) && (!alarmid.isEmpty())) {
                                        tempDidOptData.cookAlarmId = alarmid;
                                        alarmIdList.add(alarmid);
                                        if(LOG)
                                            Log.d(TAG, " cookAlarmId SIZE()="+alarmIdList.size());
                                    }
                                    else
                                        parErr = true;
                                } else if (attrName.equals("cookAlarmDate")) {
                                String alarmDate = parser.getAttributeValue(i);
                                if ((alarmDate != null) && (!alarmDate.isEmpty()))
                                    tempDidOptData.alarmDate = alarmDate;
                                else
                                    parErr = true;
                                }
                            }
                            if(parErr == false) {
//                                FileUtils.updateFile(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), getApplication(), mDidExterVarApp.mDidStbOpt);
                                ;
                            }
                        }
                        if (name.equals("Menu")) {
                            DidAlarmMenuData didMenuData = new DidAlarmMenuData();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                //Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals("orderMenuNotice")) {
                                    didMenuData.orderMenuNoti = parser.getAttributeValue(i);
                                    //Log.d(TAG, "orderMenuNotice:" + didMenuData.orderMenuNoti);
                                } else if (attrName.equals("orderMenuPacking")) {
                                    didMenuData.orderPacking = parser.getAttributeValue(i);
                                    //Log.d(TAG, "orderMenuPacking:" + didMenuData.orderPacking);
                                } else if (attrName.equals("orderMenuAmount")) {
                                    didMenuData.count = parser.getAttributeValue(i);
                                    //Log.d(TAG, "orderMenuAmount:" + didMenuData.count);
                                } else if (attrName.equals("orderMenuName")) {
                                    didMenuData.name = parser.getAttributeValue(i);
                                    //Log.d(TAG, "orderMenuName:" + parser.getAttributeValue(i));
                                } else if (attrName.equals("orderMenuNewAlarm")) {
                                    didMenuData.menuNewAlarm = parser.getAttributeValue(i);
                                    //Log.d(TAG, "orderMenuName:" + parser.getAttributeValue(i));
                                }
                            }
                            if(tempDidOptData!=null) {
                                if(tempDidOptData.menuList==null)
                                    tempDidOptData.menuList = new ArrayList<DidAlarmMenuData>();
                                if(didMenuData.menuNewAlarm.equalsIgnoreCase("Y"))
                                {
                                    tempDidOptData.menuList.add(0, didMenuData);
                                    if(LOG)
                                        Log.d(TAG, "new tempDidOptData.menuList.SIZE()"+tempDidOptData.menuList.size());
                                    for(int l = 0; l < tempDidOptData.menuList.size(); l++)
                                    {
                                        DidAlarmMenuData item = tempDidOptData.menuList.get(l);
                                        if(LOG)
                                            Log.d(TAG, "new l="+l+" item="+item.name+" orderMenuNoti="+item.orderMenuNoti);
                                    }
                                }
                                else {
                                    tempDidOptData.menuList.add(didMenuData);
                                    if(LOG)
                                        Log.d(TAG, "old tempDidOptData.menuList.SIZE()" + tempDidOptData.menuList.size());
                                    for(int l = 0; l < tempDidOptData.menuList.size(); l++)
                                    {
                                        DidAlarmMenuData item = tempDidOptData.menuList.get(l);
                                        Log.d(TAG, "old l="+l+" item="+item.name+" orderMenuNoti="+item.orderMenuNoti);
                                    }
                                }
                            }

                        }
                    }
                    break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (LOG)
                            Log.d(TAG, "TEXT = " + text);
                        if ((text != null) && !text.isEmpty()) {
                            if (cmd != null)
                                cmd.prameter = text;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name = parser.getName();
                        if (LOG)
                            Log.d(TAG, "END_TAG.name=" + name + "");
                        if (name.equals("SEQ")) {
                            if(mDidExterVarApp.alarmDataList==null)
                                mDidExterVarApp.alarmDataList = new ArrayList<DidAlarmData>();
                            int listcount = mDidExterVarApp.alarmDataList.size();
                            boolean addflag = true;
                            boolean tempDidClearFlag = true;
                            for(int i = 0; i<listcount; i++)
                            {
                                DidAlarmData alarmData = mDidExterVarApp.alarmDataList.get(i);
                                if(alarmData.orderSeq.equalsIgnoreCase(tempDidOptData.orderSeq)) {
                                    if(Integer.parseInt(alarmData.cookAlarmId) < Integer.parseInt(tempDidOptData.cookAlarmId))
                                    {
                                        addflag = false;
                                        int index = mDidExterVarApp.alarmDataList.indexOf(alarmData);
                                        mDidExterVarApp.alarmDataList.remove(alarmData);

                                        long now = System.currentTimeMillis();
                                        Date date = new Date(now);
                                        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
                                        tempDidOptData.updateDate = dateFormat.format(date);

                                        int limit = 0;
                                        if(mDidExterVarApp.alarmDataList.size()>(mDidExterVarApp.MAX_ALARAM_COUNT_PER_SCREEN-1))
                                            limit= mDidExterVarApp.MAX_ALARAM_COUNT_PER_SCREEN-1;
                                        else
                                            limit = mDidExterVarApp.alarmDataList.size();
                                        //Log.d(TAG, "alarm limit="+limit+" index="+index+" size()="+mDidExterVarApp.alarmDataList.size());
                                        if(mDidExterVarApp.alarmDataList.size()-index <= limit) {
                                            //Log.d(TAG, "1 alarm add pos="+index);
                                            mDidExterVarApp.alarmDataList.add(index, tempDidOptData);
                                            tempDidClearFlag = false;
                                        }
                                        else
                                        {
                                            int pos = getIndexAddPos(mDidExterVarApp.alarmDataList, tempDidOptData);
                                            /*
                                            Log.d(TAG, "2 alarm add pos="+pos);
                                            Log.e(TAG, "ALARM seq 1211 SEQ="+tempDidOptData.orderSeq+" UpdateDate="+tempDidOptData.updateDate);
                                            */
                                            mDidExterVarApp.alarmDataList.add(pos, tempDidOptData);
                                            tempDidClearFlag = false;
                                        }
                                        //mDidExterVarApp.alarmDataList.add(index, tempDidOptData);
                                        break;
                                    }
                                    else {
                                        tempDidClearFlag = true;
                                        addflag = false;
                                        break;
                                    }
                                }
                            }
                            if(addflag) {

                                mDidExterVarApp.alarmDataList.add(tempDidOptData);
                                mDidExterVarApp.alarmIdList = alarmIdList;

                                if(LOG)
                                    Log.d(TAG, " mDidExterVarApp.alarmIdList.size()="+mDidExterVarApp.alarmIdList.size());
                                //tempDidOptData.menuList.clear();
                            }
                            else {
                                mDidExterVarApp.alarmIdList = alarmIdList;
                                if(LOG)
                                    Log.d(TAG, " mDidExterVarApp.alarmIdList.size()="+mDidExterVarApp.alarmIdList.size());
                                if(tempDidClearFlag)
                                    tempDidOptData.menuList.clear();
                            }
                        }
                        break;
                    default:
                        Log.d(TAG, "EVENT TYPE = " + eventType);
                        break;
                }
//                Log.d(TAG, "parser.NEXT="+parser.nextToken());
                eventType = parser.next();

            }


        } catch (Exception e) {
            Log.d(TAG, "2 Error in ParseXML()", e);
        }
        if(parErr==false) {
/*
            for(int i = 0; i<mDidExterVarApp.alarmDataList.size(); i++) {
                DidAlarmData data = mDidExterVarApp.alarmDataList.get(i);
                data.menuList.clear();
            }
            mDidExterVarApp.alarmDataList.clear();
            mDidExterVarApp.alarmDataList = alarmDataList;
*/
            return "Y";
        }
        return "N";
    }
    private String onParseCookComplete(String res)
    {
        CommandObject cmd = null;
        boolean parErr = false;

        if((res==null)||(res.isEmpty()))
            return "";

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new ByteArrayInputStream(res.getBytes());
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean isItemTag = false;

//            DidAlarmData tempDidOptData = new DidAlarmData();

            //copyStbOption(mDidExterVarApp.mDidStbOpt, tempDidOpt);
            DidAlarmData tempDidOptData = null;
/*
            mDidExterVarApp.alarmDataList.clear();
            mDidExterVarApp.alarmDataList = new ArrayList<DidAlarmData>();
*/
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        //Log.d(TAG, "START_TAG.name=" + name);
//                        if (name.equals(getResources().getString(R.string.paycast_tag)))
                        if (name.equals("SEQ"))
                        {
                            tempDidOptData = new DidAlarmData();

                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                if (attrName.equals("orderSeq")) {
                                    String orderSeq = parser.getAttributeValue(i);
                                    if(orderSeq!=null&&!orderSeq.isEmpty())
                                        tempDidOptData.orderSeq = orderSeq;
                                    else
                                        parErr = true;
                                    if(LOG)
                                        Log.d(TAG, "orderSeq:" + parser.getAttributeValue(i));
                                } else if (attrName.equals("cookAlarmId")) {
                                    String alarmid = parser.getAttributeValue(i);
                                    if ((alarmid != null) && (!alarmid.isEmpty()))
                                        tempDidOptData.cookAlarmId = alarmid;
                                    else
                                        parErr = true;
                                }
                            }
                            if(parErr == false) {
//                                FileUtils.updateFile(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid), getApplication(), mDidExterVarApp.mDidStbOpt);
                                ;
                            }
                        }
                    }
                    break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (LOG)
                            Log.d(TAG, "TEXT = " + text);
                        if ((text != null) && !text.isEmpty()) {
                            if (cmd != null)
                                cmd.prameter = text;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name = parser.getName();
                        if (LOG)
                            Log.d(TAG, "END_TAG.name=" + name + "");
                        if (name.equals("SEQ")) {
                            if(mDidExterVarApp.alarmDataList==null)
                                mDidExterVarApp.alarmDataList = new ArrayList<DidAlarmData>();
                            int listcount = mDidExterVarApp.alarmDataList.size();
                            if(mDidExterVarApp.completeList==null)
                                mDidExterVarApp.completeList = new ArrayList<DidAlarmData>();
                            if(LOG)
                                Log.e(TAG, "onParseCookComplete() listcount="+listcount);
                            for(int i = 0; i<listcount; i++)
                            {
                                DidAlarmData completeData = mDidExterVarApp.alarmDataList.get(i);
                                if(LOG)
                                    Log.e(TAG, "onParseCookComplete() completeData.orderSeq="+completeData.orderSeq+" tempDidOptData.orderSeq="+tempDidOptData.orderSeq);
                                if(completeData.orderSeq.equalsIgnoreCase(tempDidOptData.orderSeq)) {
/*
                                    mDidExterVarApp.alarmDataList.remove(completeData);
                                    DidCompleteItem completeItem = new DidCompleteItem();
                                    completeItem.cookAlarmId =tempDidOptData.cookAlarmId;
                                    completeItem.orderSeq = tempDidOptData.orderSeq;
*/
                                    mDidExterVarApp.completeList.add(completeData);
                                    break;
                                }
                            }
                            mDidExterVarApp.alarmIdList.add(tempDidOptData.cookAlarmId);
                        }
                        break;
                    default:
                        Log.d(TAG, "EVENT TYPE = " + eventType);
                        break;
                }
//                Log.d(TAG, "parser.NEXT="+parser.nextToken());
                eventType = parser.next();

            }


        } catch (Exception e) {
            Log.d(TAG, "2 Error in ParseXML()", e);
        }
        if(parErr==false)
            return "Y";
        return "N";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAgentCmdReceiver);
    }
    private boolean mIsAlarmPlaying = false;

    private class AlarmDelayTimerTask extends TimerTask {
        @Override
        public void run() {
            if(mIsAlarmPlaying)
            {
                mAlarmDelayTimer = new Timer();
                mAlarmDelayTimer.schedule(new AlarmDelayTimerTask(), ALARM_DELAY_TIME_INTERVAL);
            }
            else
            {
                onAlarmSound(getApplication());
            }
        }

    }
    private MediaPlayer mPlayer = null;
    private AsyncTask mPlayerAsync;
    public void onAlarmSound(Context ctx) {
    /*
        if((mPlayer!=null)&&(mPlayer.isPlaying()))
        {
            Log.d("onAlarmSound()", "중복.. stop()");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

*/
        if(LOG)
            Log.e(TAG, "onAlarmSound() START...");
         mPlayer = new MediaPlayer();

         if((mPlayerAsync == null)||(mPlayerAsync.getStatus()== AsyncTask.Status.FINISHED)||(mPlayerAsync.isCancelled())) {
             mPlayerAsync = new AsyncTask<Void, Void, Boolean>()
             {
                 @Override
                 protected Boolean doInBackground(Void... voids) {
                     try {
                         mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                             @Override
                             public void onCompletion(MediaPlayer mp) {
                                 mPlayer.reset();
                                 mPlayer.release();
                             }
                         });
                         mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                             @Override
                             public void onPrepared(MediaPlayer mp) {
                                 mPlayer.start();
                             }
                         });
                         AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.alarmsound);
                         if (afd == null) return false;
                         mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                         afd.close();

                         if (Build.VERSION.SDK_INT >= 21) {
                             mPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                     .setUsage(AudioAttributes.USAGE_ALARM)
                                     .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                     .build());
                         } else {
                             mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                         }
                         mPlayer.setVolume(1.0f, 1.0f);
                         mPlayer.prepare();
                         return true;
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     return false;
                 }

             }.execute();
         }
         else
         {
             if(LOG)
                 Log.e(TAG, " onAlarmSound() STATUS="+mPlayerAsync.getStatus());
         }
/*
        if(mPlayer==null)
            mPlayer = MediaPlayer.create(this, R.raw.alarmsound);
        mIsAlarmPlaying = true;
        mPlayer.reset();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });
        mPlayer.prepareAsync();
        //mPlayer.start();
        Log.d("onAlarmSound()", "start()");
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.stop();
                mp.release();
                mPlayer = null;
                mIsAlarmPlaying = false;
                Log.d("onAlarmSound()", "onCompletion()");
            }

        });
        mPlayer.setLooping(false);
*/
    }
    public int getIndexAddPos(List<DidAlarmData> list, DidAlarmData newitem) {
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");

        for(int i = 0; i<mDidExterVarApp.MAX_ALARAM_COUNT_PER_SCREEN; i++)
        {
            DidAlarmData item = list.get(i);
            Date date1 = null;
            try {
                date1 = dateFormat.parse(item.alarmDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = null;
            try {
                date2 = dateFormat.parse(newitem.alarmDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date2.after(date1))
            {
                return  list.size();
            }
        }
        return 0;
    }
    public class DidUpdateWaitingCountAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            executeUpdateWaitingCount(null);
            return null;
        }
    }
}
