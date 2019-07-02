package com.loanhome.lib.util;

import org.json.JSONException;
import org.json.JSONObject;

public class Global {

    public static Global mInfo;

    public static String pheadjson;
    public static String appKey ;
    public static String moxieKey ;

    public static String uuid;

    public static boolean IsTestVersion = true;	//是否测试版本，true为测试服务器

    public static boolean IsDebug = true;	//是否debug模式，true为Debug模式

    public static boolean IS_ENABLE_LOG = true;   //是否打开log输出, true为打开log输出


    /**
     * pversion : 30
     * phoneid : 1fbd042af94cbecd
     * aid : 1fbd042af94cbecd
     * imei : 869336023883984
     * cversionname : 1.8
     * channel :
     * lang : zh_cn
     * imsi : 46011
     * dpi : 1920*1080
     * sys : 8.1.0-27
     * net : WIFI
     * cversion : 19
     * phone : Mi4c
     * platform : android
     * before_channel :
     * activity_id :
     * account_global_new_user :
     * account_product_new_user :
     * from_id :
     * device_global__new_user :
     * device_product_new_user :
     * access_token : 4892B01EB17918C8F2DFCFE6317DF576
     * cityid : 440100
     * gcityid : 440100
     * lng : 113.326339
     * lat : 23.146984
     * prdid : 8202
     */

    private String pversion;
    private String phoneid;
    private String aid;
    private String imei;
    private String cversionname;
    private String channel;
    private String lang;
    private String imsi;
    private String dpi;
    private String sys;
    private String net;
    private int cversion;
    private String phone;
    private String platform;
    private int before_channel;
    private String activity_id;
    private boolean account_global_new_user;
    private boolean account_product_new_user;
    private String from_id;
    private boolean device_global__new_user;
    private boolean device_product_new_user;
    private String access_token;
    private String cityid;
    private String gcityid;
    private long lng;
    private long lat;
    private String prdid;

    public String getPversion() {
        return pversion;
    }

    public void setPversion(String pversion) {
        this.pversion = pversion;
    }

    public String getPhoneid() {
        return phoneid;
    }

    public void setPhoneid(String phoneid) {
        this.phoneid = phoneid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCversionname() {
        return cversionname;
    }

    public void setCversionname(String cversionname) {
        this.cversionname = cversionname;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public int getCversion() {
        return cversion;
    }

    public void setCversion(int cversion) {
        this.cversion = cversion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getBefore_channel() {
        return before_channel;
    }

    public void setBefore_channel(int before_channel) {
        this.before_channel = before_channel;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public boolean isAccount_global_new_user() {
        return account_global_new_user;
    }

    public void setAccount_global_new_user(boolean account_global_new_user) {
        this.account_global_new_user = account_global_new_user;
    }

    public boolean isAccount_product_new_user() {
        return account_product_new_user;
    }

    public void setAccount_product_new_user(boolean account_product_new_user) {
        this.account_product_new_user = account_product_new_user;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public boolean isDevice_global__new_user() {
        return device_global__new_user;
    }

    public void setDevice_global__new_user(boolean device_global__new_user) {
        this.device_global__new_user = device_global__new_user;
    }

    public boolean isDevice_product_new_user() {
        return device_product_new_user;
    }

    public void setDevice_product_new_user(boolean device_product_new_user) {
        this.device_product_new_user = device_product_new_user;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getGcityid() {
        return gcityid;
    }

    public void setGcityid(String gcityid) {
        this.gcityid = gcityid;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public String getPrdid() {
        return prdid;
    }

    public void setPrdid(String prdid) {
        this.prdid = prdid;
    }

    /**
     * 把accountProductInfo转换成jsonobject的方法
     * @param global
     * @return
     */
    public static JSONObject writeProductInfoToJSON(Global global) {
        JSONObject json = new JSONObject();
        try {
            json.put("pversion", global.getPversion());
            json.put("phoneid", global.getPhoneid());
            json.put("aid", global.getAid());
            json.put("imei", global.getImei());
            json.put("cversionname", global.getCversionname());
            json.put("channel", global.getChannel());
            json.put("lang", global.getLang());
            json.put("imsi", global.getImei());
            json.put("dpi", global.getDpi());
            json.put("sys", global.getSys());
            json.put("net", global.getNet());
            json.put("cversion", global.getCversion());
            json.put("phone", global.getPhone());
            json.put("platform", global.getPlatform());
            json.put("before_channel", global.getBefore_channel());
            json.put("activity_id", global.getActivity_id());
            json.put("account_global_new_user", global.isAccount_global_new_user());
            json.put("account_product_new_user", global.isAccount_product_new_user());
            json.put("from_id", global.getFrom_id());
            json.put("device_global__new_user", global.isDevice_global__new_user());
            json.put("device_product_new_user", global.isDevice_product_new_user());
            json.put("access_token", global.getAccess_token());
            json.put("cityid", global.getCityid());
            json.put("gcityid", global.getGcityid());
            json.put("lng", global.getLng());
            json.put("lat", global.getLat());
            json.put("prdid", global.getPrdid());
//            json.put("lat", global.getPversion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 从jsonobject解释accountProductInfo的方法
     *
     * @param json
     * @return
     */
    public static Global parseFromJSONObject(JSONObject json) {
        if (json == null) {
            return null;
        }
        Global global = new Global();
        global.setPversion(json.optString("pversion"));
        global.setPhoneid(json.optString("phoneid"));
        global.setAid(json.optString("aid"));
        global.setImei(json.optString("imei"));
        global.setCversionname(json.optString("cversionname"));
        global.setChannel(json.optString("channel"));
        global.setLang(json.optString("lang"));
        global.setImsi(json.optString("imsi"));
        global.setDpi(json.optString("dpi"));
        global.setSys(json.optString("sys"));
        global.setNet(json.optString("net"));
        global.setCversion(json.optInt("cversion"));
        global.setPhone(json.optString("phone"));
        global.setPlatform(json.optString("platform"));
        global.setBefore_channel(json.optInt("before_channel"));
        global.setActivity_id(json.optString("activity_id"));
        global.setAccount_global_new_user(json.optBoolean("account_global_new_user"));
        global.setAccount_product_new_user(json.optBoolean("account_product_new_user"));
        global.setFrom_id(json.optString("from_id"));
        global.setDevice_global__new_user(json.optBoolean("device_global__new_user"));
        global.setDevice_product_new_user(json.optBoolean("device_product_new_user"));
        global.setAccess_token(json.optString("access_token"));
        global.setCityid(json.optString("cityid"));
        global.setGcityid(json.optString("gcityid"));
        global.setLng(json.optLong("lng"));
        global.setLat(json.optLong("lat"));
        global.setPrdid(json.optString("prdid"));

        mInfo = global ;
//        setGlobalInfo(global);

        return mInfo;
    }

//    public static void setGlobalInfo(Global globalInfo){
//        mInfo = globalInfo;
//    }
    public static Global getGlobalInfo() {
        return mInfo;
    }
}
