package kr.co.bbmc.selforderutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";
    private static boolean LOG = true;

    public static String HttpResponseString(String url, String param, PropUtil prop) {

        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)

        if (Boolean.valueOf(prop.serverSSLEnabled)) {
            url = url.replace("http://", "https://");
        }

        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=UTF-8");
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        String responseString = "";
        try {
            //add data
            if(param!= null) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                String[] nameValue = param.split("&", -1);
                if ((nameValue != null) && (nameValue.length > 0)) {
                    for (String n : nameValue) {
                        String[] value = n.split("=", -1);
                        nameValuePairs.add(new BasicNameValuePair(value[0], value[1]));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("data", param));
                }

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
            //execute http post
            HttpResponse response = httpClient.execute(httpPost);
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    public static String HttpResponseString(String url, String param, Context c, boolean sslFlag) {
        Log.d(TAG, "URL 체크 : " + url);
        Log.d(TAG, "PARAM 체크 : " + param);
        Log.d(TAG, "ssFlag 체크 : " + sslFlag);
        if(sslFlag) {
            // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
            String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);
            if ((ssl!=null) && (Boolean.valueOf(ssl))){
                url = url.replace("http://", "https://");
            }
        }

        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=UTF-8");
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        try {
            int result = pingHost(url);
//            int result = pingHost("auth.signcast.co.kr");
            Log.e(TAG, "PING url ="+url+" RESULT="+result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String responseString = "";
        try {
            //add data
            if(param!= null) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                String[] nameValue = param.split("&", -1);
                if ((nameValue != null) && (nameValue.length > 0)) {
                    for (String n : nameValue) {
                        String[] value = n.split("=", -1);
                        nameValuePairs.add(new BasicNameValuePair(value[0], value[1]));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("data", param));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8") );
            }

            //execute http post
            HttpResponse response = httpClient.execute(httpPost);
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }
    /**
     * Ping a host and return an int value of 0 or 1 or 2 0=success, 1=fail, 2=error
     * <p>
     * Does not work in Android emulator and also delay by '1' second if host not pingable
     * In the Android emulator only ping to 127.0.0.1 works
     *
     * @param @String host in dotted IP address format
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static int pingHost(String host) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        return exit;
    }
    public static class TcpClientRec {

        private static String server_ip;
        private static int server_port;
        // message to send to the server
        private String mServerMessage;
        // sends message received notifications
        private OnMessageReceived mMessageListener = null;
        // while this is true, the server will continue running
        public boolean mRun = false;
        // used to send messages
//        private PrintWriter mBufferOut;
        private OutputStream mBufferOut;
        // used to read messages from the server
        private BufferedReader mBufferIn;
        private ArrayList<Thread> mThreadList = new ArrayList<>();

        /**
         * Constructor of the class. OnMessagedReceived listens for the messages received from server
         */
        public TcpClientRec(OnMessageReceived listener, String ip, int port) {
            mMessageListener = listener;
            server_ip = ip;
            server_port = port;
            mThreadList = new ArrayList<>();
        }

        /**
         * Sends the message entered by client to the server
         *
         * @param message text entered by client
         */
        public void sendMessage(final String message) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mBufferOut != null) {
                        Log.d(TAG, "Sending: " + message);
//                        mBufferOut.println(message + "\r\n");
                        byte[] buf = new byte[1024]; //choose your buffer size if you need other than 1024
                        buf = message.getBytes();//.substring(0, message.length()).toCharArray();
                        try {
                            mBufferOut.write(buf);
                            mBufferOut.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.setName(message);
            thread.start();

            mThreadList.add(thread);
/*
            if (mBufferOut != null) {
                Log.d(TAG, "Sending: " + message);
//                        mBufferOut.println(message + "\r\n");
                byte[] buf = new byte[1024]; //choose your buffer size if you need other than 1024
                buf = message.getBytes();//.substring(0, message.length()).toCharArray();
                try {
                    mBufferOut.write(buf);
                    mBufferOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
*/
        }


        public void sendFile(final String fileName) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mBufferOut != null) {
                        byte[] buffer = new byte[1024];
                        int readBytes = 0;

                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(fileName);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return;
                        }
                        while(true)
                        {
                            try {
                                readBytes = fis.read(buffer);
                                if(readBytes <= 0)
                                    break;
                                else
                                {
                                    try {
                                        mBufferOut.write(buffer, 0, readBytes);
                                        mBufferOut.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
/*
                if (mBufferOut != null) {
                    byte[] buffer = new byte[1024];
                    int readBytes = 0;

                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    while(true)
                    {
                        try {
                            readBytes = fis.read(buffer);
                            if(readBytes <= 0)
                                break;
                            else
                            {
                                try {
                                    mBufferOut.write(buffer, 0, readBytes);
                                    mBufferOut.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
*/
        }

        /**
         * Close the connection and release the members
         */
        public void stopClient() throws IOException {
            if(mBufferOut!=null) {
                mBufferOut.flush();
                mBufferOut.close();
            }

            //mMessageListener = null;
            mBufferIn = null;
            mBufferOut = null;
            mServerMessage = null;
            mRun = false;
        }

        public void run() {

            mRun = true;

            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(server_ip);

                Log.e("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                Socket socket = new Socket(serverAddr, server_port);

                try {

                    //sends the message to the server
                    mBufferOut = socket.getOutputStream();
//                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//                    out =soc.getOutputStream();


                    //receives the message which the server sends back
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    int charsRead = 0;
                    char[] buffer = new char[1024]; //choose your buffer size if you need other than 1024

                    //in this while the client listens for the messages sent by the server
                    while (mRun) {
                        charsRead = mBufferIn.read(buffer);
                        if(charsRead>0) {
                            mServerMessage = new String(buffer).substring(0, charsRead);

                            if (mServerMessage != null && mMessageListener != null) {
                                //call the method messageReceived from MyActivity class
                                mMessageListener.messageReceived(mServerMessage);
                            }
                        }

                    }

                    Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                } finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    socket.close();
                }

            } catch (Exception e) {

                Log.e("TCP", "C: Error", e);

            }

        }

        //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
        //class at on asynckTask doInBackground
        public interface OnMessageReceived {
            public void messageReceived(String message);
        }

    }
    public static class UDP_Client
    {

        private InetAddress IPAddress = null;
        private String message = "Hello Android!" ;
        private AsyncTask<Void, Void, Void> async_cient;
        public String Message;


        @SuppressLint("NewApi")
        public void NachrichtSenden()
        {
            async_cient = new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    DatagramSocket ds = null;

                    try
                    {
                        byte[] ipAddr = new byte[]{ (byte) 192, (byte) 168,43, (byte) 157};
                        InetAddress addr = InetAddress.getByAddress(ipAddr);
                        ds = new DatagramSocket(5000);
                        DatagramPacket dp;
                        dp = new DatagramPacket(Message.getBytes(), Message.getBytes().length, addr, 50000);
                        ds.setBroadcast(true);
                        ds.send(dp);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (ds != null)
                        {
                            ds.close();
                        }
                    }
                    return null;
                }

                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);
                }
            };

            if (Build.VERSION.SDK_INT >= 11) async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else async_cient.execute();
        }
    }
    public static String UrlEncode(String str)
    {
        if (str == null)
        {
            return "";
        }
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int count = bytes.length;
        int offset = 0;

        int num = 0;
        int num2 = 0;
        for (int i = 0; i < count; i++)
        {
            char ch = (char)bytes[offset + i];
            if (ch == ' ')
            {
                num++;
            }
            else if (!isSafe(ch))
            {
                num2++;
            }
        }

        byte[] buffer = new byte[count + (num2 * 2)];
        int num4 = 0;
        for (int j = 0; j < count; j++)
        {
            byte num6 = bytes[offset + j];
            char ch2 = (char)num6;
            if (isSafe(ch2))
            {
                buffer[num4++] = num6;
            }
            else if (ch2 == ' ')
            {
                buffer[num4++] = 0x2b;
            }
            else
            {
                buffer[num4++] = 0x25;
                buffer[num4++] = (byte)IntToHex((num6 >> 4) & 15);
                buffer[num4++] = (byte)IntToHex(num6 & 15);
            }
        }
        String ret = null;
        try {
            ret = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //return Encoding.ASCII.GetString(buffer, 0, buffer.length);
        return ret;
    }
    /*
        public static string UrlDecode(string str)
        {
            if (null == str)
                return null;

            if (str.IndexOf('%') == -1 && str.IndexOf('+') == -1)
                return str;

            Encoding e = Encoding.UTF8;

            long len = str.Length;
            var bytes = new List<byte>();
            int xchar;
            char ch;

            for (int i = 0; i < len; i++)
            {
                ch = str[i];
                if (ch == '%' && i + 2 < len && str[i + 1] != '%')
                {
                    if (str[i + 1] == 'u' && i + 5 < len)
                    {
                        // unicode hex sequence
                        xchar = GetChar(str, i + 2, 4);
                        if (xchar != -1)
                        {
                            WriteCharBytes(bytes, (char)xchar, e);
                            i += 5;
                        }
                        else
                            WriteCharBytes(bytes, '%', e);
                    }
                    else if ((xchar = GetChar(str, i + 1, 2)) != -1)
                    {
                        WriteCharBytes(bytes, (char)xchar, e);
                        i += 2;
                    }
                    else
                    {
                        WriteCharBytes(bytes, '%', e);
                    }
                    continue;
                }

                if (ch == '+')
                    WriteCharBytes(bytes, ' ', e);
                else
                    WriteCharBytes(bytes, ch, e);
            }

            byte[] buf = bytes.ToArray();
            bytes = null;
            return e.GetString(buf);
        }
        private static void WriteCharBytes(IList buf, char ch, Encoding e)
        {
            if (ch > 255)
            {
                for (byte b : e.GetBytes(new char[] { ch }))
                buf.Add(b);
            }
            else
                buf.Add((byte)ch);
        }
    */
    private static char IntToHex(int n)
    {
        if (n <= 9)
        {
            return (char)(n + 0x30);
        }
        return (char)((n - 10) + 0x61);
    }
    private static boolean isSafe(char ch)
    {
        if ((((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) || ((ch >= '0') && (ch <= '9')))
        {
            return true;
        }
        switch (ch)
        {
            case '\'':
            case '(':
            case ')':
            case '*':
            case '-':
            case '.':
            case '_':
            case '!':
                return true;
        }
        return false;
    }
    public static boolean isConnected(Context c)
    {
        ConnectivityManager manager =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo etherNet = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        if((wifi==null)&&(etherNet==null)&&(wimax==null))
            return false;

        if(!wifi.isConnected()&&!etherNet.isConnected())
        {
            return false;
        }

        return true;
    }
    public static class TcpClientSend {

        private static String server_ip;
        private static int server_port;
        // message to send to the server
        private String mServerMessage;
        // sends message received notifications
        private OnMessageReceived mMessageListener = null;
        // while this is true, the server will continue running
        private boolean mRun = false;
        // used to send messages
        private PrintWriter mBufferOut;
        // used to read messages from the server
        private BufferedReader mBufferIn;
        private String mTransferFile = null;
        private String mCommand = null;

        /**
         * Constructor of the class. OnMessagedReceived listens for the messages received from server
         */
        public TcpClientSend(OnMessageReceived listener, String ip, int port) {
            mMessageListener = listener;
            server_ip = ip;
            server_port = port;
        }

        public TcpClientSend(OnMessageReceived listener, String ip, int port, String cmd, String srcFile) {
            mMessageListener = listener;
            server_ip = ip;
            server_port = port;
            mCommand = cmd;
            mTransferFile = srcFile;
        }


        /**
         * Sends the message entered by client to the server
         *
         * @param message text entered by client
         */
        public void sendMessage(final String message) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mBufferOut != null) {
                        Log.d(TAG, "Sending: " + message);
                        mBufferOut.println(message + "\r\n");
                        mBufferOut.flush();
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }

        /**
         * Close the connection and release the members
         */
        public void stopClient() {

            mRun = false;

            if (mBufferOut != null) {
                mBufferOut.flush();
                mBufferOut.close();
            }

            mMessageListener = null;
            mBufferIn = null;
            mBufferOut = null;
            mServerMessage = null;
        }

        public void run() {

            mRun = true;

            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(server_ip);

                Log.d("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                Socket socket = new Socket(serverAddr, server_port);

                File trFile = new File(mTransferFile);
                if((trFile==null)|| !trFile.exists() || !trFile.isFile())
                {
                    Utils.LOG("Capture file is not exists");
                    Utils.LOG("");
                    return;
                }

                try {
                    FileInputStream fis = new FileInputStream(trFile);

                    //sends the message to the server
                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    //receives the message which the server sends back
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    int charsRead = 0;
                    char[] inputbuffer = new char[1024]; //choose your buffer size if you need other than 1024
                    byte[] outbuffer = new byte[1024]; //choose your buffer size if you need other than 1024

                    int read;
                    while(mRun)
                    {
                        charsRead = mBufferIn.read(inputbuffer);
                        mServerMessage =new String(inputbuffer).substring(0, charsRead);

                        if (mServerMessage != null && mMessageListener != null) {
                            //call the method messageReceived from MyActivity class
                            mMessageListener.messageReceived(mServerMessage);
                        }
/*
                        while ((read = fis.read(outbuffer, 0, 1024)) != -1) {
                            mBufferOut.write(String.valueOf(outbuffer), 0, read);
                            mBufferOut.flush();
                        }
*/
                    }

                    Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                } finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    socket.close();
                }

            } catch (Exception e) {

                Log.e("TCP", "C: Error", e);

            }

        }

        //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
        //class at on asynckTask doInBackground
        public interface OnMessageReceived {
            public void messageReceived(String message);
        }
    }
    public boolean getNetworkInfoFromServer(Context context, PropUtil propUtil, StbOptionEnv stbOpt,  List<CommandObject> commandList, List<DownFileInfo> downloadList, List<CommandObject> newcommandList) {
        // start timer...
        String reqUrl = null;
        String macAddr  = Utils.getMacAddrOnConnect(context);
        String queryString = null;
        XmlOptionParser xmlParser = new XmlOptionParser();

        if (stbOpt.serverPort == 80) {
            reqUrl = String.valueOf("http://" + stbOpt.serverHost + "/info/stb?");
            queryString = String.valueOf("macAddress=" + macAddr + "&site=" + stbOpt.serverUkid);
        } else {
            reqUrl = String.valueOf("http://" + stbOpt.serverHost + ":" + stbOpt.serverPort + "/info/stb?");
            queryString = String.valueOf("macAddress=" + macAddr + "&site=" +stbOpt.serverUkid);
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(context.getString(R.string.serverSSLEnabled), context);
        Log.d(TAG, "startTimerTask() ssl=" + ssl);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
        Log.d(TAG, "reqUrl=" + reqUrl);
        Log.d(TAG, "queryString=" + queryString);

        String encodedQueryString = null;
        try {
            encodedQueryString = URLEncoder.encode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        URI url = null;
        try {
            url = new URI(reqUrl + queryString);
//                url = new URI(reqUrl+encodedQueryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        boolean result = false;
        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.d(TAG, "getNetworkInfoFromServer() response="+responseString);
            if((responseString!=null)&&(!responseString.isEmpty())) {
                result =xmlParser.ParseXML(responseString, context, propUtil, stbOpt, commandList, downloadList, newcommandList);
            }
        } catch (IOException e) {
            Log.e(TAG, "NETWORK ERROR!!! 1");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String HttpKioskResponseString(String url, KioskPayDataInfo kioskPayDataInfo) throws JSONException {

        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
/*
        if (Boolean.valueOf(prop.serverSSLEnabled)) {
            url = url.replace("http://", "https://");

        }
*/
        HttpClient httpClient = new DefaultHttpClient();

        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/json");

        String responseString = "";
        ArrayList<NameValuePair> postParameters;

        postParameters = new ArrayList<NameValuePair>();

        JSONArray json1 = new JSONArray();
        JSONObject k = new JSONObject();


        k.put("tid", kioskPayDataInfo.tid);  //거래고유번호
        k.put("mid", kioskPayDataInfo.mid);  //가맹점 번호
        k.put("fnCd", kioskPayDataInfo.fnCd);    //발급사 코드
        k.put("fnName", kioskPayDataInfo.fnName);   //발급사명
        k.put("fnCd1", kioskPayDataInfo.fnCd1);    //매입사 코드
        k.put("fnName1", kioskPayDataInfo.fnName1);   //매입사명


        k.put("storeIdpay", kioskPayDataInfo.storeIdpay);
        k.put("totalindex", kioskPayDataInfo.totalindex);
        k.put("goodsAmt", kioskPayDataInfo.goodsAmt);
        k.put("goodsTotal", kioskPayDataInfo.goodsTotal);
        k.put("orderNumber", kioskPayDataInfo.orderNumber);
        k.put("orderDate", kioskPayDataInfo.orderDate);
        k.put("AuthCode", kioskPayDataInfo.authCode);
        //k.put("fnName", kioskPayDataInfo.fnName);
        k.put("CATID", kioskPayDataInfo.catID);
//        json1.put(k);
//        postParameters.add(new BasicNameValuePair("", json1.toString()));

        JSONArray json = new JSONArray();
        for(int i = 0; i<kioskPayDataInfo.orderMenuList.size(); i++) {
            JSONObject j = new JSONObject();
            KioskOrderMenuItem menu = kioskPayDataInfo.orderMenuList.get(i);
            try {
                j.put("productID", menu.productID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                j.put("productName", menu.productName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                j.put("orderCount", menu.orderCount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int price = Integer.valueOf(menu.orderPrice);
            int count = Integer.valueOf(menu.orderCount);
            if((price!=0)&&(count!=0)) {
                try {
                    j.put("orderPrice", String.valueOf(price / count));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            json.put(j);
        }
        k.put("orderMenu", json);
        json1.put(k);


        StringEntity postingString = null;//gson.tojson() converts your pojo to json
        try {
            postingString = new StringEntity(json1.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "HttpKioskResponseString() postStr="+postingString);

        httpPost.setEntity(postingString);

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            try {
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseString;
    }
    public static String onGetOrderPrintStringFrServer(String urlString)
    {
        String responseString = "";
        HttpClient httpClient = new DefaultHttpClient();

        URI url = null;
        try {
            url = new URI(urlString);
//                url = new URI(reqUrl+encodedQueryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpGet httpget = new HttpGet(url);
        httpget.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpget);
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.d(TAG, "MonitorAsynTask() resp="+responseString);
//                ParseXML(responseString);
/*
            String rightStb = Utils.getElementAttrValue(responseString, "data", "exits");
            if (rightStb.isEmpty()) {

            }
*/
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "MonitorAsynTask() ERROR e="+e);
        }
        return responseString;
    }
    public static String onGetStoreChgSync(String urlString)
    {
        String responseString = "";
        HttpClient httpClient = new DefaultHttpClient();

        URI url = null;
        try {
            url = new URI(urlString);
//                url = new URI(reqUrl+encodedQueryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);


        //Log.d(TAG, "1 onGetStoreChgSync() urlString="+urlString);
        HttpGet httpget = new HttpGet(urlString);
        httpget.setURI(url);
            //Log.d(TAG, "2 onGetStoreChgSync() urlString="+urlString);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response!=null) {
            try {
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(LOG) {
                Log.d(TAG, "1 onGetStoreChgSync() response.getAllHeaders()=" + response.getAllHeaders());
                Log.d(TAG, "2 onGetStoreChgSync() resp=" + responseString);
            }
        }
        else {
            if(LOG)
                Log.d(TAG, "3 onGetStoreChgSync() resp=" + responseString);
        }

        return responseString;
    }
    public static String getStoreInfoChgFromServer(String rcCommandid, Context c, StbOptionEnv stb) {
        String reqUrl = getServerStoreInfoChgUrl(stb, c);
        String resStr = "";
        if(LOG) {
            Log.d(TAG, "getStoreInfoChgFromServer() reqUrl:" + reqUrl);
        }

        String result = null;
        HttpURLConnection urlConnection = null;
        int lastResponseCode = -1;
        String lastContentType = null;
        BufferedReader buffRecv;

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpPost = new HttpGet(reqUrl);
        httpPost.setURI(URI.create(reqUrl));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString ="";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String getServerStoreInfoChgUrl(StbOptionEnv stbOpt, Context c) {
        String serverUrl = "";
        if (stbOpt.serverPort == 80) {
            serverUrl = String.format("http://" + stbOpt.serverHost + "/info/store");
        } else {
            serverUrl = String.format("http://" + stbOpt.serverHost + ":" + stbOpt.serverPort + "/info/store");
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            serverUrl = serverUrl.replace("http://", "https://");
        }
        //-
        String encodedQueryString = String.valueOf("storeId=" + stbOpt.storeId);
        return String.valueOf(serverUrl + "?" + encodedQueryString);
    }
    public static String getNetworkInfoFromServer(Context c, String serverHost, int serverPort, String deviceId, String serverUkid) {
        // start timer...
        String reqUrl = null;
        String queryString = null;

        if (serverPort == 80) {
            reqUrl = String.valueOf("http://" + serverHost + "/info/stb?");
            queryString = String.valueOf("deviceId=" + deviceId + "&site=" + serverUkid);
        } else {
            reqUrl = String.valueOf("http://" + serverHost + ":" + serverPort + "/info/stb?");
            queryString = String.valueOf("deviceId=" + deviceId + "&site=" + serverUkid);
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
            if(LOG) {
                Log.d(TAG, "reqUrl=" + reqUrl);
                Log.d(TAG, "queryString=" + queryString);
            }


        URI url = null;
        try {
            url = new URI(reqUrl + queryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpPost = new HttpGet(url);
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "getNetworkInfoFromServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException e) {
            if(LOG) {
                Log.e(TAG, "NETWORK ERROR!!! 2");
            }
            e.printStackTrace();
            return null;
        }
    }

    public static String getNetworkInfoFromServer(Context c, StbOptionEnv stbOpt) {
        // start timer...
        String reqUrl = null;
        String queryString = null;

        if (stbOpt.serverPort == 80) {
            reqUrl = String.valueOf("http://" + stbOpt.serverHost + "/info/stb?");
            queryString = String.valueOf("deviceId=" + stbOpt.deviceId + "&site=" + stbOpt.serverUkid);
        } else {
            reqUrl = String.valueOf("http://" + stbOpt.serverHost + ":" + stbOpt.serverPort + "/info/stb?");
            queryString = String.valueOf("deviceId=" + stbOpt.deviceId + "&site=" + stbOpt.serverUkid);
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
        if(LOG) {
            Log.d(TAG, "reqUrl=" + reqUrl);
            Log.d(TAG, "queryString=" + queryString);
        }


        URI url = null;
        try {
            url = new URI(reqUrl + queryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "getNetworkInfoFromServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException e) {
            Log.e(TAG, "NETWORK ERROR!!! 2");
            e.printStackTrace();
            return null;
        }
    }
    public static String sendFCMTokenToAuthServer(String reqUrl, String queryString) {

        URI url = null;
        try {
            url = new URI(reqUrl + "?"+queryString);
//                url = new URI(reqUrl+encodedQueryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();

        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);
        if(LOG) {
            Log.e(TAG, "sendFCMTokenToAuthServer()reqUrl=" + reqUrl + " queryString=" + queryString);
        }
        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "sendFCMTokenToAuthServer() response=" + responseString);
            }
            return responseString;
//            ParseXML(responseString);
        } catch (IOException e) {
            if(LOG) {
                Log.e(TAG, "NETWORK ERROR!!! 3 e=" + e.toString());
            }
            e.printStackTrace();
            return null;
        }
    }
    public static String getCookalarmListFromServer(Context c, DidOptionEnv didOpt) {
        // start timer...
        String reqUrl = null;
        String queryString = null;

        if (didOpt.serverPort == 80) {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + "/cookalarmlist?");
            queryString = String.valueOf("storeId=" + didOpt.storeId+"&deviceId=" + didOpt.deviceId );
        } else {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + ":" + didOpt.serverPort + "/cookalarmlist?");
            queryString = String.valueOf("storeId=" +didOpt.storeId+"&deviceId=" + didOpt.deviceId );
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
        if(LOG) {
            Log.d(TAG, "reqUrl=" + reqUrl);
            Log.d(TAG, "queryString=" + queryString);
        }


        URI url = null;
        try {
            url = new URI(reqUrl + queryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "getCookalarmListFromServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException e) {
            if(LOG) {
                Log.e(TAG, "NETWORK ERROR!!! 2");
            }
            e.printStackTrace();
            return null;
        }
    }
    public static String reportCookCompleteListToServer(Context c, DidOptionEnv didOpt, String postStringYes) {
        String reqUrl = null;
        String queryString = null;

        if (didOpt.serverPort == 80) {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + "/cookdispcom?");
            queryString = String.valueOf("storeId=" + didOpt.storeId+"&");
        } else {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + ":" + didOpt.serverPort + "/cookdispcom?");
            queryString = String.valueOf("storeId=" +didOpt.storeId+"&");
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
        String param = "";
        if((postStringYes!=null)&&(!postStringYes.isEmpty())) {
            try {
                param = "completeY=" + URLEncoder.encode(postStringYes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(LOG) {
            Log.d(TAG, "reportCookCompleteListToServer() postStringYes=" + postStringYes);
            Log.d(TAG, "reportCookCompleteListToServer() reqUrl=" + reqUrl);
            Log.d(TAG, "reportCookCompleteListToServer() queryString=" + queryString + param);
        }


        URI url = null;
        try {
            url = new URI(reqUrl + queryString+param);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "getCookCompleteListFromServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException e) {
            if(LOG) {
                Log.e(TAG, "NETWORK ERROR!!! 2");
            }
            e.printStackTrace();
            return null;
        }
    }

    public static String sendHttpget(URI url) {
        HttpClient httpClient = new DefaultHttpClient();

        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);
        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        if(LOG) {
            Log.d(TAG, "명령실행보고  url=" + String.valueOf(url));
            Log.e(TAG, "sendHttpget()");
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.e(TAG, "실행보고 response=" + responseString);
            }
            return "Y";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "N";
    }
    public static String getCookCompleteListFromServer(Context c, DidOptionEnv didOpt) {
        // start timer...
        String reqUrl = null;
        String queryString = null;

        if (didOpt.serverPort == 80) {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + "/cookcomlist?");
            queryString = String.valueOf("storeId=" + didOpt.storeId+"&deviceId=" + didOpt.deviceId );
        } else {
            reqUrl = String.valueOf("http://" + didOpt.serverHost + ":" + didOpt.serverPort + "/cookcomlist?");
            queryString = String.valueOf("storeId=" +didOpt.storeId+"&deviceId=" + didOpt.deviceId );
        }
        // jason:serverssl: 서버 https 프로토콜 옵션(2017/10/12)
        String ssl = PropUtil.configValue(c.getString(R.string.serverSSLEnabled), c);

        if ((ssl!=null) && (Boolean.valueOf(ssl))){
            reqUrl = reqUrl.replace("http://", "https://");
        }
        //-
        if(LOG) {
            Log.d(TAG, "reqUrl=" + reqUrl);
            Log.d(TAG, "queryString=" + queryString);
        }


        URI url = null;
        try {
            url = new URI(reqUrl + queryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);

        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if(response!=null)
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if(LOG) {
                Log.d(TAG, "getCookCompleteListFromServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException e) {
            if(LOG) {
                Log.e(TAG, "NETWORK ERROR!!! 2");
            }
            e.printStackTrace();
            return null;
        }
    }
    public static String getCookOrderCountServer(String reqUrl, String queryString) {
        URI url = null;

        try {
            url = new URI(reqUrl + "?" + queryString);
        } catch (URISyntaxException var8) {
            var8.printStackTrace();
        }

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.expect-continue", false);
        httpClient.getParams().setParameter("http.connection.timeout", 3000);
        httpClient.getParams().setParameter("http.socket.timeout", 3000);
        HttpGet httpPost = new HttpGet(url);
        httpPost.setURI(url);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = "";
            if (response != null) {
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

            if(LOG) {
                Log.d("NetworkUtil", "getCookOrderCountServer() response=" + responseString);
            }
            return responseString;
        } catch (IOException var7) {
            if(LOG) {
                Log.e("NetworkUtil", "NETWORK ERROR!!!");
            }
            var7.printStackTrace();
            return null;
        }
    }

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_ETHERNET = 3;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
            if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET)
                return TYPE_ETHERNET;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_ETHERNET) {
            status = "Ethernet data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    public static int getWifiRssi(Context c)
    {
        int conn = NetworkUtil.getConnectivityStatus(c);
        int rssi = 0xfff;
        if (conn == NetworkUtil.TYPE_WIFI) {
            WifiManager wm = (WifiManager) c.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            rssi = wifiInfo.getRssi();
        }
        return 0;

    }

}
