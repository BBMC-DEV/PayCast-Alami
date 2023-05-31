package kr.co.bbmc.selforderutil;

import java.util.ArrayList;

public class KioskOrderInfoForPrint {
    public String goodsAmt;
    public String orderDate;
    public String orderNumber;
    public String recommandId;
    public String storeName;
    public ArrayList<OrderMenuItem> menuItems = new ArrayList<OrderMenuItem>();

    public static class OrderMenuItem {
        public String orderPrice;
        public String productName;
        public String productId;
        public String orderCount;
    }
    public boolean printOk = false;
}
