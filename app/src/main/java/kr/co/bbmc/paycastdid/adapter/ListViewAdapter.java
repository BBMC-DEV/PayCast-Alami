package kr.co.bbmc.paycastdid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.bbmc.paycastdid.R;
import kr.co.bbmc.paycastdid.model.OrderMenuItem;

public class ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<OrderMenuItem> listViewItemList = new ArrayList<OrderMenuItem>() ;

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        if(listViewItemList==null)
            return null;
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final int pos = position;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, viewGroup, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView menuTextView = (TextView) convertView.findViewById(R.id.id_menu_name) ;
        TextView menuCountTextView = (TextView) convertView.findViewById(R.id.id_menu_count) ;
        menuTextView.setSelected(true);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        OrderMenuItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(listViewItem.orderMenuNoti.equalsIgnoreCase("Y")) {
            menuTextView.setText(listViewItem.menu_name);
            menuCountTextView.setText(listViewItem.menu_count);
        }
        return convertView;
    }
    public void addItem(String orderseq, String menu, String count, String noti)
    {
        if(noti.equalsIgnoreCase("Y")) {
            OrderMenuItem item = new OrderMenuItem();
            item.orderSeq = orderseq;
            item.menu_name = menu;
            item.menu_count = count;
            item.orderMenuNoti = noti;
            listViewItemList.add(item);
        }
    }
    public OrderMenuItem getMenuItem(int index)
    {
        OrderMenuItem item = listViewItemList.get(index);
        return item;
    }
    public void removeItem(OrderMenuItem item)
    {
        listViewItemList.remove(item);
    }
    public void removeAllItem()
    {
        listViewItemList.clear();
        listViewItemList = new ArrayList<OrderMenuItem>() ;
    }
}
