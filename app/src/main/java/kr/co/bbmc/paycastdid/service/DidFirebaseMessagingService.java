package kr.co.bbmc.paycastdid.service;

import static kr.co.bbmc.paycastdid.ConstantKt.ACTION_SERVICE_COMMAND;
import static kr.co.bbmc.paycastdid.ConstantKt.getFirebaseMsg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import kr.co.bbmc.paycastdid.DidExternalVarApp;
import kr.co.bbmc.paycastdid.R;
import kr.co.bbmc.selforderutil.AuthKeyFile;
import kr.co.bbmc.selforderutil.FileUtils;
import kr.co.bbmc.selforderutil.NetworkUtil;
import kr.co.bbmc.selforderutil.PlayerCommand;
import kr.co.bbmc.selforderutil.ProductInfo;
import kr.co.bbmc.selforderutil.ServerReqUrl;


public class DidFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "DidFirebaseMessagingService";
    private DidExternalVarApp mDidExterVarApp;
//    private LocalBroadcastManager broadcaster;
    @Override
    public void onCreate() {
        super.onCreate();
        mDidExterVarApp = (DidExternalVarApp) getApplication();
//        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public interface onExecuteListener{
    }
    public DidFirebaseMessagingService() {
        super();
        /*  Persister   */
        //SettingEnvPersister.initPrefs(this);

//        DidMainService.onSetFcmStart();
    }
    private void onReceiveFcmMessage() {
/*
        PlayerCommand command = new PlayerCommand();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        command.executeDateTime = simpleDateFormat.format(currentTime);
        command.requestDateTime = simpleDateFormat.format(currentTime);

        Intent sendIntent = new Intent(SingCastPlayIntent.ACTION_SERVICE_COMMAND);
        Bundle b = new Bundle();
        b.putString("executeDateTime", command.executeDateTime);
        b.putString("requestDateTime", command.requestDateTime);
        b.putString("command", command.command);
        if((command.addInfo!=null)&&(!command.addInfo.isEmpty()))
            b.putString("addInfo", command.addInfo);
        Log.d(TAG, "sendBroadcast command = " + command.command);
        sendIntent.putExtras(b);


        this.sendBroadcast(sendIntent);
*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Logger.e("Firebasae msg received : " + remoteMessage.getMessageId());
        getFirebaseMsg().onNext(Objects.requireNonNull(remoteMessage.getMessageId()));
        PlayerCommand command = new PlayerCommand();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        command.command = getString(R.string.paycast_did_fcm_receive);
        command.executeDateTime = simpleDateFormat.format(currentTime);
        command.requestDateTime = simpleDateFormat.format(currentTime);

        Intent sendIntent = new Intent(ACTION_SERVICE_COMMAND);
        Bundle b = new Bundle();
        b.putString("executeDateTime", command.executeDateTime);
        b.putString("requestDateTime", command.requestDateTime);
        b.putString("command", command.command);
        Log.e(TAG, "sendBroadcast command = " + command.command);
        sendIntent.putExtras(b);
//        broadcaster.sendBroadcast(sendIntent );
        sendBroadcast(sendIntent );
    }
    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
    }
    private void handleNow() {

        Log.d(TAG, "Short lived task is done.");

    }

    @Override
    public void onNewToken(String s) {
        if(mDidExterVarApp==null) {
            mDidExterVarApp = (DidExternalVarApp) getApplication();
            String errlog = String.format("onNewToken() mDidExterVarApp=null %s\r\n", s);
            // Place a breakpoint here to catch application crashes
            FileUtils.writeDebug(errlog, "PayCastAgent");

        }

        mDidExterVarApp.token = s;
        sendRegistrationToServer(s);

    }
    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        ProductInfo pInfo = AuthKeyFile.getProductInfo();
        if(pInfo!=null)
        {
            AuthKeyFile.onSetFcmToken(token);

            final String tokenSaveUrl = ServerReqUrl.getServerSaveTokenUrl(mDidExterVarApp.mDidStbOpt.serverHost, mDidExterVarApp.mDidStbOpt.serverPort, getApplicationContext());
            final String tokenParam = AuthKeyFile.getFcmTokenParam();
            new Thread()
            {
                public void run() {
                    if (!NetworkUtil.isConnected(getApplicationContext())) {
//                        sendShowMessage(R.string.Msg_InvalidStbStatusAlert);
                        Log.e(TAG, "Msg_InvalidStbStatusAlert token");
                        return;
                    }

                    String response = NetworkUtil.HttpResponseString(tokenSaveUrl, tokenParam, getApplicationContext(), false);
                    Log.d(TAG, "Token response = " + response);
                }
            }.start();
        }
    }
    private void sendNotification(String messageBody) {

    }
}
