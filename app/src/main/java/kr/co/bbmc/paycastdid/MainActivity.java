package kr.co.bbmc.paycastdid;

import android.Manifest;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bbmc.selforderutil.AuthKeyFile;
import kr.co.bbmc.selforderutil.CustomAlertDialog;
import kr.co.bbmc.selforderutil.FileUtils;
import kr.co.bbmc.selforderutil.NetworkUtil;
import kr.co.bbmc.selforderutil.PlayerCommand;
import kr.co.bbmc.selforderutil.ProductInfo;
import kr.co.bbmc.selforderutil.ServerReqUrl;

public class MainActivity extends AppCompatActivity implements DidMainService.Callbacks {
    private final static String TAG = "MainMenuActivity";
    private boolean LOG = false;
    private DidExternalVarApp mDidExterVarApp;          //PayCastDid app 의 전역변수
    private Intent mServiceIntent;                      //DidMainService 구동/정지를 위한 intent
    private RecyclerView mRecyclerView;                 //recyclerView
    private TextView mWaitingCountView;                 //대기자 수 display
    private static final int COLUMNS = 4;               //autofit 컬럼수 지정. 한 줄에 display 되는 item 의 갯수 지정
    private RecyclerViewAdapter mRecyclerAdapter;       //did 알리미 adapter
    private List<OrderListItem> mDidlist;           //DID 알리미에 display 할 list
    private CustomAlertDialog mAuthDlg;         //기기 인증 실패 시에 dialog 생성
    private FcmTokenAsyncTask mFcmAsyncTask;     //FCM token을 서버에 전달하는 async task
    private static Timer fcmTokenTimer;         //fcm token 서버 전달시에 main Thread 에서 network으로 전달할 수 없다.  async task 를 구동하기 위해 필요.
    private DidMainService mDidService;     //DidMainService message 통신시 사용
    private BroadcastReceiver mNetworkChgReceiver;
    private AlertDialog mAlert = null;
    private Context mContext = null;
    private CoordinatorLayout mMaincontent = null;
    private LinearLayout mWifiDisconnected;
    private CreateBgBitmapTask mBgBitmapTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SettingEnvPersister.initPrefs(this);


        setContentView(R.layout.activity_main);

