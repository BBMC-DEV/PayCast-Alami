package kr.co.bbmc.paycastdid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingEnvPersister {
    private static String TAG ="SettingEnvPersister";
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;
    private static Context mContext;

    private static final String FCM_MSG_RECEIVED = "pref_key_fcm_message_receive";
    private static final String DID_ORDERLIST = "pref_key_did_order_list";
    private static final String DID_ALARM_ORDERLIST = "pref_key_did_alarm_order_list";


    private SettingEnvPersister() {

    }

    public static void initPrefs(Context context) {
        if( mPrefs == null){
            mPrefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            mPrefsEditor = mPrefs.edit();
        }

    }

    /**
     *  Received FCM Message
     * @return
     */
    public static boolean getSettingFcmMsgReceived() {
        // false : not received
        // true : received
        return mPrefs.getBoolean(FCM_MSG_RECEIVED, false);
    }
    /**
     *  Received FCM Message
     * @return
     */
    public static void setSettingFcmMsgReived(boolean fcmMsgReived) {
        // false : not received
        // true : received
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean(FCM_MSG_RECEIVED, fcmMsgReived);
        mPrefsEditor.commit();

    }

    /**
     *  get order list
     * @return
     */
    public static List<OrderListItem> getDidOrderList() {
        String s = mPrefs.getString(DID_ORDERLIST, "");
        if((s==null)||(s.isEmpty()))
            return null;
        List<OrderListItem> list = new ArrayList<OrderListItem>();
        JSONArray listjson = null;
        try {
            listjson = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if((listjson==null)||(listjson.length()==0))
            return null;
        for (int i = 0; i <listjson.length(); i++) {
            JSONObject listItemj = null;
            try {
                listItemj = listjson.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OrderListItem listItem = new OrderListItem();
            try {
                listItem.updateDate = (String)listItemj.get("updateDate");
                if(listItem.updateDate.isEmpty()) {
                    Log.e(TAG, "getDidOrderList() updateDate emptry!!!!!!");
                }
                listItem.order_num = (String)listItemj.get("order_num");
                listItem.alarm_date = (String)listItemj.get("alarm_date");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listItem.menulist = new ArrayList<OrderMenuItem>();
            String menulistjson = null;
            try {
                menulistjson = (String)listItemj.get("menulist");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray json = null;
            if(menulistjson==null)
                return null;
            try {
                json = new JSONArray(menulistjson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if((json==null)||(json.length()==0))
                return null;
            for (int menucount = 0; menucount <json.length(); menucount++) {
                OrderMenuItem menuItem = new OrderMenuItem();
                JSONObject j = null;
                try {
                    j = json.getJSONObject(menucount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.orderSeq = (String)j.get("orderSeq");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.menu_name = (String)j.get("menu_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.menu_count = (String)j.get("menu_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.orderMenuNoti = (String)j.get("orderMenuNoti");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listItem.menulist.add(menuItem);
            }
            list.add(listItem);
        }
        return list;
    }
    /**
     *  save order list items
     * @return
     */
    public static void setDidOrderList(List<OrderListItem> list) {
        mPrefsEditor = mPrefs.edit();
        JSONArray listjson = new JSONArray();

        for(int i = 0; i<list.size();i++)
        {
            JSONObject listItemj = new JSONObject();
            OrderListItem listItem = list.get(i);
            try {
                listItemj.put("updateDate", listItem.updateDate);
                if(listItem.updateDate.isEmpty()) {
                    Log.e(TAG, "setDidOrderList() updateDate emptry!!!!!!");
                }
                listItemj.put("order_num", listItem.order_num);
                listItemj.put("alarm_date", listItem.alarm_date);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<OrderMenuItem> menuList = listItem.menulist;
            if(menuList.size() > 0) {
                JSONArray json = new JSONArray();

                for (int menucout = 0; menucout < menuList.size(); menucout++) {
                    OrderMenuItem menuitem = menuList.get(menucout);
                    JSONObject j = new JSONObject();
                    try {
                        j.put("orderSeq", menuitem.orderSeq);
                        j.put("menu_name", menuitem.menu_name);
                        j.put("menu_count", menuitem.menu_count);
                        j.put("orderMenuNoti", menuitem.orderMenuNoti);
                        json.put(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    listItemj.put("menulist", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            listjson.put(listItemj);

        }
        if(list.size()>0)
            mPrefsEditor.putString(DID_ORDERLIST, listjson.toString());
        else
            mPrefsEditor.putString(DID_ORDERLIST, "");
        mPrefsEditor.apply();

    }


    /**
     *  get order list
     * @return
     */
    public static List<DidAlarmData > getDidAlarmOrderList() {
        String s = mPrefs.getString(DID_ALARM_ORDERLIST, "");
        if((s==null)||(s.isEmpty()))
            return null;
        List<DidAlarmData > list = new ArrayList<DidAlarmData >();
        JSONArray listjson = null;
        try {
            listjson = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if((listjson==null)||(listjson.length()==0))
            return null;
        for (int i = 0; i <listjson.length(); i++) {
            JSONObject listItemj = null;
            try {
                listItemj = listjson.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DidAlarmData  listItem = new DidAlarmData ();
            try {
                listItem.updateDate = (String)listItemj.get("updateDate");
                if(listItem.updateDate.isEmpty()) {
                    Log.e(TAG, "getDidAlarmOrderList() updateDate emptry!!!!!!");
                }
                listItem.orderSeq = (String)listItemj.get("orderSeq");

                listItem.cookAlarmId = (String)listItemj.get("cookAlarmId");
                listItem.orderSeq = (String)listItemj.get("orderSeq");
                listItem.menuCount = (String)listItemj.get("menuCount");
                listItem.alarmDate = (String)listItemj.get("alarmDate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listItem.menuList = new ArrayList<DidAlarmMenuData>();
            String menulistjson = null;
            try {
                menulistjson = (String)listItemj.get("menuList");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray json = null;
            if(menulistjson==null)
                return null;
            try {
                json = new JSONArray(menulistjson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if((json==null)||(json.length()==0))
                return null;
            for (int menucount = 0; menucount <json.length(); menucount++) {
                DidAlarmMenuData menuItem = new DidAlarmMenuData();
                JSONObject j = null;
                try {
                    j = json.getJSONObject(menucount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.orderMenuNoti = (String)j.get("orderMenuNoti");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.orderPacking = (String)j.get("orderPacking");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.count = (String)j.get("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.name = (String)j.get("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    menuItem.menuNewAlarm = (String)j.get("menuNewAlarm");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listItem.menuList.add(menuItem);
            }
            list.add(listItem);
        }
        return list;
    }
    /**
     *  save order list items
     * @return
     */
    public static void setDidAlarmOrderList(List<DidAlarmData > list) {
        mPrefsEditor = mPrefs.edit();
        JSONArray listjson = new JSONArray();

        for(int i = 0; i<list.size();i++)
        {
            JSONObject listItemj = new JSONObject();
            DidAlarmData listItem = list.get(i);
            try {
                listItemj.put("updateDate", listItem.updateDate);
                if(listItem.updateDate.isEmpty()) {
                    Log.e(TAG, "setDidAlarmOrderList() updateDate emptry!!!!!!");
                }
                listItemj.put("orderSeq", listItem.orderSeq);
                listItemj.put("menuCount", listItem.menuCount);
                listItemj.put("cookAlarmId", listItem.cookAlarmId);
                listItemj.put("alarmDate", listItem.alarmDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<DidAlarmMenuData> menuList = listItem.menuList;
            if(menuList.size() > 0) {
                JSONArray json = new JSONArray();

                for (int menucout = 0; menucout < menuList.size(); menucout++) {
                    DidAlarmMenuData menuitem = menuList.get(menucout);
                    JSONObject j = new JSONObject();
                    try {
                        j.put("orderMenuNoti", menuitem.orderMenuNoti);
                        j.put("orderPacking", menuitem.orderPacking);
                        j.put("count", menuitem.count);
                        j.put("name", menuitem.name);
                        j.put("menuNewAlarm", menuitem.menuNewAlarm);
                        json.put(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    listItemj.put("menuList", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            listjson.put(listItemj);

        }
        if(list.size()>0)
            mPrefsEditor.putString(DID_ALARM_ORDERLIST, listjson.toString());
        else
            mPrefsEditor.putString(DID_ALARM_ORDERLIST, "");
        mPrefsEditor.apply();

    }

}
