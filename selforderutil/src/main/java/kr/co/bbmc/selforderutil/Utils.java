package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.content.Context.WIFI_SERVICE;

public class Utils {
    private static final String TAG = "Utils";

    // 휴일 전원 오프 전 잔여 시간 정보 출력 초시간
    public int HolidayPowerOffInfoSeconds = 300;

    // 휴일 전원 오프 초시간(현재부터)
    public int HolidayPowerOffDurationSeconds = 300;

    // 정규 방송 시작 전 잔여 시간 정보 출력 초시간
    public int RegularShowBeginInfoSeconds = 180;

    // 정규 방송 종료 전 잔여 시간 정보 출력 초시간
    public int RegularShowEndInfoSeconds = 60;

    // 원격 제어 전 잔여 시간 정보 출력 초시간
    public static int UserRequestInfoSeconds = 180;




    // Debugging Log Queue
    private static List<String> dLogQ = new ArrayList<String>();
    // Debugging Log Queue
    private static List<String> dDebugQ = new ArrayList<String>();

    // jason:uploaddebuglog: 디버그 파일 업로드(2015/04/23)
    public static Date TempLogDt;
    //-
    public List<SlideShowLog> mTrackingLogs = new ArrayList<SlideShowLog>();
    // jason:touchlogoption: 플레이어 터치로그 옵션(2014/12/03)
    public List<SlideShowTouchLog> mTrackingTouchLogs = new ArrayList<SlideShowTouchLog>();
    private static List<TrackingContentItem> mTrackingContents = new ArrayList<TrackingContentItem>();
    private static List<SpecialTrackingItem> mSpecialTrackingContents = new ArrayList<SpecialTrackingItem>();
    private static boolean dLogLock = false;

    /*
        public static String getEthernetMacAddress() {
            String macAddress = "Not able to read";
            try {
                List<NetworkInterface> allNetworkInterfaces = Collections.list(NetworkInterface
                        .getNetworkInterfaces());
                for (NetworkInterface nif : allNetworkInterfaces) {
                    if (!nif.getName().equalsIgnoreCase("eth0"))
                        continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return macAddress;
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    macAddress = res1.toString();
                }
            } catch (Exception ex) {
                log(LogLevel.ERROR, "getEthernetMacAddress e :" + ex.getMessage());
                ex.printStackTrace();
            }
            return macAddress;
        }
    */
    public static String getMacAddrOnConnect(Context c)
    {
        String macAddr = null;
        String tempMacAddr = null;

        ConnectivityManager manager =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

//        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo etherNet = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if(wifi.isConnected())
        {
            WifiManager mng = (WifiManager) c.getSystemService(WIFI_SERVICE);

            WifiInfo info = mng.getConnectionInfo();
            tempMacAddr = info.getMacAddress();
        }
        else if(etherNet.isConnected()) {
//            Log.d(TAG, "etherNet..");
            tempMacAddr = getEthernetMacAddress();
        }
/*
        else if(mobile.isConnected()){
            Log.d(TAG, "mobile..");
        }
*/
        if(tempMacAddr == null)
        {
            tempMacAddr = getEthernetMacAddress();
        }
        macAddr = tempMacAddr.replace(":", "-");
        //macAddr = "F8-E6-1A-2B-45-A2";  //FOR TEST
        return macAddr;
    }

