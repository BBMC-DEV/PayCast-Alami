package kr.co.bbmc.paycastdid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import kr.co.bbmc.selforderutil.CommandObject;
import kr.co.bbmc.selforderutil.DidOptionEnv;
import kr.co.bbmc.selforderutil.NetworkUtil;
import kr.co.bbmc.selforderutil.ServerReqUrl;
import kr.co.bbmc.selforderutil.Utils;

public class DidCmdAsyncTask extends AsyncTask {
    private static final String TAG = "DidCommandAsyncTask";
    public ArrayList<CommandObject> newcommandList = null;
    private ArrayList<CommandObject> commandList = new ArrayList<>();
    private DidOptionEnv didOpt;
    private Context context;
    public boolean isRun = true;
    private onExecuteCmdListener mExecuteCommandListener = null;

    public interface onExecuteCmdListener{
        String exeCommand(CommandObject ci);
        ArrayList<CommandObject> getNewCommandList();
    }

    public void setApplication(DidOptionEnv opt, Context context, onExecuteCmdListener mListener)
    {
        this.didOpt = opt;
        this.context = context;
        this.mExecuteCommandListener = mListener;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        List<CommandObject> delCommandList = new ArrayList<CommandObject>();
        isRun = true;
        newcommandList = mExecuteCommandListener.getNewCommandList();
        if (newcommandList.size() > 0) {
            int size = newcommandList.size();
            for (int i = 0; i < size; i++) {
                commandList.add(newcommandList.get(0));
                newcommandList.remove(0);
            }
        }
        mExecuteCommandListener.getNewCommandList().clear();
        //Log.d(TAG, "CommandAsynTask() commandList.SIZE=" + commandList.size()+" new commandlist="+mExecuteCommandListener.getNewCommandList().size());
        if (commandList.size() > 0) {
            for (CommandObject command : commandList) {
                if(mExecuteCommandListener==null) {
                    Log.d(TAG, "DidCmdAsyncTask() mExecuteCommandListener is null!!!");
                    return null;
                }
                String result = mExecuteCommandListener.exeCommand(command);
                String reqUrl = ServerReqUrl.getServerDidReportUrl(didOpt, context);
                command.result = result;
                URI url = null;
                //Log.d(TAG, "DidCmdAsyncTask() result="+result+" commandList.size()="+commandList.size());
                if((result!=null)&&(!result.isEmpty()))
                {
                    //if(result.equalsIgnoreCase("Y"))
                    {
                        url = URI.create(reqUrl + "&" + "completeY=" + command.rcCommandid + "&result=" + command.result);
                        result = NetworkUtil.sendHttpget(url);
                        //Log.d(TAG, "DidCmdAsyncTask() url="+url+" result="+result+" commandList.size()="+commandList.size());
                        if ((result != null) && (!result.isEmpty())) {
                            if (result.equalsIgnoreCase("Y"))
                                delCommandList.add(command);
                        }
                    }
                }
                break;
            }
        }
        for (CommandObject command : delCommandList) {
            Utils.LOG("--------------------");
            Utils.LOG("Delete command id" + " #: " + command.rcCommandid);
            Utils.LOG(context.getString(R.string.Log_CmdCommandName) + ": " + command.command);
            Utils.LOG(context.getString(R.string.Log_CmdExecTime) + ": " + command.execTime);
            Utils.LOG(context.getString(R.string.Log_CmdParameter) + ": " + command.prameter);
            Utils.LOG("--------------------");

            commandList.remove(command);
        }
        return null;
    }
}
