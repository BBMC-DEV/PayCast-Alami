package kr.co.bbmc.selforderutil;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

public class UdpServerThread extends Thread {
    private final static String TAG = "UdpServerThread";

    private int serverPort;
    private DatagramSocket socket;
    private Context context;
    boolean running;
    private XmlOptionParser mXmlOptUtil;

    public UdpServerThread(Context c, int serverPort, Application application) {
        super();
        this.serverPort = serverPort;
        this.context = c;
        mXmlOptUtil = new XmlOptionParser();

        Log.d(TAG, "UdpServerThread UDP PORT="+serverPort);
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        running = true;

        try {
            String reqCommand;
            String retStr = null;

            socket = new DatagramSocket(serverPort);

//            updateState("UDP Server is running");
            Log.e(TAG, "UDP Server is running");


            while(running){
                byte[] buf = new byte[1024];
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);     //this code block the program flow

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                String sentence = new String( packet.getData(), Charset.defaultCharset());
                reqCommand = sentence.substring(0, packet.getLength());
                Log.d(TAG, "1 reqCommand="+reqCommand+" length="+reqCommand.length());
                Log.d(TAG, "Request from: " + address + ":" + port + "\n");

/*
            for (Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet())
            {
                final String value = new String(packet.getData(), entry.getValue());
                Log.d("character set Result", value);
            }
*/

                if (reqCommand.equals("MN") || reqCommand.equals("MF") || reqCommand.equals("PF") ||
                        reqCommand.equals("RB") || reqCommand.equals("HT") || reqCommand.equals("SB") ||
                        reqCommand.equals("CONNECT"))
                {
                    retStr = Utils.MakeCommandFile(reqCommand) ? "Y" : "N";
                }
                else if (reqCommand.equals("Chk"))
                {
                    retStr = SettingPersister.getPlayerStatus();
//                    Log.e(TAG, "Chk() STATUS ="+retStr);
                }
                else if (reqCommand.startsWith("Capture:"))
                {
                    retStr = "Y";
                    if(SettingPersister.getCaptureNow()==false) {
                        SettingPersister.setCaptureNow(true);
                        String captureParam = reqCommand.substring(8);
                        SettingPersister.setCaptureParam(captureParam);
                    }
                }
                // jason:restartifnewer: 새로운 상시 스케줄 유무 체크(2015/08/31)
                else if (reqCommand.equals("RestartPlayerIfNewer"))
                {
                    retStr = Utils.MakeCommandFile("CR") ? "Y" : "N";
                }
                //-
                // jason:shutdown: 플레이어 프로그램 종료(2015/11/09)
                else if (reqCommand.equals("Shutdown"))
                {
                    retStr = Utils.MakeCommandFile("SD") ? "Y" : "N";
                }
                //-
                else if (reqCommand.equals("RestartPlayer"))
                {
                    retStr = Utils.MakeCommandFile("RP") ? "Y" : "N";
                }
                else if (reqCommand.equals("Restart"))
                {
/*
                    List<NameValuePair> transTempFileList = exterVarApp.getListTransTempFiles();
                    Log.e(TAG, "Restart  !!!");
                    Log.d(TAG, "reCommand Restart.. transTempFileList.size()=" + transTempFileList.size());
                    if (transTempFileList.size() > 0) {
//                    List<KeyValuePair<String, String>> transTempFileList = mTransTempFiles.ToList();

                        // jason:multictntmount: 다중 스케줄 탑재(2013/01/21)
                        String mountPoint = mXmlOptUtil.MountPointFromSchedule(transTempFileList.get(0).getValue());

                        for (NameValuePair pair : transTempFileList) {
                            try {
                                FileUtils.copyFileOrDirectory(pair.getValue(), pair.getName().toLowerCase().endsWith(".scd") ? pair.getName() : FileUtils.MountedRelativeFilename(mountPoint, pair.getName()));
                                File scrFile = new File(pair.getValue());
                                scrFile.delete();
                            } catch (Exception ex) {
                                Utils.debugException("Copy content before Restart", ex);
                            }
                        }

                        transTempFileList.clear();
                    }
                    // -
*/
/*
                    retStr = "Y";
                    MainService.removeCommandByUkid(PlayerCommand.CommandUkid.UserRequest);
                    PlayerCommand command = new PlayerCommand(PlayerCommand.PlayerCommandType.Restart,
                            Utils.getCurrentDate());
                    final Intent sendIntent = MainService.sendPlayerCommand(command);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.sendBroadcast(sendIntent);
                        }

                    }, 3000);
                    //PlayerCommands.add(command);
*/
                }
                else if (reqCommand.startsWith("Ping:"))
                {
                    String tmp = reqCommand.substring(5);

                    Log.d(TAG, "reqCommand="+reqCommand);

                    Boolean ret = false;
                    Log.e(TAG, "Ping:"+tmp);
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        Process ipProcess = runtime.exec("/system/bin/ping -c 1 "+tmp);

                        int exitValue = ipProcess.waitFor();  // 0 : 성공, 1 : fail, 2 : error이다.
                        Log.d(TAG, "EXIT VALUE=" + exitValue);
                        if(exitValue == 0)
                            ret = true;
                        else
                            ret = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retStr = ret ? "Y" : "N";
                }
                else if (reqCommand.startsWith("SetManagerIp:"))
                {
/*
                    String tmp = reqCommand.substring(13);
                    if ((tmp!=null&&!tmp.isEmpty())&& tmp.length() >= 3 && !tmp.startsWith("|") && !tmp.endsWith("|"))
                    {
                        mPlayerOpt.optionManagerIp = tmp.substring(0, tmp.indexOf("|"));
                        mPlayerOpt.optionManagerTcpPort = Integer.parseInt(tmp.substring(tmp.indexOf("|") + 1));
                        File dir = FileUtils.makeDirectory(FileUtils.BBMC_DIRECTORY);
                        File f = FileUtils.updatePlayerOptionFile(dir, FileUtils.BBMC_DIRECTORY + FileUtils.getFilename(FileUtils.Player),context, mPlayerOpt);

                    }
*/
                    retStr = "Y";
                }
                else if (reqCommand.startsWith("GetConfig:"))
                {
                    retStr = "Y";
                    String tempSavedConfig = Utils.tempSavedConfigFileName()+".xml";
                    OnMakePlayerConfigData makePlayerConfigDataThread = new OnMakePlayerConfigData();
                    makePlayerConfigDataThread.setParam(reqCommand, tempSavedConfig, context);
                    makePlayerConfigDataThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else if (reqCommand.startsWith("SetConfig:"))
                {
/*
                    String tmp = reqCommand.substring(10);
                    if (tmp.endsWith("|")) {
                        exterVarApp.setReceiveConfigSeq(Integer.valueOf(tmp.replace("|", "")));
                        exterVarApp.setReceiveBgImgFile("");
                    } else {
                        exterVarApp.setReceiveConfigSeq(Integer.valueOf(tmp.substring(0, tmp.indexOf("|"))));
                        exterVarApp.setReceiveBgImgFile(tmp.substring(tmp.indexOf("|") + 1));
                    }
                    retStr = "Y";
                    exterVarApp.setResultOpCode(".");
                    exterVarApp.setReceiveConfigNow(true);
*/
                    //readRequestedStbConfigFile();
                } else if (reqCommand.equals("OperationResult")) {
/*
                    retStr = exterVarApp.getResultOpCode();

                    Log.d(TAG, "2 OperationResult retStr="+retStr);
*/
                } else if (reqCommand.equals("CleanOperationResult")) {
/*
                    exterVarApp.setResultOpCode("");
                    retStr = "Y";
*/
                }
                else if (reqCommand.equals("RCMode")) {
                    retStr = "Y";
                    Log.e(TAG, "RCMode  !!!");
/*
                    final Intent sendIntent =MainService.waitDuringRemoteControl(true);
                    if(sendIntent!=null)
                    {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.sendBroadcast(sendIntent);
                            }
                        }, 3000);
                    }
*/
                }
                else if (reqCommand.equals("SetConfig")) {
                    retStr = "Y";
                    Log.e(TAG, "SetConfig() File write start...!!!");

                    String tempSavedConfigFile = SettingPersister.getRecConfigTempFileName();
/*
                    if(tempSavedConfigFile!=null) {

                        File dir = FileUtils.makeDirectory(FileUtils.BBMC_DIRECTORY);

                        FileUtils.readRequestedStbConfigFile(new XmlOptionParser(), tempSavedConfigFile, mPlayerOpt, context);
                        FileUtils.updatePlayerOptionFile(dir, FileUtils.BBMC_DIRECTORY + FileUtils.getFilename(FileUtils.Player), context, mPlayerOpt);

                        try {
                            SettingPersister.setPlayerOptionEnv(mPlayerOpt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        FileUtils.updatePlayerOptionFile(dir, FileUtils.BBMC_DIRECTORY + FileUtils.getFilename(FileUtils.Player), context, mPlayerOpt);

                        Log.e(TAG, "SetConfig() File write done!!!");
                    }
*/
                }
                else
                    retStr = "?";

                String dString = new Date().toString() + "\n"
                        + "Your address " + address.toString() + ":" + String.valueOf(port);
                buf = retStr.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

            }

            Log.e(TAG, "UDP Server ended");

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                Log.e(TAG, "socket.close()");
            }
        }
    }
    public static class OnMakePlayerConfigData  extends AsyncTask<String, String, String> {
        String saveConfg = null;
        Context context = null;
        String reqCommand =null;
        public void setParam(String reqCommd, String saveConfg, Context c)
        {
            this.reqCommand = reqCommd;
            this.saveConfg = saveConfg;
            this.context = c;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (FileUtils.saveRemotePlayerConfigData( this.saveConfg, this.context))
            {
                int sendConfigSeq =  Integer.parseInt(this.reqCommand.substring(10));
                SettingPersister.setSendConfigSeq(sendConfigSeq);
                SettingPersister.setSendConfigNow(true);
                SettingPersister.SetSendConfigTempFileName(this.saveConfg);

            }
            else
            {
//                retStr = "N";
                SettingPersister.setSendConfigNow(false);
            }
            Log.e(TAG, "OnSavePlayerConfigData doInBackground() sendConfig="+SettingPersister.getSendConfigNow());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, "OnSavePlayerConfigData onPostExecute()");
            super.onPostExecute(s);
        }
    }
}
