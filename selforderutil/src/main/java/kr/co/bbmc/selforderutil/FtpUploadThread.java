package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpUploadThread extends Thread {
    private static final String TAG = "FtpUploadThread";
    private boolean LOG = false;

    public boolean stopFlag = false;
    public boolean isUploading = false;
    private Context context;
    private StbOptionEnv stbOpt;
    public List<String> uploadList = null;

    public void setApplication(Context context, StbOptionEnv stbOpt, List<String> upList )
    {
        this.context = context;
        this.stbOpt = stbOpt;
        this.uploadList = upList;
    }
    @Override
    public void run() {
        FTPUtil ftpUtill = new FTPUtil();
//        mAgentExterVarApp = (AgentExternalVarApp) getApplication();

        isUploading = true;

        if (LOG)
            Log.d(TAG, "FtpUploadThread() 1");
        FTPUtil.ftpClient = new FTPClient();
        List<String> uploadCompletList = new ArrayList<>();
        ftpUtill.connect(stbOpt.ftpHost, stbOpt.ftpPort);
        ftpUtill.login(stbOpt.ftpHost, stbOpt.ftpPort, stbOpt.ftpUser, stbOpt.ftpPassword);
        int listsize = uploadList.size();
        for (int i = 0; i < listsize; i++) {
            String uploadName = uploadList.get(i);
            if (stopFlag == true)
                return;
            int index = uploadName.lastIndexOf("/");
            String fileName = uploadName.substring(index + 1);

            if (LOG)
                Log.d(TAG, "FtpUploadThread() fileName=" + fileName);
            String folder = null;

            String[] arr = fileName.split("_");
            if (arr[0].equals(stbOpt.serverUkid)) {
                switch (arr[1]) {
                    case "shot":
                        folder = "shots";
                        break;
                    case "debug":
                        folder = "debugs";
                        break;
                    case "log":
                        folder = "logs";
                        break;
                    case "track":
                        folder = "tracks";
                        break;
                }
                if (LOG)
                    Log.d(TAG, "folder name=" + folder + " fileName=" + fileName);
                ftpUtill.cd("upload");
                ftpUtill.cd(folder);
                try {
                    if (ftpUtill.UploadContents(folder, uploadName)) {
                        Log.d(TAG, "upload true");
                        FTPFile flist[] = ftpUtill.list();
                        for (FTPFile f : flist) {
                            if (f.getName().equals(fileName)) {
                                if (LOG)
                                    Log.d(TAG, "FILE UPLOAD OK!!!");
                                uploadCompletList.add(uploadName);
                            } else {
                                if (LOG)
                                    Log.d(TAG, "FILE UPLOAD list=" + f.getName());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String l : uploadCompletList) {
            uploadList.remove(l);
//for test 임시 막음                FileUtils.removeFile(l);
        }
        uploadCompletList.clear();
        ftpUtill.logout();
        ftpUtill.disconnect();
        isUploading = false;
    }

    @Override
    public synchronized void start() {
        isUploading = true;
        super.start();
    }
}