    public static List<String> getMacAddress(Context c)
    {
        List<String> mMacAddrList = new ArrayList<>();

        ConnectivityManager manager =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

/*
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo etherNet = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
*/
        //wifi mac addr
        {
            WifiManager mng = (WifiManager) c.getSystemService(WIFI_SERVICE);

            WifiInfo info = mng.getConnectionInfo();
            String tempMacAddr  = info.getMacAddress();
            String macAddr = null;
            if((tempMacAddr!=null)&&(!tempMacAddr.isEmpty())) {
                macAddr = tempMacAddr.replace(":", "-");
                mMacAddrList.add(macAddr);
            }
        }
        //etherNet mac addr
        {
//            Log.d(TAG, "etherNet..");
            String tempMacAddr = getEthernetMacAddress();
            String macAddr = null;
            if((tempMacAddr!=null)&&(!tempMacAddr.isEmpty())) {
                macAddr = tempMacAddr.replace(":", "-");
                mMacAddrList.add(macAddr);
            }
        }
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if ("wlan0" != null) {
                    if (!intf.getName().equalsIgnoreCase("wlan0"))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac!=null) {
                    StringBuilder buf = new StringBuilder();
                    for (int idx = 0; idx < mac.length; idx++)
                        buf.append(String.format("%02X:", mac[idx]));
                    if (buf.length() > 0)
                        buf.deleteCharAt(buf.length() - 1);
                    String macAddr = null;
                    macAddr = buf.toString().replace(":", "-");
                    mMacAddrList.add(macAddr);
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return mMacAddrList;
    }
    public static String getEthernetMacAddress() {
        NetworkInterface netf = null;
        try {
            netf = NetworkInterface.getByName("eth0");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] array = new byte[0];
        try {
            array = netf.getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder("");
        String str = "";
        for (int i = 0; i < array.length; i++) {
            int v = array[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append("-");
        }
        str = stringBuilder.substring(0, stringBuilder.length() - 1);
        return str;
    }


    public static void LOG(String log)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss ");
        Date now = new Date();

        String todayStr = sdf.format(now);

        if(dLogQ == null)
            dLogQ = new ArrayList<>();

        dLogQ.add(todayStr+log);
        Log.d("LOG", todayStr+log);
    }
    public static void flushLogQueue()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();


        String todayStr = sdf.format(now)+".log";

        if (dLogQ.size() > 0)
        {
            File file = new File(FileUtils.BBMC_LOG_DIR+todayStr);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                for(String data : dLogQ)
                    writer.append(data);
                writer.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dLogQ.clear();
        }
    }
    // jason:logdebuginfo: 플레이어 디버깅 로그 옵션(2014/02/20)
    public static boolean PlayerDebuggingMode = false;
    // jason:uploaddebuglog: 디버그 파일 업로드(2015/04/23)
    //public static Date TempLogDt;
    //-

    public static void DebugAuto(String logText)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (PlayerDebuggingMode /*|| DateTime.Now.CompareTo(TempLogDt) < 0*/)
        {
            debug(logText);
            flushDebugLogQueue();
        }
    }
    private static void debug(String logText)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss ");
        Date now = new Date();

