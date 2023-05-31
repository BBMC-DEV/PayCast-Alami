package kr.co.bbmc.selforderutil;

import android.util.Log;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;

public class FTPUtil {

    public static FTPClient ftpClient = null;

    public boolean DownloadContents(DownFileInfo fileInfo) {
//        this.ftpClient = new FTPClient();
//        connect();
//        login(SERVERID, SERVERPW);
        cd(fileInfo.LocalFolderName());//input u r directory
        FTPFile[] files = list();
        if (files == null) {
            return false;
        }
        Log.d("FTPUtill", "DownloadContents() files.length="+files.length);
        ArrayList<String> ImageIds_tmp = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            long size = files[i].getSize();
            extension = extension.toUpperCase();
            Log.d("FTPUtill", "fileName = "+fileName);
            if (size > 0) {
                for (int j = 0; j < size; j++) {
                    if (fileInfo.fileName.equalsIgnoreCase(fileName.substring(0, fileName.indexOf(".")))) {
                        StringBuffer furl = new StringBuffer(fileInfo.LocalFolderName());
                        furl.append(fileName);
                        ImageIds_tmp.add(furl.toString());
                        get(fileName, fileName);
                    }
                }
            }
        }
//        logout();
//        disconnect();
        return true;
    }

    public boolean login(String ftpServer, int port, String user, String password) {
        try {
            this.connect(ftpServer, port );
            return this.ftpClient.login(user, password);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public boolean logout() {
        try {
            return this.ftpClient.logout();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public void connect(String ftpServer, int port) {
        if(this.ftpClient==null)
            this.ftpClient = new FTPClient();
        Log.d("FTPUtill", "connect() ftpServer="+ftpServer+" port="+port);
//        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        try {
            this.ftpClient.connect(InetAddress.getByName(ftpServer), port);
            int reply;
            reply = this.ftpClient.getReplyCode();
            Log.d("FTPUtill", "connect() reply="+reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                Log.d("FTPUtill", "disconnect() ");
            }
        } catch (IOException ioe) {
            Log.d("FTPUtill", "connect() error ioe="+ioe.toString());
            if (this.ftpClient.isConnected()) {
                try {
                    this.ftpClient.disconnect();
                } catch (IOException f) {
                    ;
                }
            }
        }
    }

    public FTPFile[] list() {
        FTPFile[] files = null;
        try {
            files = this.ftpClient.listFiles();
            return files;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public File get(String source, String target) {
        OutputStream output = null;
        try {
            StringBuffer furl = new StringBuffer("/sdcard/xxx/");
            File path = new File(furl.toString());
            if (!path.isDirectory()) {
                path.mkdirs();
            }

            furl.append(target);
            File local = new File(furl.toString());
            if (local.isFile()) {
                return null;
            }
            output = new FileOutputStream(local);
        } catch (FileNotFoundException fnfe) {
            ;
        }
        File file = new File(source);
        try {
            if (this.ftpClient.retrieveFile(source, output)) {
                return file;
            }
        } catch (IOException ioe) {
            ;
        }
        return null;
    }

    public void cd(String path) {
        try {
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.changeWorkingDirectory(path);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.ftpClient.disconnect();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public boolean UploadContents(String folder, String path) throws IOException {

        boolean success = false;

        String sendFilePath = path;
        //String sendFilePath = "/storage/emulated/0/BBMC/signcast_shot_20180509_114720.jpg";


        File file;
        file = new File(sendFilePath);
        ftpClient.changeWorkingDirectory(folder);
        FTPFile flist[] = ftpClient.listFiles();

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);


        if (file.exists())
        {
            Log.d("FTP", "file exist " + file.getName());
            FileInputStream ifile = new FileInputStream(file);
            ftpClient.rest(file.getName());  // ftp에 해당 파일이있다면 이어쓰기
            ftpClient.appendFile(file.getName(), ifile); // ftp 해당 파일이 없다면 새로쓰기
            //ftpClient.rename(file.getName(), "profile.jpg"); //이름 변경
            //Log.e("FTP", "file rename " + file.getName());
            success = true;
        }

        return success;
    }
}
