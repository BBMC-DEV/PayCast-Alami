package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CommandAsynTask extends AsyncTask <String, String, String> {
    private static final String TAG = "CommandAsynTask";
    private boolean LOG = false;
    //private AgentExternalVarApp mAgentExterVarApp;
    public ArrayList<CommandObject> newcommandList = null;
    private ArrayList<CommandObject> commandList = new ArrayList<>();
    private StbOptionEnv stbOpt;
    private Context context;
    public boolean isRun = true;
    private onExecuteCommandListener mExecuteCommandListener = null;

    public interface onExecuteCommandListener{
        String exeCommand(CommandObject ci);
        ArrayList<CommandObject> getNewCommandList();
    }

    public void setApplication(StbOptionEnv stbOpt, Context context, onExecuteCommandListener mListener)
    {
        this.stbOpt = stbOpt;
        this.context = context;
        this.mExecuteCommandListener = mListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        List<CommandObject> delCommandList = new ArrayList<CommandObject>();
        isRun = true;
/*
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            newcommandList = mExecuteCommandListener.getNewCommandList();
            Log.d(TAG, "mCommandAsynTask newcommandList.SIZE=" + newcommandList.size());
            if(isRun==false)
                return null;
        }
        while(newcommandList.size()==0);
*/
        newcommandList = mExecuteCommandListener.getNewCommandList();
        if (newcommandList.size() > 0) {
            int size = newcommandList.size();
            for (int i = 0; i < size; size--) {
                commandList.add(newcommandList.get(i));
                //newcommandList.remove(i);
            }
        }
        mExecuteCommandListener.getNewCommandList().clear();
        //Log.d(TAG, "CommandAsynTask() commandList.SIZE=" + commandList.size()+" new commandlist="+mExecuteCommandListener.getNewCommandList().size());
        if (commandList.size() > 0) {
            for (CommandObject command : commandList) {
                if(mExecuteCommandListener==null)
                    return null;
                String result = mExecuteCommandListener.exeCommand(command);
                String reqUrl = ServerReqUrl.getServerRcCommandUrl(stbOpt, context);
                command.result = result;
                URI url = URI.create(reqUrl + "?" + "rcCmdId=" + command.rcCommandid + "&result=" + command.result);
                HttpClient httpClient = new DefaultHttpClient();

                HttpGet httpPost = new HttpGet(url);
                httpPost.setURI(url);
                if(LOG) {
                    Log.d(TAG, "명령실행보고  url=" + String.valueOf(url));
                    Log.e(TAG, "CommandAsynTask rcCmdId=" + command.rcCommandid + " result=" + command.result);
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    String responseString = "";
                    if(response!=null)
                        responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                    if(LOG) {
                        Log.e(TAG, "실행보고 response=" + responseString);
                    }
                    delCommandList.add(command);
//                        ParseXML(responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (CommandObject command : delCommandList) {
            commandList.remove(command);
        }
        if (newcommandList.size() > 0) {
            int size = newcommandList.size();
            for (int i = 0; i < size; size--) {
                //commandList.add(newcommandList.get(i));
                newcommandList.remove(i);
            }
        }
        return null;
    }

    @Override
    protected void onCancelled(String s) {
        isRun = false;
        super.onCancelled(s);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        isRun = false;
//        mCommandAsynTask = null;
    }

    private String executeCommand(CommandObject ci) {
        String result = "F";
/*
        if (ci != null) {
            String playerStatusCode = getPlayerAgentCheck(false);
            boolean playerControlMode = true;
            switch (ci.command) {
                case "UpdatePlayer.bbmc":              // 성공: P
                    if((ci.prameter!=null)&&(!ci.prameter.isEmpty()))
                        result = executeUpdatePlayer(playerControlMode, ci.prameter.replace("version=", ""));
                    else
                        result = executeUpdatePlayer(playerControlMode, "");
                    break;
                case "SetConfig.bbmc":                 // 성공: S
                    result = executeSetConfig(playerControlMode, ci.prameter);
                    Log.e(TAG, "CommandAsynTask SetConfig.bbmc() RESULT="+result);
                    break;
                case "MonitorOn.bbmc":                 // 성공: S
                    result = executeOnMonitor(playerControlMode);
                    break;
                case "MonitorOff.bbmc":                // 성공: S
                    result = executeOffMonitor(playerControlMode);
                    break;
                case "PowerOff.bbmc":                  // 성공: S
                    result = executeOffPower(playerControlMode);
                    break;
                case "Reboot.bbmc":                    // 성공: S
                    result = executeReboot(playerControlMode);
                    break;
                case "HideEmergenText.bbmc":           // 성공: S
                    result = executeHideEmergenText(playerControlMode);
                    break;
                case "SyncContent.bbmc":               // 성공: S(다운로드 대상 항목이 0), P(다운로드 대상 항목 존재 시)
                    Log.e(TAG, "executeCommand() SyncContent.bbmc executeSyncContent()");
                    result = executeSyncContent(ci.rcCommandid);
                    break;
                case "UploadCapture.bbmc":             // 성공: P
                    result = executeUploadCapture();
                    break;
                case "UploadCaptures.bbmc":            // 성공: P
                    result = executeUploadCaptures();
                    break;
                case "UploadTrackFile.bbmc":           // 성공: S(업로드 대상 항목이 0), P(업로드 대상 항목 존재 시)
                    result = executeUploadTrackFile();
                    break;
                case "UploadLogFile.bbmc":             // 성공: S(업로드 대상 항목이 0), P(업로드 대상 항목 존재 시)
                    result = executeUploadLogFile();
                    break;
                case "UploadDebugFile.bbmc":             // 성공: S(업로드 대상 항목이 0), P(업로드 대상 항목 존재 시)
                    result = executeUploadDebugFile();
                    break;
                case "UploadTodayFile.bbmc":             // 성공: S(업로드 대상 항목이 0), P(업로드 대상 항목 존재 시)
                    result = executeUploadTodayFile();
                    break;
                case "PowerOnWol.bbmc":                // 성공: S
                    result = "S";
                    break;
                case "DeleteTrackFile.bbmc":           // 성공: S
                    result = executeDeleteTrackFile(ci.prameter.replace("file=", ""));
                    break;
                case "RestartPlayer.bbmc":             // 성공: S
                    result = executeRestartPlayer(playerControlMode, playerStatusCode);
                    break;
                case "ShowEmergenText.bbmc":           // 성공: S
                    result = executeShowEmergenText(playerControlMode, ci.prameter);
                    break;
                case "DeleteAllSchedule.bbmc":         // 성공: S
                    result = executeDeleteAllSchedule(playerControlMode);
                    break;
                case "Debug10Mins.bbmc":                // 성공: S
                    result = executeDebug10Mins(playerControlMode);
                    break;
                // jason:restartagent: 에이전트 재시작(2015/08/31)
                case "RestartAgent.bbmc":
                    Utils.LOG(getString(R.string.Log_RCRestartAgent));
                    Utils.LOG("");

                    result = "P";
                    isAgentRestartRequired = true;
                    reStartAgent();
                    break;
                //-
                // jason:checkresourcecmd: 리소스 체크 명령(2015/09/10)
                case "CheckResources.bbmc":            // 성공: S
                    result = executeCheckResources();
                    break;
                //-
                case "SetSchedule.bbmc":
                    result = executeSetConfig(playerControlMode, ci.prameter);
                    break;

                default:
                    break;
            }

            switch (ci.command) {
                case "MonitorOn.bbmc":
                case "MonitorOff.bbmc":
                case "RestartPlayer.bbmc":
                case "DeleteAllSchedule.bbmc":
                    //case "SetConfig.bbmc":
                case "PowerOnWol.bbmc":
                    reportStbStatusToServer();
                    break;
                default:
                    break;
            }
        }
*/
        return result;
    }

}
