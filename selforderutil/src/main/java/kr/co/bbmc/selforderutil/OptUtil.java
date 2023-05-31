package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class OptUtil {
    private static final String TAG = "OptUtil";
    private static final boolean LOG = false;

    private static int currentOptType = FileUtils.None;
    private static HashMap<String, String> optStages = new HashMap();

    // Agent 기본 옵션
    private static String[][] initAgentOptions = new String[][] {
            new String[] { "PlayerStart", "True" },
            new String[] { "MonitorMins", "3" },
            new String[] { "FtpActiveMode", "False" },

            new String[] { "Server.StbId", "-1" },
            new String[] { "Server.StbName", "" },
            new String[] { "Server.StbUdpPort", "11001" },
            new String[] { "Server.StbServiceType", "I" },
            new String[] { "Server.FtpHost", "saas.signcast.co.kr" },
            new String[] { "Server.FtpPort", "21" },
            new String[] { "Server.FtpUser", "signcast" },
            new String[] { "Server.FtpPassword", "signcastpwd" },
            new String[] { "Server.ServerHost", "saas.signcast.co.kr" },
            new String[] { "Server.ServerPort", "80" },
            new String[] { "Server.ServerUkid", "signcast" },
    };
    private static String[][] initPadOptions = new String[][] {
            new String[] { "Server.StbUdpPort", "11001" },
            new String[] { "Server.StbServiceType", "C" },
            new String[] { "Server.ServerHost", "test.signcast.co.kr" },
            new String[] { "Server.ServerPort", "8281" },
            new String[] { "Server.ServerUkid", "signcast" },
    };
    private static String[][] initDidOptions = new String[][] {
            new String[] { "Server.StbId", "-1" },
            new String[] { "Server.StbName", "" },
            new String[] { "Server.StbUdpPort", "11001" },
            new String[] { "Server.StbServiceType", "D" },
            new String[] { "Server.ServerHost", "test.signcast.co.kr" },
            new String[] { "Server.ServerPort", "8281" },
            new String[] { "Server.ServerUkid", "signcast" },
    };
    // Manager 기본 옵션
    private static String[][] initMgrOptions = new String[][] {
            new String[] { "Lang", "" },
            new String[] { "Region", "" },
            new String[] { "Content.DefaultWidth", "300" },
            new String[] { "Content.DefaultHeight", "300" },
            new String[] { "Content.DefaultPlaytime", "00:00:20.000" },
            new String[] { "Content.DefaultVolume", "100" },
            new String[] { "SlideShow.DefaultFadeApplied", "True" },
            new String[] { "SlideShow.DefaultFadeTimeType", "M" },
            new String[] { "SlideShow.TransparentBGApplied", "False" },
            new String[] { "SlideShow.DisplayAfterDuration", "True" },
            new String[] { "SlideShow.PptViewer", "" },
            new String[] { "SlideShow.WebBrowser", "" },
            new String[] { "Communication.TcpPort", "11002" },
            new String[] { "Weather.City", "" },
            new String[] { "Weather.Display", "" },
            new String[] { "Weather.Unit", "C" },
            new String[] { "Weather.ApiKey", "" },
    };

    // Player 기본 옵션
    private static String[][] initPlayerOptions = new String[][] {
            new String[] { "Lang", "" },
            new String[] { "Region", "" },

            new String[] { "SlideShow.GuideImageFile", "" },
            new String[] { "SlideShow.PptViewerFile", "" },
            new String[] { "SlideShow.ScheduleFile", "" },
            new String[] { "SlideShow.StoreMenuFile", "" },
            new String[] { "SlideShow.ConnectionCheck", "False" },
            new String[] { "SlideShow.ConnectionErrorDisplayed", "False" },
            new String[] { "SlideShow.CustomExec", "" },
            new String[] { "SlideShow.CustomArgs", "" },
            new String[] { "SlideShow.SleepSecs", "0" },
            new String[] { "SlideShow.KioskStartOnPlayerStartUp", "False" },
            new String[] { "SlideShow.KioskAutoStartSecs", "0" },
            new String[] { "SlideShow.AfterClosingAction", "PF" },
            new String[] { "SlideShow.ClosingDelaySecs", "60" },
            new String[] { "SlideShow.MonitorAction", "Monitor" },
            new String[] { "SlideShow.RestartHours", "3" },
            new String[] { "SlideShow.ScreenExtMode", "0" },
            new String[] { "SlideShow.TestNoticeDisplayed", "False" },
            new String[] { "SlideShow.TestNoticeText", "" },
            new String[] { "SlideShow.TopmostDisplayed", "False" },
            new String[] { "SlideShow.TimeSyncRequired", "False" },
            new String[] { "SlideShow.AllSchedulePlayMode", "False" },
            new String[] { "SlideShow.WebBrowserFile", "" },
            new String[] { "SlideShow.SimpleClockDisplayed", "False" },
            new String[] { "SlideShow.SimpleClockDisplayedOnlyOntheHour", "True" },
            new String[] { "SlideShow.SlideProgressBarDisplayed", "False" },
            new String[] { "SlideShow.SlideProgressBarColor", "Red" },
            new String[] { "SlideShow.OperationInfoDebugged", "False" },
            new String[] { "SlideShow.TouchInfoLogged", "False" },
            new String[] { "SlideShow.DefaultFadeApplied", "True" },
            new String[] { "SlideShow.DefaultFadeTimeType", "M" },
            new String[] { "SlideShow.TransparentBGApplied", "False" },
            new String[] { "SlideShow.DisplayAfterDuration", "True" },
            // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
            new String[] { "SlideShow.RoutineSchedPrefix", "" },
            new String[] { "SlideShow.AutoDelSchedDays", "0" },
            //-

            new String[] { "Playtime.MonBeginTime", "00:00" },
            new String[] { "Playtime.MonEndTime", "00:00" },
            new String[] { "Playtime.TueBeginTime", "00:00" },
            new String[] { "Playtime.TueEndTime", "00:00" },
            new String[] { "Playtime.WedBeginTime", "00:00" },
            new String[] { "Playtime.WedEndTime", "00:00" },
            new String[] { "Playtime.ThuBeginTime", "00:00" },
            new String[] { "Playtime.ThuEndTime", "00:00" },
            new String[] { "Playtime.FriBeginTime", "00:00" },
            new String[] { "Playtime.FriEndTime", "00:00" },
            new String[] { "Playtime.SatBeginTime", "00:00" },
            new String[] { "Playtime.SatEndTime", "00:00" },
            new String[] { "Playtime.SunBeginTime", "00:00" },
            new String[] { "Playtime.SunEndTime", "00:00" },

            new String[] { "Communication.ManagerTcpPort", "11002" },
            new String[] { "Communication.StbUdpPort", "11001" },

            new String[] { "Weather.City", "" },
            new String[] { "Weather.Display", "" },
            new String[] { "Weather.Unit", "C" },
            new String[] { "Weather.ApiKey", "" },

            // jason:volumectrlbytime: 시간별 음량 설정(2015/11/17)
            new String[] { "Volume", "" },
            //-
    };
//    private static int optType;
//    private static List<Date> list;

    /*

        private static String getNow()
        {
            return String.Format("{0}/{1:00}/{2:00} {3:00}:{4:00}:{5:00}",
                    DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day,
                    DateTime.Now.Hour, DateTime.Now.Minute, DateTime.Now.Second);
        }
    */
    private static String getFilename(int currentOptType)
    {
        String filename = "";

        if (currentOptType == FileUtils.Manager)
        {
            filename = "ManagerOptData.xml";
        }
        else if (currentOptType == FileUtils.Player)
        {
            filename = "PlayerOptData.xml";
        }
        else if (currentOptType == FileUtils.Agent)
        {
            filename = "AgentOptData.xml";
        }
        else if (currentOptType == FileUtils.SearchKeyword)
        {
            filename = "SearchKeywordData.xml";
        }
        else if (currentOptType == FileUtils.Holiday)
        {
            filename = "HolidayData.xml";
        }
        else if (currentOptType == FileUtils.PayCastDid)
        {
            filename = "PayCastDidOpt.xml";
        }
        else if (currentOptType == FileUtils.PayCastPad)
        {
            filename = "PayCastPadOpt.xml";
        }
        else if (currentOptType == FileUtils.PayCastAgent)
        {
            filename = "PayCastAgentOpt.xml";
        }

        if (filename==null )
        {
            return "";
        }

        return filename;
    }
    /*
        private static void saveFile(String xmlRoot, int type)
        {
            String filename = getFilename(type);

            if ((filename==null)||filename.isEmpty())
            {
                //throw new ArgumentNullException("No option type specified.");
                Log.d(TAG, "No option type specified.");
            }
            else
            {
    //            var uni = new UTF8Encoding();
    //            var ms = uni.GetBytes(xmlRoot.ToString(System.Xml.Linq.SaveOptions.None));

                File newFile = new File(FileUtils.BBMC_DEFAULT_DIR+"Data//"+filename);
                FileOutputStream xmlFile = null;
                try {
                    xmlFile = new FileOutputStream(newFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    xmlFile.write(xmlRoot.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    xmlFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    */
    private static void printOptions(String title)
    {
/*
        Debug.WriteLine(String.Format("-------------------------- [{0}]", title));

        List<String> keys = optStages.Keys.ToList();
        keys.Sort();

        foreach (String key in keys)
        {
            Debug.WriteLine(String.Format("{0}={1}", key, optStages[key]));
        }

        Debug.WriteLine("-------------------------- end");
*/
    }

    private static HashMap<String, String> initializeOptions(String[][] initOpts)
    {
        HashMap<String, String> options = new HashMap<String, String>();

        for (String[] itemRow : initOpts)
        {
            options.put(itemRow[0], itemRow[1]);
        }

        return options;
    }
    private static void readFile(int type)
    {
        String name = getFilename(type);

        if ((name==null||(name.isEmpty())))
        {
            Log.d(TAG, "No option type specified");
        }
        else
        {
            File filename = new File(FileUtils.BBMC_PAYCAST_DIRECTORY+"Data//"+name);
            if (filename.exists())
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder dBuilder = null;
                try {
                    dBuilder = factory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                if(optStages.size()>0)
                    optStages.clear();
                Document doc = null;
                try {
                    doc = dBuilder.parse(filename);
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Option");


                for (int temp = 0; temp < nList.getLength(); temp++)
                {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        NodeList n = eElement.getChildNodes();
                        for(int i = 0; i<n.getLength(); i++)
                        {
                            Node node = n.item(i);
                            if(node.getNodeType()!=Node.TEXT_NODE) {
                                optStages.put(node.getNodeName(), node.getTextContent());
                                if(LOG)
                                    Log.d(TAG, "node.getNodeName()=" + node.getNodeName() + " getTextContent()=" + node.getTextContent());
                            }

                        }
                        if(LOG)
                            Log.d(TAG, "eElement.getTagName()=" +eElement.getTagName() + " value()=" + eElement.getAttribute(eElement.getTagName() ));
                    }
                }
            }
        }
    }


    public static String GetValue(String key)
    {
        return GetValue(key, "");
    }
    public static String GetValue(String key, String defaultVal)
    {
        String tmp = optStages.get(key);
        if ((tmp==null)||tmp.isEmpty())
        {
            return defaultVal;
        }
        return tmp;
    }

    public static int GetValue(String key, int defaultVal)
    {
        String tmp = optStages.get(key);
        if ((tmp==null)||tmp.isEmpty())
        {
            return defaultVal;
        }
        return Integer.valueOf(tmp);
    }

    public static boolean GetValue(String key, boolean defaultVal)
    {
        String tmp = optStages.get(key);
        if ((tmp==null)||tmp.isEmpty())
        {
            return defaultVal;
        }
        return Boolean.valueOf(tmp);
    }

    public static void ReadOptions(int optType, Context c)
    {
        ReadOptions(optType, true, null, c);
    }

    public static void ReadOptions(int optType, boolean doInitOption, Context c)
    {
        ReadOptions(optType, doInitOption, null, c);
    }

    public static void ReadHoliday()
    {
        Log.d(TAG, "ReadHoliday()  미구현");
    }
    public static void ReadOptions(int optType, boolean doInitOption, String pathFilename, Context c)
    {
        Log.d(TAG, " ReadOptions() ");

        if(optStages.size()>0)
            optStages.clear();

        optStages = new HashMap<>();
//        currentOptType = optType;

        if (doInitOption)
        {
            if (optType == FileUtils.Manager)
            {
                optStages = initializeOptions(initMgrOptions);
            }
            else if (optType == FileUtils.Player)
            {
                optStages = initializeOptions(initPlayerOptions);

                // 디스플레이로 TV를 1차 적용한 프로젝트의 경우 monitor action 대상을 TV로 설정
                if (c.getString(R.string.editionCode).equals("HN") ||
                        c.getString(R.string.editionCode).equals("YS"))
                {
                    SetValue("SlideShow.MonitorAction", "TV");
                }
                //-
            }
            else if (optType == FileUtils.Agent)
            {
                optStages = initializeOptions(initAgentOptions);
            }
            else if (optType == FileUtils.PayCastAgent)
            {
                optStages = initializeOptions(initAgentOptions);
            }
            else if (optType == FileUtils.PayCastPlayer)
            {
                optStages = initializeOptions(initPlayerOptions);

                // 디스플레이로 TV를 1차 적용한 프로젝트의 경우 monitor action 대상을 TV로 설정
                if (c.getString(R.string.editionCode).equals("HN") ||
                        c.getString(R.string.editionCode).equals("YS"))
                {
                    SetValue("SlideShow.MonitorAction", "TV");
                }
                //-
            }
            else if (optType == FileUtils.PayCastDid)
            {
                optStages = initializeOptions(initDidOptions);

            }
            else if (optType == FileUtils.PayCastPad)
            {
                optStages = initializeOptions(initPadOptions);

            }
        }

        try
        {
            if ((pathFilename!=null)&&!pathFilename.isEmpty())
            {
                File fXmlFile = new File(pathFilename);
                if (fXmlFile.exists())
                {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder dBuilder = factory.newDocumentBuilder();

                    Document doc = dBuilder.parse(fXmlFile);

                    doc.getDocumentElement().normalize();

                    NodeList nList = doc.getElementsByTagName("Option");

                    for (int temp = 0; temp < nList.getLength(); temp++)
                    {
                        Node nNode = nList.item(temp);
                        Log.d(TAG, "Current Element :" + nNode.getNodeName());
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            NodeList n = eElement.getChildNodes();
                            for(int i = 0; i<n.getLength(); i++)
                            {
                                Node node = n.item(i);
                                if(node.getNodeType()!=Node.TEXT_NODE) {
                                    optStages.put(node.getNodeName(), node.getTextContent());
                                    if(LOG)
                                        Log.d(TAG, "node.getNodeName()=" + node.getNodeName() + " getTextContent()=" + node.getTextContent());
                                }

                            }
                            if(LOG)
                                Log.d(TAG, "eElement.getTagName()=" +eElement.getTagName() + " value()=" + eElement.getAttribute(eElement.getTagName() ));
                        }
                    }
                }
            }
            else
            {
                readFile(optType);
            }
        }
        catch (Exception ex)
        {
            //          DebugModel.DebugException("Read options from local file", ex);
        }
    }

    public static void ReadAndSaveOptions(int optType, HashMap m, Context c)
    {
        ReadOptions(optType, c);
        HashMap<String, String> dic = m;
        if (dic.size() >0)
        {
//            List<String> keys = (List<String>) dic.keySet();
            for (String key :  dic.keySet())
            {
                if(optStages.containsKey(key)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        optStages.replace(key, dic.get(key));
                    }
                    else {
                        optStages.remove(key);
                        optStages.put(key, dic.get(key));
                    }
                }
                else
                    optStages.put(key,dic.get(key));
                Log.d(TAG, "ReadAndSaveOptions() key="+key+" dic.get(key)="+dic.get(key));
            }
            String name = getFilename(optType);
            try {
                SaveOptions(FileUtils.BBMC_PAYCAST_DIRECTORY+"Data//"+name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, String> GetCurrentOptionDic()
    {
        return optStages;
    }

    public static List<String> ReadOtherOptions(int optType)
    {
        List<String> ret = new ArrayList<String>();
        currentOptType = optType;

        String name = getFilename(optType);

        if ((name!=null)&&!name.isEmpty()) {
            File filename = new File(FileUtils.BBMC_PAYCAST_DIRECTORY+"Data//"+name);

            if (filename.exists()) {
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
                    doc = dBuilder.parse(filename);
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("SignCast");

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    Log.d(TAG, "Current Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        NodeList n = eElement.getChildNodes();
                        for(int i = 0; i<n.getLength(); i++)
                        {
                            Node node = n.item(i);
                            if(node.getNodeType()!=Node.TEXT_NODE) {
//                                String value = node.getNodeValue();
                                String content = node.getTextContent();
                                ret.add(content);
                            }

                        }
                    }
                }
            }
        }
        return ret;
    }


    public static List<Date> ReadOtherDateTimeOptions(int optType)
    {
        List<String> list = ReadOtherOptions(optType);
        List<Date> ret = new ArrayList<Date>();

        for (String dtStr : list)
        {
            if ((dtStr!=null&&!dtStr.isEmpty()) && dtStr.length() == 10)
            {
                String d = dtStr.replace("-", "").replace("/", "");
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");

                try {
                    ret.add(sdfNow.parse(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
    //      #endregion


//        #region 파일 저장

    public static void PrepareOptions(int optType)
    {
        optStages = new HashMap<>();
        currentOptType = optType;
    }

    public static void SetValue(String key, String value)
    {
        optStages.put(key, value);
    }

    public static void SetValues(String[][] opts)
    {
        for (String[] itemRow : opts)
        {
            optStages.put(itemRow[0],itemRow[1]);
        }
    }
    /*
        public static void SaveOptions()
        {
            SaveOptions(null, null);
        }
    */
    public static void SaveOptions(String pathFilename) throws IOException {
        File file = new File(pathFilename);
        FileOutputStream fileos = new FileOutputStream(file);

//        FileOutputStream fileos = fs;
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag(null, "Option");
        for(String key : optStages.keySet())
        {
            xmlSerializer.startTag(null, key);
            xmlSerializer.text(optStages.get(key));
            xmlSerializer.endTag(null, key);
            if(key.equals("SlideShow.ScheduleFile"))
                Log.d(TAG, "key="+key+" get(key)="+optStages.get(key));
        }

        xmlSerializer.endTag(null, "Option");
        xmlSerializer.endDocument();
        xmlSerializer.flush();
        String dataWrite = writer.toString();
        fileos.write(dataWrite.getBytes());
        fileos.close();
/*
        catch (Exception ex)
        {
            DebugModel.DebugException("Save options to local file", ex);
        }
*/
    }

    public static void SaveHolidayOptions(int type, List<Date> l)
    {
        int optType = type;
        List<Date> list = l;
        List<String> strList = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        for (Date dt : list)
        {
            strList.add(sdf.format(dt));
        }

        SaveOptions(optType, strList);
    }
    /*
        public static void SaveOptions(int type, List<Date> l)
        {
            List<String> strList = new ArrayList<String>();
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");

            for (Date dt : l)
            {
                strList.add(sdfNow.format(dt));
            }

            SaveOptions(type, strList);
        }
    */
    public static void SaveOptions(int optType, List<String> list)
    {
        HashMap<String, String> element;
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));

        try
        {
            currentOptType = optType;
            String filename = getFilename(currentOptType);
            File file = new File(FileUtils.BBMC_PAYCAST_DIRECTORY+"Data//"+filename);

            String nodeName = "Node";
//            String rootStr = "<SignCast generated="+Utils.getCurrentDate()+" />";


            if (optType == FileUtils.SearchKeyword)
            {
                nodeName = "SearchKeyword";
            }
            else if (optType == FileUtils.Holiday)
            {
                nodeName = "Holiday";
            }
            FileOutputStream fileos = new FileOutputStream(file);

//        FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
//            xmlSerializer.startTag(null, nodeName);
            for (String item : list)
            {
                xmlSerializer.startTag(null, nodeName);
                xmlSerializer.text(item);
                xmlSerializer.endTag(null, nodeName);
                xmlSerializer.text("\n");
                Log.d(TAG, "nodeName item="+item+" nodeName="+nodeName);
            }

//            xmlSerializer.endTag(null, nodeName);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();

        }
        catch (Exception ex)
        {
//            DebugModel.DebugException("Save options to local file", ex);
        }
    }
    /*
        private static void saveFile(HashMap<String, String> elment, int type)
        {
            String filename = getFilename(type);

            if (string.IsNullOrEmpty(filename))
            {
                throw new ArgumentNullException("No option type specified.");
            }
            else
            {
                var uni = new UTF8Encoding();
                var ms = uni.GetBytes(xmlRoot.ToString(System.Xml.Linq.SaveOptions.None));

                FileStream xmlFile = new FileStream(getFilename(), FileMode.Create);
                xmlFile.Write(ms, 0, Convert.ToInt32(ms.Length));
                xmlFile.Close();
            }
        }
    //        #endregion


    //        #region 기타 메소드
    */
    // jason:uploaddebuglog: 디버그 파일 업로드(2015/04/23)
    public static String OptDateTimeValue(Date dt)
    {
        return OptDateTimeValue(dt, false);
    }

    public static String OptDateTimeValue(Date dt, boolean ignoredBeforeNow)
    {
        Date now = Utils.getCurrentDate();
        if (now.compareTo(dt) < 0 || !ignoredBeforeNow)
        {
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return sdfNow.format(dt);
        }
        else
        {
            return "";
        }
    }

    public static Date OptDateTimeValue(String dtStr)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = dtStr.replace("-", "").replace("/", "").
                replace(" ", "").replace(":", "");
        try {
            return sdfNow.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d = Utils.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();

    }
//-
}