        permissioncheck();
        scheduleAlarm();
        mContext = getApplicationContext();
        mDidExterVarApp = (DidExternalVarApp) getApplication();
        mServiceIntent = new Intent(this, DidMainService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!

        mMaincontent = findViewById(R.id.id_main_content);

        mBgBitmapTask = new CreateBgBitmapTask();
        mBgBitmapTask.onSetImageFile("background.jpg");
        mBgBitmapTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LayoutInflater inflater = getLayoutInflater();

        View myLayout = inflater.inflate(R.layout.message_layout_view, mMaincontent, false);

        mWifiDisconnected = (LinearLayout) myLayout.findViewById(R.id.wifiDisconnected);
        mMaincontent.addView(myLayout);

        mDidlist = SettingEnvPersister.getDidOrderList();
        if (mDidlist == null)
            mDidlist = new ArrayList<>();
        else {
            mDidExterVarApp.alarmDataList = SettingEnvPersister.getDidAlarmOrderList();

            if (mDidExterVarApp.alarmDataList == null)
                mDidExterVarApp.alarmDataList = new ArrayList<>();
        }
        mRecyclerAdapter = new RecyclerViewAdapter(this, mDidlist, new RecyclerViewAdapter.ItemListener() {
            /*
                        @Override
                        public void onTimeOutCb(Drawable bg) {
                            Log.d(TAG, "onTimeOutCb()");

                        }
            */
            @Override
            public int onSetAnimation(String order, String date, long diff) {
                if (LOG)
                    Log.d(TAG, "onSetAnimation() pos=" + order);
                if (mDidExterVarApp.blinkDataList == null)
                    mDidExterVarApp.blinkDataList = new ArrayList<>();
                boolean addblinkFlag = true;
                for (int i = 0; i < mDidExterVarApp.blinkDataList.size(); i++) {
                    BlinkAlarmData blinkItem = mDidExterVarApp.blinkDataList.get(i);
                    if (blinkItem.orderSeq.equalsIgnoreCase(order)) {
/*
                        if(blinkItem.alarmDate.equalsIgnoreCase(date))
                        {
                            addblinkFlag = false;
                            break;
                        }
                        else
*/
                        {
                            blinkItem.blinkTimer.cancel();
                            blinkItem.blinkTimerTask.cancel();
                            blinkItem.animation.cancel();
                            mDidExterVarApp.blinkDataList.remove(i);
                            break;
                        }
                    }
                }
                if (LOG)
                    Log.e(TAG, "ALARM seq OnBlinkTimerTask() onSetAnimation() addblinkFlag=" + addblinkFlag);
                if (addblinkFlag) {
                    BlinkAlarmData blinkItem = new BlinkAlarmData();
                    blinkItem.blinkFlag = "Y";
                    blinkItem.orderSeq = order;
                    blinkItem.alarmDate = date;
                    blinkItem.blinkTimer = new Timer("blink timer " + order);
                    blinkItem.blinkTimerTask = new OnBlinkTimerTask();
                    blinkItem.blinkTimerTask.setBlinkAlarmData(blinkItem);
                    blinkItem.blinkTimer.schedule(blinkItem.blinkTimerTask, diff);

                    blinkItem.animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                    mDidExterVarApp.blinkDataList.add(blinkItem);
                    RecyclerViewAdapter.ViewHolder v = mRecyclerAdapter.getViewHold(order);
                    if (v != null) {
                        if (v.textView != null) {
                            v.textView.setAnimation(blinkItem.animation);
                            v.textView.startAnimation(blinkItem.animation);
                            if (LOG)
                                Log.d(TAG, "OnBlinkTimerTask() onSetAnimation() v.textView=" + v.textView.getText().toString());
                            v.textView.setTextColor(Color.RED);
                        }
                        if (v.recyclerLayout != null)
                            v.recyclerLayout.setBackgroundResource(R.drawable.bg_red_line);
                    }
                    if (LOG)
                        Log.d(TAG, "OnBlinkTimerTask() onSetAnimation() v=" + v);
                    return mDidExterVarApp.blinkDataList.size();
                }
                return -1;
            }
/*
            @Override
            public void onClearAnimation(int pos) {
                Log.d(TAG, "onClearAnimation()pos="+pos);
                RecyclerViewAdapter.ViewHolder v = mRecyclerAdapter.getViewHold(pos);
                if(v!=null) {
                    v.mViewholder.getAnimation().cancel();
                    v.mViewholder.clearAnimation();
                    v.mViewholder.getAnimation().setAnimationListener(null);
                }
            }
*/
        });

        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, COLUMNS);
        //recyclerView.setLayoutManager(layoutManager);
        //GridLayoutManager manager = new GridLayoutManager(this, COLUMNS);
        // Set up the ViewPager with the sections adapter.
        mRecyclerView = (RecyclerView) findViewById(R.id.container);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mWaitingCountView = (TextView) findViewById(R.id.id_waiting_count);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        if (LOG)
                            Log.d(TAG, "getTogken : " + token);
                        mDidExterVarApp.token = token;
                        if (fcmTokenTimer == null) {
                            if (LOG)
                                Log.e(TAG, "fcmTokenTimerTask() SET");
                            fcmTokenTimer = new Timer("fcmTokenTimer");
                            fcmTokenTimer.schedule(new fcmTokenTimerTask(), 1000);
                        }
                    }
                });
        hideSystemBar();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkChgReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (!NetworkUtil.isConnected(context)) {
                    if (mWifiDisconnected != null)
                        mWifiDisconnected.setVisibility(View.VISIBLE);
                } else {
//                            if((mAlert!=null) && mAlert.isShowing())
//                                mAlert.dismiss();
                    if (NetworkUtil.getWifiRssi(getApplicationContext()) < (-70)) {
                        if (mWifiDisconnected != null)
                            mWifiDisconnected.setVisibility(View.VISIBLE);
                    } else {
                        if (mWifiDisconnected != null)
                            mWifiDisconnected.setVisibility(View.GONE);

                    }

                }
            }
        };
        registerReceiver(mNetworkChgReceiver, intentFilter);
        if (!NetworkUtil.isConnected(getApplicationContext())) {
            //show(getString(R.string.str_paycastdid_network_title), getString(R.string.str_paycastdid_check_network));
            if (mWifiDisconnected != null)
                mWifiDisconnected.setVisibility(View.VISIBLE);

        } else if (NetworkUtil.getWifiRssi(getApplicationContext()) < (-70)) {
            if (mWifiDisconnected != null)
                mWifiDisconnected.setVisibility(View.VISIBLE);
            //show(getString(R.string.str_paycastdid_network_title), getString(R.string.str_paycastdid_strength_network));
        }
    }


    void show(String title, String body) {
        if ((mAlert != null) && (mAlert.isShowing()))
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        mAlert = builder.create();
        // Title for AlertDialog
        mAlert.setTitle(title);
        // Icon for AlertDialog
        mAlert.show();

    }

    class OnBlinkTimerTask extends TimerTask {
        private BlinkAlarmData blinkAlarmData;
        private boolean cancelFlag = false;

        public void setBlinkAlarmData(BlinkAlarmData blink) {
            blinkAlarmData = blink;
            if (LOG)
                Log.d(TAG, "OnBlinkTimerTask() setBlinkAlarmData (blinkAlarmData.orderSeq=" + blinkAlarmData.orderSeq);
        }

        @Override
        public void run() {
            if (cancelFlag)
                return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ((mDidExterVarApp.blinkDataList != null) && (mDidExterVarApp.blinkDataList.size() > 0)) {
                        if (blinkAlarmData.animation != null)
                            blinkAlarmData.animation.cancel();
                        RecyclerViewAdapter.ViewHolder v = mRecyclerAdapter.getViewHold(blinkAlarmData.orderSeq);
                        if (v != null) {
                            if (v.textView != null) {
                                if (LOG) {
                                    Log.d(TAG, "OnBlinkTimerTask() time out (blinkAlarmData.orderSeq=" + blinkAlarmData.orderSeq);
                                    Log.d(TAG, "OnBlinkTimerTask() time out textView.text=" + v.textView.getText().toString());
                                }
                                v.textView.setTextColor(Color.BLACK);
                                v.textView.clearAnimation();
                            }
                            if (v.recyclerLayout != null)
                                v.recyclerLayout.setBackgroundResource(R.drawable.bg_white_line);
                        }
                        mDidExterVarApp.blinkDataList.remove(blinkAlarmData);
                        mRecyclerAdapter.notifyDataSetChanged();
                        if (LOG)
                            Log.d(TAG, "OnBlinkTimerTask() time out blinkDataList.size()=" + mDidExterVarApp.blinkDataList.size());
                    }
                }
            });
        }

        @Override
        public boolean cancel() {
            cancelFlag = true;
            if (LOG)
                Log.d(TAG, "OnBlinkTimerTask() cancel (blinkAlarmData.orderSeq=" + blinkAlarmData.orderSeq + " blinkDataList.size()=" + mDidExterVarApp.blinkDataList.size());
            return super.cancel();
        }
    }

    private class fcmTokenTimerTask extends TimerTask {

        @Override
        public void run() {
            if (LOG)
                Log.e(TAG, "fcmTokenTimerTask() RUN");
            if ((mFcmAsyncTask == null) || mFcmAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED) || mFcmAsyncTask.isCancelled()) {
                mFcmAsyncTask = new FcmTokenAsyncTask();
                mFcmAsyncTask.setToken(mDidExterVarApp.token);
                mFcmAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "mFcmAsyncTask");
            }