        String logTime = sdf.format(now);
        if(dDebugQ==null)
            dDebugQ = new ArrayList<>();
        dDebugQ.add(logTime + logText);
        Log.d("DEBUG", logTime + logText);

    }
    public static void flushDebugLogQueue()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        List<String> delDebugQ = new ArrayList<String>();


        String todayStr = String.valueOf("Debug"+sdf.format(now)+".log");
        if (dDebugQ.size() > 0)
        {
            File file = new File(FileUtils.BBMC_LOG_DIR+todayStr);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                for(String data : dDebugQ) {
                    writer.append(data);
                    delDebugQ.add(data);
                }
                writer.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(String delData:delDebugQ) {
            dDebugQ.remove(delData);
        }
        delDebugQ.clear();
    }


    public static boolean MakeCommandFile(String command)
    {
        boolean ret = false;
        try {
            ret = MakeCommandFile(command, getCurrentDate());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean MakeCommandFile(String command, int mins)
    {
        long now = System.currentTimeMillis ( );
        long time = now+(mins*1000);
        Date date = new Date(time);
        try {
            return MakeCommandFile(command, date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean MakeCommandFile(String command, Date dt) throws IOException {
        if (command==null)
        {
            return false;
        }
        String[] arr = command.split("_");


        if (command.equals("CONNECT"))
        {
            String file_path = FileUtils.BBMC_DEFAULT_DIR+"/Command/CONNECT.xml";

            File file = new File(file_path);
            if(file!=null&&!file.exists()) {
                Log.i(TAG, "!file.exists");
                file.createNewFile();
            }
            return true;
        }
        else if (command.equals("SYNCCONTENT"))
        {
            String file_path = FileUtils.BBMC_DEFAULT_DIR+"/Command/SYNCCONTENT.xml";
            File file = new File(file_path);
            if(file!=null&&!file.exists()) {
                Log.i(TAG, "!file.exists");
                file.createNewFile();
            }
            return true;
        }

        if (command.equals("MN") || command.equals("MF") || command.equals("PF") ||
                command.equals("PP") || command.equals("RB") || command.equals("HT") ||
                command.equals("RP") || command.equals("SB")
                || command.equals("CR")  // jason:restartifnewer: 새로운 상시 스케줄 유무 체크(2015/08/31)
                || command.equals("TM")  // jason:timesignal: 시보 기능(2015/08/26)
                || command.equals("SD")  // jason:shutdown: 플레이어 프로그램 종료(2015/11/09)
                )
        {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyyMMddHHmm");
            String fileName = String.format(NextCommandFilePrefix()+"_"+simpleDate.format(dt)+"_"+command+".xml");
            String file_path = FileUtils.BBMC_DEFAULT_DIR+"/Command/"+fileName;
            File file = new File(file_path);
            if(file!=null&&!file.exists()) {
                Log.i(TAG, "!file.exists");
                file.createNewFile();
            }
            return true;
        }
        else if (command.equals("UP"))
        {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyyMMddHHmm");
            String fileName = String.format(NextCommandFilePrefix()+"_"+simpleDate.format(dt)+"_"+command+".xml");
            String file_path = FileUtils.BBMC_DEFAULT_DIR+"/Command/"+fileName;
            File file = new File(file_path);
            if(file!=null&&!file.exists()) {
                Log.i(TAG, "!file.exists");
                file.createNewFile();
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    //-
    // jason:makecommandfile: 명령 파일 생성 일반화(2015/10/06)
    public static String NextCommandFilePrefix()
    {
        String file_path = FileUtils.BBMC_DEFAULT_DIR+"/Command/";

        File dInfo = new File(file_path);
        if(!dInfo.exists())
            dInfo.mkdir();
        File[] fInfos = dInfo.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                if(name.length()=="???_????????????_??.xml".length()) {
                    if (name.endsWith(".xml")) {
                        String s = name.substring(3);
                        if (s.equals("_")) {
                            String m = name.substring(16);
                            if (m.equals("_")) {
                                return true;
                            }
                        }

                    }
                }
                return false;
            }
        });
        int tmp = 0;

        if (fInfos.length > 0)
        {
            for (File fInfo : fInfos)
            {
                int seq = Integer.valueOf(fInfo.getName().substring(0, 3));

                if (seq > tmp)
                {
                    tmp = seq;
                }
            }
        }

        return String.format("%03d", ++tmp);
    }
    public static void debugException(String title, Exception e)
    {
        Log.e(TAG, "DebugException() title="+title+" Exception="+e);

        Exception finalE = null;

        if (e != null)
        {
            finalE = e;

            while (e.getCause() != null)
            {
                finalE = e;
            }

            if (finalE != null)
            {
                debug(" ");


                if ((title!=null)&&(!title.isEmpty()))
                {
                    debug(" ");
                    debug("[ Exception Details: "+title+" ]");
                    debug(" ");
                }

                debug("Exception: "+finalE.getMessage());
                debug("  " + finalE.getStackTrace());
                debug(" ");

                flushDebugLogQueue();
            }
        }
    }

    public static void killCustomExec()
    {
        LOG("killCustomExec() NOT SUPPORT!!!");
        /*
        if (!String.IsNullOrEmpty(model.CustomExeFile) && CustomProcess != null)
        {
            try
            {
                if (! CustomProcess.HasExited)
                {
                    // jason:watchprocstart: 프로세스 시작 감시 및 전방 배치(2013/08/27)
                    removeForegroundProcessId(CustomProcess.Id);

                    // jason:taskkill: taskkill.exe에 의한 프로세스 종료(2015/04/07)
                    //CustomProcess.Kill();
                    SignCastModel.Kill(CustomProcess);
                    //-
                }
            }
            catch
            { }

            CustomProcess = null;

            // jason:logdebuginfo2: 플레이어 디버깅 로그 옵션(2014/03/11)
            SignCastPlayer.ViewModels.DebugModel.DebugAuto("");
            SignCastPlayer.ViewModels.DebugModel.DebugAuto("Check Custom Exec Killed...");
            //-
        }
        */
    }
    public static Date getCurrentDate()
    {
        long now = System.currentTimeMillis ( );
        Date date = new Date(now);
        return date;
    }
    public static String tempSavedConfigFileName()
    {
        String retStr = "";
        int num;

        Random generator = new Random();

        num = generator.nextInt(9999999);
        retStr = String.valueOf(num);
        return retStr;
    }
    // jason:tempdebug: 임시 파일 디버깅(2012/05/29)
    public static void DebugWriteLine(String line)
    {
        debug(line);
        flushDebugLogQueue();
    }

    // 내림차순
    public static class DescendingDate implements Comparator<Date> {

        @Override
        public int compare(Date o1, Date o2) {
            return o2.compareTo(o1);
        }

    }

    // 오름차순
    public static class AscendingDate implements Comparator<Date> {

        @Override
        public int compare(Date o1, Date o2) {
            return o1.compareTo(o2);
        }
    }
    // 내림차순
    public static class DescendingInt implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }

    }

    // 오름차순
    public static class AscendingInt implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }
    /**
     * 이름 오름차순
     * @author falbb
     *
     */
    static class NameAscCompare implements Comparator<String> {

        /**
         * 오름차순(ASC)
         */
        @Override
        public int compare(String arg0, String arg1) {
            // TODO Auto-generated method stub
            return arg0.compareTo(arg1);
        }

    }

    /**
     * 이름 내림차순
     * @author falbb
     *
     */
    static class NameDescCompare implements Comparator<String> {

        /**
         * 내림차순(DESC)
         */
        @Override
        public int compare(String arg0, String arg1) {
            // TODO Auto-generated method stub
            return arg1.compareTo(arg0);
        }

    }
    // jason:24houroverplaytime: 24시간 초과 재생시간 컨텐츠 재생 오류(2013/06/20)
    public static String simpleTimeFormat(Time ts)
    {
        String ret = "";
        boolean mustDisplay = false;

        if (ts.getHours() >= 1)
        //if (ts.Hours >= 1)
        {
            //ret += string.Format(ts.Hours > 9 ? "{0:00}:" : "{0:#0}:", ts.Hours);
            ret += String.valueOf(ts.getHours())+":";
            mustDisplay = true;
        }
        if (mustDisplay || ts.getMinutes() >= 1)
        {
            ret += String.valueOf(ts.getMinutes())+ ":";
            mustDisplay = true;
        }

        ret += String.valueOf(ts.getSeconds());

        if (ts.getTime() != 0)
        {
            ret += String.format(".%3d", ts.getTime());
        }

        return ret;
    }
    //-
    // jason:yesterdayadlog: 이전날의 광고추적로그를 해당일자 광고추적파일로 저장(2012/11/27)
    // jason:touchlogoption: 플레이어 터치로그 옵션(2014/12/03)
    public static void saveTrackingData(List<SlideShowLog> trackingLogs, List<SlideShowTouchLog> trackingTouchLogs) throws IOException {
        List<Date> dates = new ArrayList<Date>();
        for (SlideShowLog logItem : trackingLogs)
        {
            if (!dates.contains(logItem.getPlayDate()))
            {
                dates.add(logItem.getPlayDate());
            }
        }

        for (SlideShowTouchLog logItem : trackingTouchLogs)
        {
            if (!dates.contains(logItem.getTouchDate()))
            {
                dates.add(logItem.getTouchDate());
            }
        }

        if (dates.size() > 0)
        {
            Utils.AscendingDate ascendingDate = new Utils.AscendingDate();
            Collections.sort(dates, ascendingDate);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                xmlSerializer.setOutput(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Date dt : dates)
            {
                Date now = Utils.getCurrentDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                int mMSec = calendar.get(Calendar.MILLISECOND);
                int mHour = calendar.get(Calendar.HOUR);
                int mMin = calendar.get(Calendar.MINUTE);
                int mSec = calendar.get(Calendar.SECOND);

                String currentDateTime = String.format(calendar.get(Calendar.YEAR)+"/%02d"+calendar.get(Calendar.MONTH)+"/%02d"+calendar.get(Calendar.DATE)+" %02d"+
                        calendar.get(Calendar.HOUR)+":%02d"+ calendar.get(Calendar.MINUTE)+":%02d"+ calendar.get(Calendar.SECOND));

                String rootStr = String.format("<Trackings generated="+currentDateTime+"/>");
                xmlSerializer.startDocument("UTF-8", true);
                xmlSerializer.text(rootStr);
//                XElement xmlRoot = XElement.Parse(rootStr);
                List<Integer> ContentSeqList = new ArrayList<Integer>();

                xmlSerializer.startTag(null, "Logs");
                for (SlideShowLog log : trackingLogs)
                {
                    if(log.getPlayDate().compareTo(dt)==0) {
                        HashMap<String, String> attrs = new HashMap();
                        xmlSerializer.text("Log");
                        xmlSerializer.attribute(null, "seq", String.valueOf(log.seq));
                        xmlSerializer.attribute(null, "time", log.startTime);
                        xmlSerializer.attribute(null, "sec", String.valueOf(log.sec));
                        xmlSerializer.attribute(null, "schedule", log.scheduleName);
                        xmlSerializer.attribute(null, "seq", String.valueOf(log.seq));

                        if (!ContentSeqList.contains(log.seq)) {
                            ContentSeqList.add(log.seq);
                        }
                    }
                }
                xmlSerializer.endTag(null, "Logs");

//                xmlRoot.add(logsEl);

                xmlSerializer.startTag(null, "Contents");

                Utils.AscendingInt ascendingInt = new Utils.AscendingInt();

                Collections.sort(ContentSeqList, ascendingInt);
                for (int seq : ContentSeqList)
                {
                    // jason:specialtracking: 동적 변환 광고 컨텐츠 추적(2015/08/21)
                    if (seq > 0)
                    {
                        if( mTrackingContents.get(0).getSeq()==seq) {
                            TrackingContentItem tci = mTrackingContents.get(0);
                            xmlSerializer.text("Content");
                            xmlSerializer.attribute(null, "seq", String.valueOf(tci.getSeq()));
                            xmlSerializer.attribute(null, "name", tci.getName());
                            xmlSerializer.attribute(null, "schedule", tci.getScheduleName());
                        }

//                            contentsEl.add(contentEl);
                    }
                    else
                    {
                        if( mSpecialTrackingContents.get(0).getSeq()==seq) {
                            SpecialTrackingItem item = mSpecialTrackingContents.get(0);
                            xmlSerializer.text("Content");
                            xmlSerializer.attribute(null, "seq", String.valueOf(item.getSeq()));
                            xmlSerializer.attribute(null, "name", item.getName());
                            xmlSerializer.attribute(null, "schedule", item.getScheduleName());
                        }

//                            contentsEl.Add(contentEl);
                    }
                    //-
                }
//                xmlRoot.Add(contentsEl);
                xmlSerializer.endTag(null, "Contents");

                xmlSerializer.startTag(null, "TouchLogs");
//                XElement tLogsEl = new XElement("TouchLogs");
                for(SlideShowTouchLog log : trackingTouchLogs)   //.Where(t => t.TouchDate.CompareTo(dt) == 0).ToList())
                {
                    if(log.getTouchDate().compareTo(dt)==0) {
                        xmlSerializer.text("Log");
                        xmlSerializer.attribute(null, "name", log.getName());
                        xmlSerializer.attribute(null, "time", log.getTouchTime());
                        xmlSerializer.attribute(null, "ukid", log.getUkid());
                        xmlSerializer.attribute(null, "schedule", log.getScheduleName());

//                        XElement logEl = new XElement("Log", attrs);
//                        tLogsEl.Add(logEl);
                    }
                }
//                xmlRoot.Add(tLogsEl);
                xmlSerializer.endTag(null, "TouchLogs");
                xmlSerializer.endDocument();
                xmlSerializer.flush();

//                var uni = new UTF8Encoding();
//                var ms = uni.GetBytes(xmlRoot.ToString(SaveOptions.None));

                String fileName = FileUtils.currentTrackFileName(dt);

//                FileStream xmlFile = new FileStream(fileName, FileMode.Create);
//                xmlFile.Write(ms, 0, Convert.ToInt32(ms.Length));
//                xmlFile.Close();

                FileOutputStream fileos = new FileOutputStream(fileName, false );

                String dataWrite = writer.toString();
                fileos.write(dataWrite.getBytes());
                fileos.close();


            }

            trackingLogs.clear();
            trackingTouchLogs.clear();
        }
    }
    //-
    // jason:playeragentstartdt: 플레이어 및 에이전트 시작일시 보고(2015/09/30)
    public static Date ParseExactDateTime(String dtStr)
    {
        if ((dtStr==null)|| dtStr.isEmpty())
        {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");//스케줄 파일명과 관련 있음...
        dtStr.replace("-", "").replace("/", "").
                replace(" ", "").replace(":", "");
        try {
            Date retDate = format.parse(dtStr);
            return retDate;
        } catch (ParseException e) {
            e.printStackTrace();
            debugException("ParseExactDateTime", e);
        }
        return null;
    }
    //-
    public static List<BasicNameValuePair> getParseQueryString(String param)
    {
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        String temp = param;
        while(temp.length()>0) {
            String value;
            int separator = temp.indexOf("=");
            if(separator<=0)
                break;
            String key = temp.substring(0, separator);
            temp = temp.substring(separator + 1);
            separator = temp.indexOf("&");
            if(separator > 0) {
                value = temp.substring(0, separator);
            }
            else
                value = temp;
            nameValuePairs.add(new BasicNameValuePair(key, value));
            Log.d(TAG, "getParseQueryString() key="+key+" value="+value);
        }
        return nameValuePairs;
    }
    public static int getTextColor(String textColor)
    {
        int colorInt = 0;

        if (textColor.length() == 4) {
            String colorString = String.format("#" + textColor.substring(1, 2) + textColor.substring(1, 2) + textColor.substring(2, 3) + textColor.substring(2, 3) + textColor.substring(3, 4) + textColor.substring(3, 4));
            Log.d(TAG, "colorString=" + colorString);
            colorInt = Color.parseColor(colorString);
        } else {
            colorInt = Color.parseColor(textColor);
            //textContentView.setTextColor(colorInt);
        }
        return colorInt;
    }
    /**

     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */

    public static float convertDpToPixel(float dp, Context context){

        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float px = dp * (metrics.densityDpi / 160f);

        return px;

    }


    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */

    public static float convertPixelsToDp(float px, Context context){

        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float dp = px / (metrics.densityDpi / 160f);

        return dp;

    }
    /**
     * 임의 Salt 문자열 생성
     */
    public static String getRandomSalt() {
        int    i = 22;
        char[] _tmpseed =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
                        .toCharArray();

        return random(i, _tmpseed);
    }

    /**
     * 임의 문자열 생성
     */
    public static String random(int i, char[] ac) {
        StringBuffer stringbuffer = new StringBuffer(i);
        Random       random1      = new Random(System.currentTimeMillis());
        int          j            = ac.length;

        for (int k = 0; k < i; k++) {
            stringbuffer.append(ac[(int) (random1.nextFloat() * (float) j)]);
        }

        return stringbuffer.toString();
    }
    /**
     * 물리적인 루트 디렉토리 획득
     */
    public static String getPhysicalRoot(String ukid) {
        return getPhysicalRoot(ukid, "");
    }


    /**
     * 물리적인 루트 디렉토리 획득
     */
    public static String getPhysicalRoot(String ukid, String site) {
        if (ukid == null || ukid.isEmpty()) {
            return null;
        }

        String rootDirPath = FileUtils.BBMC_DEFAULT_DIR;
        String ftpDirName = "";
        //String ftpDirName = getFileProperty("dir.ftp");

        if (ukid.equals("Log")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/upload/logs";
        } else if (ukid.equals("Debug")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/upload/debugs";
        } else if (ukid.equals("Shot")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/upload/shots";
        } else if (ukid.equals("Track")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/upload/tracks";
        } else if (ukid.equals("TrackBackup")) {
            return getValidRootDir(rootDirPath) + "trackbackups";
        } else if (ukid.equals("TrackError")) {
            return getValidRootDir(rootDirPath) + "trackerrors";
        } else if (ukid.equals("StbTrack")) {
            return getValidRootDir(rootDirPath) + "stbtracks";
        } else if (ukid.equals("Logo")) {
            return getValidRootDir(rootDirPath) + "logos";
        } else if (ukid.equals("DeployedSchedule")) {
            return getValidRootDir(rootDirPath)  + "/schedules";
//            return getValidRootDir(rootDirPath) + ftpDirName + "/schedules";
        } else if (ukid.equals("DeployedBundle")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/kctnts";
        } else if (ukid.equals("GuideImg")) {
            return getValidRootDir(rootDirPath) + ftpDirName + "/introimgs" +
                    ((site == null || site.isEmpty()) ? "": "/" + site);
        } else if (ukid.equals("Repository")) {
            return getValidRootDir(rootDirPath) + "repositories" +
                    ((site == null || site.isEmpty()) ? "": "/" + site);
        } else if (ukid.equals("AdditionalRepository")) {
            return getValidRootDir(rootDirPath) + "repositories/" + site + "/Additional";
        } else if (ukid.equals("Schedule")) {
            return getValidRootDir(rootDirPath) + "repositories/" + site + "/Schedule";
        } else if (ukid.equals("PlayerSchedule")) {
            return getValidRootDir(rootDirPath) + "repositories/" + site + "/SchedContent";
        } else if (ukid.equals("SchedulePkg")) {
            return getValidRootDir(rootDirPath) + "repositories/" + site + "/SchedPkg";
        } else if (ukid.equals("Setup")) {
            return getValidRootDir(rootDirPath) + "setups";
        } else if (ukid.equals("Temp")) {
            return getValidRootDir(rootDirPath) + "temp";
        } else if (ukid.equals("FtpData")) {
            return getValidRootDir(rootDirPath) + "psn/data/ftp";
        } else if (ukid.equals("PhotoAsset")) {
            return getValidRootDir(rootDirPath) + "ast/assets";
        } else if (ukid.equals("PhotoPlayer")) {
            return getValidRootDir(rootDirPath) + "ast/players";
            //
            // [CU] ext
            //
        } else if (ukid.equals("PsnLifeStyleAd")) {
            return getValidRootDir(rootDirPath) + "psn/lifestyles";
        } else if (ukid.equals("PsnSponsorAd")) {
            return getValidRootDir(rootDirPath) + "psn/sponsors";
        } else if (ukid.equals("PsnEventAd")) {
            return getValidRootDir(rootDirPath) + "psn/events";
        } else if (ukid.equals("PsnWeather")) {
            return getValidRootDir(rootDirPath) + "psn/weatherbgs";
        } else if (ukid.equals("PsnAirline")) {
            return getValidRootDir(rootDirPath) + "psn/airlines";
        } else if (ukid.equals("WeatherData")) {
            return getValidRootDir(rootDirPath) + "psn/data/weathers";
        } else if (ukid.equals("AirportData")) {
            return getValidRootDir(rootDirPath) + "psn/data/airports";
        } else if (ukid.equals("SnsData")) {
            return getValidRootDir(rootDirPath) + "psn/data/sns";
            //-
        }

        return null;
    }
    /**
     * 유효한 루트 디렉토리 획득
     */
    public static String getValidRootDir(String rootDir) {
        if (rootDir == null || rootDir.isEmpty()) {
            // 최악의 경우
            return Environment.getExternalStorageDirectory().toString();
        }

        String tmp = rootDir.replace("\\", "/");

        return tmp.endsWith("/") ? tmp: tmp + "/";
    }

    /**
     * 지정 유형에 대한 기본 확장자 검색 패턴 획득
     */
    public static String getFileSearchPattern(String ukid) {
        if (ukid == null || ukid.isEmpty()) {
            return "*.*";
        }

        if (ukid.equals("Log") || ukid.equals("Debug")) {
            return ".txt";
        } else if (ukid.equals("Shot")) {
            return ".jpg";
        } else if (ukid.equals("Track")) {
            return ".xml";
        } else if (ukid.equals("GuideImg")) {
            return "*";
        } else if (ukid.equals("Schedule")) {
            return ".scs";
        } else if (ukid.equals("PlayerSchedule")) {
            return ".scd";
        } else if (ukid.equals("SchedulePkg")) {
            return ".scz";
        }

        return "*";
    }
    public static Date getAfterDateFrNow(Date date)
    {
        Date d = getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.HOUR, date.getHours());
        calendar.add(Calendar.MINUTE, date.getMinutes());
        calendar.add(Calendar.SECOND, date.getSeconds());
        Date retdate = calendar.getTime();

        return retdate;
    }
    public static String getDateDay(Calendar calendar, Context c)
    {
        String day = "";
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayNum)
        {
            case 1 :    //일
                day = c.getResources().getString(R.string.sunDay);
                break;
            case 2 :    //월
                day = c.getResources().getString(R.string.monDay);
                break;
            case 3 :    //화
                day = c.getResources().getString(R.string.tueDay);
                break;
            case 4 :    //수
                day = c.getResources().getString(R.string.wedDay);
                break;
            case 5 :    //목
                day = c.getResources().getString(R.string.thuDay);
                break;
            case 6 :    //금
                day = c.getResources().getString(R.string.friDay);
                break;
            case 7 :    //토
                day = c.getResources().getString(R.string.satDay);
                break;
        }
        return day;
    }
    public void log(String s)
    {
        Log.d(TAG, s);
    }
    // 내림차순
    public static class Descending implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }

    }

    // 오름차순
    public class Ascending implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }

    }
    public static void setlogLock(boolean b) {
        dLogLock = b;
    }
    public static boolean getlogLock() {
        return dLogLock;
    }

    public static void OpenNewVersion(String apkName, Context c) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkName)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);

    }
    public static String getElementAttrValue(String fls, String selElement, String attr) {
        String retValue = "";

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

//            parser.setInput(fls);
//            InputStream is = getResources().openRawResource(R.raw.agentoptdata);
            InputStream is = new ByteArrayInputStream(fls.getBytes());
            if(is==null)
                Log.e(TAG, "InputStream... null!!!!");
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean isItemTag = false;
            DownFileInfo downloadInfo = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        //Log.d(TAG, "START_TAG.name="+name);
                        if (name.equals(selElement)) {

                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                //Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals(attr)) {
                                    retValue = parser.getAttributeValue(i);
                                }
                            }
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }


        } catch (Exception e) {
            Log.d(TAG, "1 Error in ParseXML()", e);
        }
        return retValue;
    }
    public static List<UpgradePlayerData> getElementListForUpgrade(String fls, String selElement) {
        List<UpgradePlayerData> elementList = new ArrayList<>();
        String retValue = "";
        UpgradePlayerData playerData = null;
        InputSource inSource =  new InputSource(new StringReader(fls));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        Document doc = null;
        try {
            doc = dBuilder.parse(inSource);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Item");

        if (nList != null) {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Log.d(TAG, "Current Element :" + nNode.getNodeName());
                playerData = new UpgradePlayerData();
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NamedNodeMap attrs = eElement.getAttributes();
                    for (int i = 0; i < attrs.getLength(); i++) {
                        Node nodeAttr = attrs.item(i);
                        switch (nodeAttr.getNodeName()) {

                            case "type":
                                playerData.type = nodeAttr.getNodeValue();
                                break;
                            case "editioncode":
                                playerData.edition = nodeAttr.getNodeValue();
                                break;

                            case "version":
                                playerData.ver = nodeAttr.getNodeValue();
                                break;

                            case "length":
                                playerData.length = nodeAttr.getNodeValue();
                                break;
                            case "stable":
                                playerData.stable = nodeAttr.getNodeValue();
                                break;

                            case "date":
                                playerData.date = nodeAttr.getNodeValue();
                                break;
                            default:
                                break;
                        }
                    }
                    NodeList n = eElement.getChildNodes();
                    for (int i = 0; i < n.getLength(); i++) {
                        Node node = n.item(i);
                        String local = Locale.getDefault().getLanguage();
                        switch (node.getNodeName()) {
                            case "DescKo" :

                                if(local.equals("ko")) {
                                    NodeList child = node.getChildNodes();
                                    for(int c = 0; c<child.getLength(); c++) {
                                        Node desc = child.item(c);
                                        playerData.desc = desc.getNodeValue();
                                    }
                                }
                                break;
                            case "DescEn" :
                                if(!local.equals("ko")) {
                                    NodeList child = node.getChildNodes();
                                    for(int c = 0; c<child.getLength(); c++) {
                                        Node desc = child.item(c);
                                        playerData.desc = desc.getNodeValue();
                                    }
                                }
                                break;
                            default:
                                break;
                        }

                    }
                }
                if(playerData.type.equals("P")&&playerData.edition.equals("AE")) {
                    String ver = getAndroidVersion();
                    String[] localVerlist = ver.split(".");
                    String[] newVerlist = playerData.ver.split(".");

                    if((localVerlist.length > 4)&&(newVerlist.length > 4)) {
                        if (Integer.valueOf(localVerlist[0]) < Integer.valueOf(newVerlist[0]))
                            elementList.add(playerData);
                        else if (Integer.valueOf(localVerlist[0]) == Integer.valueOf(newVerlist[0])) {
                            if (Integer.valueOf(localVerlist[1]) < Integer.valueOf(newVerlist[1]))
                                elementList.add(playerData);
                            else if (Integer.valueOf(localVerlist[1]) == Integer.valueOf(newVerlist[1])) {
                                if (Integer.valueOf(localVerlist[2]) < Integer.valueOf(newVerlist[2]))
                                    elementList.add(playerData);
                                else if (Integer.valueOf(localVerlist[2]) == Integer.valueOf(newVerlist[2])) {
                                    if (Integer.valueOf(localVerlist[3]) < Integer.valueOf(newVerlist[3]))
                                        elementList.add(playerData);
                                }
                            }
                        }
                    }
                    else
                        elementList.add(playerData);
                }
            }
        }

        return elementList;
    }
    public static String getAndroidVersion() {
        String versionName = "";

        try {
            versionName = String.valueOf(Build.VERSION.RELEASE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
