package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PropUtil {
    private static final String TAG = "PropUtil";

    public String serverSSLEnabled;
    public String backgroundImgFile;
    public String updateFtpServerUrl;
    public String updateFTPServerUsername;
    public String updateFTPServerPassword;
    public String updateInfoUrl;
    public String playerRestartThresholdPct;
    public String screenAnalysisMins;


    private static Context mContext;
    private static HashMap<String, String> initProps = new HashMap<String, String>();
    private static HashMap<String, String> properties = new HashMap();

    public static void init(Context c){
        initProps.put("UpdateInfoURL", "http://update.signcast.co.kr/SignCastUpdate.xml");
        initProps.put("UpdateFTPServerURL", "update.signcast.co.kr");
//        initProps.put("UpdateFTPServerURL", "ftp://update.signcast.co.kr");
        initProps.put("UpdateFTPServerUsername", "scupdatemgr");
        initProps.put("UpdateFTPServerPassword", "welcome");
/*
        serverSSLEnabled = "true";
        backgroundImgFile = "";
        updateFtpServerUrl = "ftp://update.signcast.co.kr";
        updateFTPServerUsername = "scupdatemgr";
        updateFTPServerPassword = "welcome";
        updateInfoUrl = "http://update.signcast.co.kr/SignCastUpdate.xml";
        playerRestartThresholdPct = "80";
        screenAnalysisMins = "20";
*/
        mContext = c;
    }


    private static String getDicValue(String key)
    {
        if ((key==null)|| (key.isEmpty()))
        {
            return null;
        }

        if (properties.containsKey(key))
        {
            return properties.get(key);
        }

        return null;
    }
    private static String nodeText(String filename, String nodes)
    {
        File fXmlFile = new File(filename);
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
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Configuration");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node nNode = nList.item(temp);
            Log.d(TAG, "Current Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                NodeList chdnList = nNode.getChildNodes();
                if(chdnList!=null)
                {
                    for(int child = 0; child < chdnList.getLength(); child++)
                    {
                        Node childNode = chdnList.item(child);
                        if(childNode.getNodeType() != Node.TEXT_NODE) {
                            Element item = (Element) childNode;
                            //                        ((Element) childNode).getElementsByTagName(((Element) childNode).getTagName())
                            if (properties.containsKey(item.getTagName()))
                                properties.remove(item.getTagName());
                            properties.put(item.getTagName(), item.getTextContent());
                        }
                    }
                }
/*
                properties.put(mContext.getResources().getString(R.string.backgroundImgFile),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.backgroundImgFile)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.fTPSEnabled),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.fTPSEnabled)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.playerRestartThresholdPct),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.playerRestartThresholdPct)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.screenAnalysisMins),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.screenAnalysisMins)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.serverSSLEnabled),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.serverSSLEnabled)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.updateFTPServerURL),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.updateFTPServerURL)).item(0).getTextContent());
                properties.put(mContext.getResources().getString(R.string.updateInfoURL),
                        eElement.getElementsByTagName(mContext.getResources().getString(R.string.updateInfoURL)).item(0).getTextContent());
*/

            }
        }
        String value = properties.get(nodes);
        return value;
    }

    private static String configValue(String prefix, String node)
    {

        if ((node==null)|| (node.isEmpty()))
        {
            return null;
        }


        String storedValue = getDicValue(node);
        if (storedValue != null)
        {
            return storedValue;
        }

        String configFileName = FileUtils.BBMC_DIRECTORY+"CustomConfigData.xml";
        File file = new File(configFileName);
        if (file.exists())
        {
            try
            {
                String text = nodeText(configFileName, node);

                if (text == null)
                {
                    if (initProps.containsKey(node))
                    {
                        text = (String) initProps.get(node);
                    }
                    else
                    {
                        text = "";
                    }
                }

                return touchDicValue(node, text);
            }
            catch (Exception ex)
            {
                Utils.debugException("CustomConfigValue - node: " + node, ex);
            }
        }

        return null;
    }
    public static String configValue(String node, Context c)
    {
//        String lastPrefix = "//SignCast/Configuration/" + prefix;

        if ((node==null)|| (node.isEmpty()))
        {
            return null;
        }

//        String nodes = lastPrefix + node;

        String storedValue = getDicValue(node);
        if (storedValue != null)
        {
            return storedValue;
        }

//        String configFileName = System.IO.Path.Combine(System.IO.Path.Combine(System.IO.Path.GetDirectoryName(
//                Assembly.GetExecutingAssembly().Location), "Data"), "CustomConfigData.xml");
        String configFileName = FileUtils.BBMC_PAYCAST_DATA_DIRECTORY+"CustomConfigData.xml";
        File file = new File(configFileName);
        if(!file.exists()) {
            init(c);
            String value = (String) initProps.get(node);
            return value;
        }
        if (file.exists())
        {
            String text = nodeText(configFileName, node);

            if (text == null)
            {
                if (initProps.containsKey(node))
                {
                    try {
                        text = (String) initProps.get(node);
                    }
                    catch (Exception ex)
                    {
                        Utils.debugException("CustomConfigValue - node: " + node, ex);
                    }
                }
                else
                {
                    text = "";
                }
            }
            return touchDicValue(node, text);
        }
        return null;
    }
    public static String customConfigValue(String node)
    {
        return configValue("Edition/" + mContext.getString(R.string.editionCode) + "/", node);
    }
    private static String touchDicValue(String key, String value)
    {
        properties.put(key, value);
        return value;
    }
    public void onSaveConfigFile(HashMap<String, String> propDic,HashMap<String, String> edPropDic)
    {

        HashMap<String, String> filePropDic = new HashMap<String, String>();
        HashMap<String, String> fileEdPropDic = new HashMap<String, String>();

        String configFileName = FileUtils.BBMC_PAYCAST_DATA_DIRECTORY+"CustomConfigData.xml";

        filePropDic = readPropertiesFrFile(configFileName, "Configuration", filePropDic);
        fileEdPropDic = readPropertiesFrFile(configFileName, "Edition", fileEdPropDic);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));

        // 2. 스케줄 목록
        ArrayList<String> galleryFiles = new ArrayList<>();
        File fs = new File(configFileName);
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
//        xmlSerializer.startTag(null, "SignCast generated=\""+nowTimeStr+"\"");
        try {
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Configuration");
            xmlSerializer.text("\n");
/*
            Iterator<Integer> iter = map.keySet().iterator();

            while(iter.hasNext()) {

                int key = iter.next();

                String value = map.get(key);

                Log.d("fureun", "key : " + key + ", value : " + value);

            }
*/

            if(propDic!=null && propDic.size()>0)
            {
                Iterator<String> iter = propDic.keySet().iterator();

                while(iter.hasNext()) {

                    String key = iter.next();

                    String value = propDic.get(key);
                    if(filePropDic.containsKey(key)) {
                        filePropDic.remove(key);
                        filePropDic.put(key, value);
                    }
                    else
                    {
                        filePropDic.put(key, value);
                    }
                }
            }

            if(filePropDic!=null && filePropDic.size()>0)
            {
                Iterator<String> iter = filePropDic.keySet().iterator();

                while(iter.hasNext()) {

                    String key = iter.next();

                    String value = filePropDic.get(key);

                    xmlSerializer.startTag(null, key);
                    xmlSerializer.text(value);
                    xmlSerializer.endTag(null, key);
                    xmlSerializer.text("\n");
                }
            }
            xmlSerializer.startTag(null, "Edition");

            if(edPropDic!=null && edPropDic.size()>0)
            {
                Iterator<String> iter = edPropDic.keySet().iterator();

                while(iter.hasNext()) {

                    String key = iter.next();

                    String value = edPropDic.get(key);
                    if((fileEdPropDic.size() > 0)&&fileEdPropDic.containsKey(key)) {
                        fileEdPropDic.remove(key);
                        fileEdPropDic.put(key, value);
                    }
                    else
                    {
                        fileEdPropDic.put(key, value);
                    }
                }
            }
            if(fileEdPropDic!=null && fileEdPropDic.size() > 0)
            {
                Iterator<String> iter = fileEdPropDic.keySet().iterator();

                while(iter.hasNext()) {

                    String key = iter.next();

                    String value = fileEdPropDic.get(key);

                    xmlSerializer.startTag(null, key);
                    xmlSerializer.text(value);
                    xmlSerializer.endTag(null, key);
                    xmlSerializer.text("\n");
                }
            }
            xmlSerializer.endTag(null, "Edition");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Configuration");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private HashMap<String, String> readPropertiesFrFile(String filename, String tagname, HashMap<String, String> hashMap)
    {
        File fXmlFile = new File(filename);
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
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(tagname);

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node nNode = nList.item(temp);
            Log.d(TAG, "Current Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                NodeList chdnList = nNode.getChildNodes();
                if(chdnList!=null)
                {
                    for(int child = 0; child < chdnList.getLength(); child++)
                    {
                        Node childNode = chdnList.item(child);
                        if(childNode.getNodeType() != Node.TEXT_NODE) {
                            Element eElement = (Element) childNode;
                            String key = eElement.getTagName();
                            String val = eElement.getTextContent();
                            if((key!=null)&&!key.isEmpty()) {
                                if((val!=null)&&!val.isEmpty())
                                    hashMap.put(key, val);
                            }
                        }
                    }
                }
            }
        }
        return hashMap;
    }
}