//            fcmTokenTimer =
        }
    }

    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), PayCastDidAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, PayCastDidAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                2 * 60 * 1000, pIntent);
    }

    public void onCancelscheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), PayCastDidAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), PayCastDidAlarmReceiver.REQUEST_CODE, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    //하단 시스템바 숨기기
    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else { // getWindow().getDecorView().setSystemUiVisibility(View.GONE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

    }

    public void permissioncheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    }

                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((mAlert != null) && mAlert.isShowing())
            mAlert.dismiss();
        if (isFinishing()) {
            if ((mBgBitmapTask != null) && (mBgBitmapTask.getStatus() != AsyncTask.Status.FINISHED))
                mBgBitmapTask.cancel(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG)
            Log.e(TAG, "onDestroy()!!!!! UNBIND Service..");
        if (mNetworkChgReceiver != null)
            unregisterReceiver(mNetworkChgReceiver);
        mNetworkChgReceiver = null;
        unbindService(mConnection);
        stopService(mServiceIntent);
        if (fcmTokenTimer != null) {
            fcmTokenTimer.cancel();
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            DidMainService.LocalBinder binder = (DidMainService.LocalBinder) service;
            mDidService = binder.getServiceInstance(); //Get instance of your service!
            mDidService.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
            //tvServiceState.setText("Connected to service...");
            //tbStartTask.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
            //tvServiceState.setText("Service disconnected");
            //tbStartTask.setEnabled(false);
        }
    };


    @Override
    public void updateClient(final PlayerCommand cmd) {
//        Log.d(TAG, "auth fail");
//                            onProductKeyGuidPopUp();
//        Logger.w("update client : " + cmd.command);

        if (cmd.command.equalsIgnoreCase(getString(R.string.paycast_did_network_stability))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
/*
                            if((mAlert!=null)&&(mAlert.isShowing()))
                                mAlert.dismiss();
*/
                            if (mWifiDisconnected != null)
                                mWifiDisconnected.setVisibility(View.GONE);

                        }
                    });
                }
            }).start();
        } else if (cmd.command.equalsIgnoreCase(getString(R.string.paycast_did_network_instability))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWifiDisconnected != null)
                                mWifiDisconnected.setVisibility(View.VISIBLE);
                            //show(getString(R.string.str_paycastdid_network_title), getString(R.string.str_paycastdid_strength_network));
                        }
                    });
                }
            }).start();
        } else if (cmd.command.equalsIgnoreCase(getString(R.string.str_command_player_deviceid)))   //device id update
        {
            if (LOG)
                Log.d(TAG, "auth success");
            if ((cmd.addInfo != null) && (!cmd.addInfo.isEmpty())) {
                mDidExterVarApp.mDidStbOpt.deviceId = cmd.addInfo;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((mAlert != null) && (mAlert.isShowing()))
                                    mAlert.dismiss();
                            }
                        });
                    }
                }).start();
            }
        } else if (cmd.command.equalsIgnoreCase(getString(R.string.str_command_player_auth_fail)))   //auth fail
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if((mAlert!=null)&&(mAlert.isShowing()))
//                                mAlert.dismiss();
//
//                            android.app.FragmentManager fm = getFragmentManager();
//                            FragmentTransaction tr = fm.beginTransaction();
//                            android.app.Fragment prev = fm.findFragmentByTag("auth alert");
//                            if (prev != null) {
//                                //tr.remove(prev);
//                                return;
//                            }
//
//                            mAuthDlg = new CustomAlertDialog();
//                            mAuthDlg.setAlertDialogParam("기기인증", "기기2인증을 실패하였습니다. \r Device id를 확인하여 주시기 바랍니다.", new CustomAlertDialog.customAlertBtnClickListener() {
//                                @Override
//                                public void onSaveClick() {
//                                    mAuthDlg.dismiss();
//
//                                }
//
//                                @Override
//                                public void onCancelClick() {
//                                    mAuthDlg.dismiss();
//                                }
//                            });
//                            mAuthDlg.show(tr, "auth alert");
                        }
                    });
                }
            }).start();
        } else if (cmd.command.equalsIgnoreCase(getString(R.string.paycast_orderlist_update)))   //order list update
        {
            Intent sendIntent = new Intent(mContext, MainActivity.class);
            sendIntent.setAction(mDidExterVarApp.ACTION_ACTIVITY_UPDATE);
            startActivity(sendIntent);
        } else if (cmd.command.equalsIgnoreCase(getString(R.string.paycast_waitingcount_update)))   //waiting count update
        {
//for test 2019.06.21 start=>
            final String info = cmd.addInfo;
//<= for test 2019.06.21 end

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Logger.w("update data!!");
                    if ((mAlert != null) && (mAlert.isShowing()))
                        mAlert.dismiss();

                    if (mDidExterVarApp.LOG_SAVE) {
                        String errlog = String.format("DID TEST: onDidExeCommand StoreWaitPeople.bbmc=%d display\n", mDidExterVarApp.getWaitOrderCount());
                        // Place a breakpoint here to catch application crashes
                        FileUtils.writeDebug(errlog, "PayCastDid");
                    }
                    int a = mDidExterVarApp.getWaitOrderCount();
                    Logger.w("wait people" + a);
                    if (a > 0) {
                        mWaitingCountView.setText(String.format("대기 %d", mDidExterVarApp.getWaitOrderCount()));
                        mWaitingCountView.invalidate();
                        mWaitingCountView.setVisibility(View.VISIBLE);
                    } else {
                        mWaitingCountView.invalidate();
                        mWaitingCountView.setVisibility(View.GONE);
                    }
                    //Toast.makeText(getApplicationContext(), "command="+info, Toast.LENGTH_LONG).show();
                    if (LOG)
                        Log.e("DID TEST", "DID TEST onDidExeCommand StoreWaitPeople.bbmc display " + mDidExterVarApp.getWaitOrderCount());
                }
            });
        }
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        ProductInfo pInfo = AuthKeyFile.getProductInfo();
        if (pInfo != null) {
            AuthKeyFile.onSetFcmToken(token);

            final String tokenSaveUrl = ServerReqUrl.getServerSaveTokenUrl(mDidExterVarApp.mDidStbOpt.serverHost, mDidExterVarApp.mDidStbOpt.serverPort, getApplicationContext());
            final String tokenParam = AuthKeyFile.getFcmTokenParam();
            Logger.e("tokenParam : " + tokenParam);
            if (!NetworkUtil.isConnected(getApplicationContext())) {
                if (LOG) {
                    Log.e(TAG, "Msg_InvalidStbStatusAlert token");
                    Log.e(TAG, "sendRegistrationToServer() NETWROK RETURN");
                }
                return;
            }
            if (NetworkUtil.getWifiRssi(getApplicationContext()) < (-70)) {
                if (LOG)
                    Log.e(TAG, "sendRegistrationToServer() NETWROK RETURN");
                PlayerCommand command = new PlayerCommand();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                command.executeDateTime = simpleDateFormat.format(currentTime);
                command.requestDateTime = simpleDateFormat.format(currentTime);
                command.command = getString(R.string.paycast_did_network_instability);
                updateClient(command);
                return;

            }

            String response = NetworkUtil.HttpResponseString(tokenSaveUrl, tokenParam, getApplicationContext(), false);
            if (LOG)
                Log.d(TAG, "Token response = " + response);
        }
    }

    public class FcmTokenAsyncTask extends AsyncTask {
        private String fcmToken = "";

        public void setToken(String token) {
            fcmToken = token;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (LOG)
                Log.d(TAG, "FcmTokenAsyncTask() doInBackground()");
            if ((fcmToken != null) && (!fcmToken.isEmpty()))
                sendRegistrationToServer(fcmToken);
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        Logger.w("action : " + action);
        if (action == null) return;
        if (action.equalsIgnoreCase(mDidExterVarApp.ACTION_ACTIVITY_UPDATE)) {
            if ((mAlert != null) && (mAlert.isShowing()))
                mAlert.dismiss();
            List<DidAlarmData> delList = new ArrayList<>();
            for (int i = 0; i < mDidExterVarApp.completeList.size(); i++) {
                DidAlarmData alarmData = mDidExterVarApp.completeList.get(i);
                for (int j = 0; j < mDidExterVarApp.blinkDataList.size(); j++) {
                    BlinkAlarmData blinkAlarmData = mDidExterVarApp.blinkDataList.get(j);
                    if (blinkAlarmData.orderSeq.equalsIgnoreCase(alarmData.orderSeq)) {
                        blinkAlarmData.blinkTimer.cancel();
                        if (blinkAlarmData.animation != null)
                            blinkAlarmData.animation.cancel();
                        RecyclerViewAdapter.ViewHolder v = mRecyclerAdapter.getViewHold(blinkAlarmData.orderSeq);
                        if (v != null) {
                            if (v.textView != null) {
                                v.textView.setTextColor(Color.BLACK);
                                v.textView.clearAnimation();
                            }
                            if (v.recyclerLayout != null)
                                v.recyclerLayout.setBackgroundResource(R.drawable.bg_white_line);
                        }
                        mDidExterVarApp.blinkDataList.remove(blinkAlarmData);
                        mDidExterVarApp.blinkDataList.remove(blinkAlarmData);
                        break;
                    }
                }
                for (int j = 0; j < mDidlist.size(); j++) {
                    OrderListItem didMenuItem = mDidlist.get(j);
                    if (didMenuItem.order_num.equalsIgnoreCase(alarmData.orderSeq)) {
                        mDidlist.remove(j);
                        break;
                    }
                }
                delList.add(alarmData);
                FileUtils.writeDebug(String.format("completeList[%d]=%s", i, alarmData.orderSeq), "PayCastDid");
            }

            if (mDidExterVarApp.alarmDataList.size() > 0) {
                int listCount = mDidExterVarApp.alarmDataList.size();
                List<OrderListItem> tempDidlist = new ArrayList<>();

                boolean didalarmAddFlag = true;
                if (mDidlist == null)
                    mDidlist = new ArrayList<OrderListItem>();
                for (int i = listCount - 1; i >= 0; i--) {
                    didalarmAddFlag = true;
                    DidAlarmData didAlarmData = mDidExterVarApp.alarmDataList.get(i);
                    OrderListItem orderListItem = new OrderListItem();
                    orderListItem.updateDate = didAlarmData.updateDate;
                    orderListItem.order_num = didAlarmData.orderSeq;
                    if (LOG)
                        Log.d(TAG, "DID TEST ALARM seq=" + didAlarmData.orderSeq + " updateDate=" + orderListItem.updateDate);
                    FileUtils.writeDebug(String.format("cDID TEST ALARM seq=%s", didAlarmData.orderSeq), "PayCastDid");
                    orderListItem.alarm_date = didAlarmData.alarmDate;
                    orderListItem.menulist = new ArrayList<>();
                    for (int menucount = 0; menucount < didAlarmData.menuList.size(); menucount++) {
                        OrderMenuItem menuItem = new OrderMenuItem();
                        DidAlarmMenuData didAlarmMenuData = didAlarmData.menuList.get(menucount);
                        menuItem.orderSeq = didAlarmData.orderSeq;
                        menuItem.menu_name = didAlarmMenuData.name;
                        menuItem.menu_count = didAlarmMenuData.count;
                        menuItem.orderMenuNoti = didAlarmMenuData.orderMenuNoti;
                        orderListItem.menulist.add(menuItem);
                    }
                    tempDidlist.add(orderListItem);
/*
                    for(int j = 0; j<mDidlist.size(); j++)
                    {
                        OrderListItem didMenuItem = mDidlist.get(j);
                        if(didMenuItem.order_num.equalsIgnoreCase(orderListItem.order_num))
                        {
                            mDidlist.remove(j);
                            mDidlist.add(j, orderListItem);
                            didalarmAddFlag = false;
                            break;
                        }
                    }
                    if(didalarmAddFlag)
                        mDidlist.add(0, orderListItem);
*/
                }
                mDidlist = tempDidlist;
            } else {
                mDidlist.clear();
                mDidlist = new ArrayList<OrderListItem>();
                mRecyclerAdapter.mValues.clear();
            }
            SettingEnvPersister.setDidOrderList(mDidlist);
            SettingEnvPersister.setDidAlarmOrderList(mDidExterVarApp.alarmDataList);

            for (int i = 0; i < delList.size(); i++) {
                DidAlarmData item = delList.get(i);
                mDidExterVarApp.completeList.remove(item);
            }
            if (mDidExterVarApp.completeList == null)
                mDidExterVarApp.completeList = new ArrayList<>();

            //mRecyclerAdapter.mValues.clear();
            mRecyclerAdapter.mValues = mDidlist;
            if (LOG)
                Log.e("DID TEST", "DID TEST  실제 DID display mDidlist=" + mDidlist.size() + " alarmDataList=" + mDidExterVarApp.alarmDataList.size());
            if (mDidlist.size() != mDidExterVarApp.alarmDataList.size()) {
                if (LOG)
                    Log.e("DID TEST", "DID TEST eRROR!!!!!!");
            }
            if (mDidExterVarApp.LOG_SAVE) {
                String errlog = String.format("DID TEST: DID TEST  실제 DID display 대기 %d\n", mDidExterVarApp.getWaitOrderCount());
                FileUtils.writeDebug(errlog, "PayCastDid");
            }
            Logger.e("여길 타야함!! : " + mDidExterVarApp.getWaitOrderCount());
            mWaitingCountView.setText(String.format("대기 %d", mDidExterVarApp.getWaitOrderCount()));
            mWaitingCountView.setVisibility(View.VISIBLE);
            mWaitingCountView.invalidate();
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public class CreateBgBitmapTask extends AsyncTask<Bitmap, String, String> {
        Bitmap bitmap = null;
        String filename = "";
        File imagFile;
        Drawable drawable;

        public void onSetImageFile(String name) {
            filename = name;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            File imagFile = new File(FileUtils.BBMC_PAYCAST_BG_DIRECTORY + filename);

            if (imagFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();

/*
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 3;
                options.inPurgeable = true;
*/
                Bitmap bgImage;

                try {
                    bgImage = BitmapFactory.decodeFile(imagFile.getAbsolutePath(), options);
                    drawable = new BitmapDrawable(bgImage);
                    if (bgImage == null)
                        Log.e(TAG, "CreateBgBitmapTask() bgImage is null");
                    else
                        Log.e(TAG, "CreateBgBitmapTask() bgImage is not null~~~");

                } catch (OutOfMemoryError e) {
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(imagFile.getAbsolutePath(), options);
                        return null;
                    } catch (Exception excepetion) {
                        Log.e(TAG, "Exception err=" + excepetion.toString());
                    }
                }

            }
            publishProgress("");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (drawable != null) {
                mMaincontent.setBackground(drawable);
            }
/*
            else
                mMaincontent.setBackgroundColor(Color.BLACK);
*/
            super.onProgressUpdate(values);
        }

    }

}
