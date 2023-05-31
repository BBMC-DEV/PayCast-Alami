package kr.co.bbmc.selforderutil;

public class DataModel {

    public String productId;
    public String text;
    public String drawable;
    public String color;
    public String itemprice;
    public String price;
    public int count = 1;
    public boolean popular = false;
    public boolean newmenu = false;

    public DataModel(String pId, String t, String d, String c, String p , boolean pop, boolean nm)
    {
        productId = pId;
        text=t;
        drawable=d;
        color=c;
        itemprice = p;
        count = 1;
        popular = pop;
        newmenu = nm;
    }
    public DataModel(String pId, String t, String d, String c, String p )
    {
        productId = pId;
        text=t;
        drawable=d;
        color=c;
        itemprice = p;
        count = 1;
        popular = false;
        newmenu = false;
    }

    public DataModel(String pId, String t, String d, String c, String p, int itemcount )
    {
        productId = pId;
        text=t;
        drawable=d;
        color=c;
        itemprice = p;
        count = itemcount;
        popular = false;
        newmenu = false;
    }
    public DataModel(String pId, String t, String d, String c, String p, int itemcount, boolean pop, boolean nm )
    {
        productId = pId;
        text=t;
        drawable=d;
        color=c;
        itemprice = p;
        count = itemcount;
        popular = pop;
        newmenu = nm;
    }
}
