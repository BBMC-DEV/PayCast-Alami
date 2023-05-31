package kr.co.bbmc.paycastdid.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.bbmc.paycastdid.R;
import kr.co.bbmc.paycastdid.model.OrderListItem;
import kr.co.bbmc.paycastdid.model.OrderMenuItem;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static String TAG ="RecyclerViewAdapter";
    private static int MAX_CARD_VIEW = 3;
    private Context mContext;
    protected ItemListener mListener;
    public List<OrderListItem> mValues;
    private List<ViewHolder> mViewList = new ArrayList<ViewHolder>();


    public interface ItemListener {
        //void onItemClick(View v);
        //void onTimeOutCb(Drawable bg);
        int onSetAnimation(String pos, String date, long diff);
        //void onClearAnimation(int pos);
    }
    public ViewHolder getViewHold(int pos)
    {
        ViewHolder h = null;

        if((mViewList!=null)&&(mViewList.size()>0)) {
            if(pos >= mViewList.size())
            {
                Log.e(TAG, "getViewHold() pos="+pos);
            }
            else
               h = mViewList.get(pos);
        }
        return h;
    }
    public ViewHolder getViewHold(String ordernum)
    {
        ViewHolder h = null;

        if((mViewList!=null)&&(mViewList.size()>0)) {
            for (int i = 0; i < mViewList.size(); i++) {
                ViewHolder vh = mViewList.get(i);
                String num = vh.textView.getText().toString();
                if (num.equalsIgnoreCase(ordernum))
                    return vh;
            }
        }
        return h;
    }

    public int getViewHoldCount()
    {
        if(mViewList!=null)
            return mViewList.size();
        return 0;
    }

    public RecyclerViewAdapter(Context context, List<OrderListItem> list, ItemListener itemListener) {

        mContext = context;
        mListener=itemListener;
        mValues = list;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder()");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( RecyclerViewAdapter.ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder()");
        if(mViewList==null)
            mViewList = new ArrayList<>();
        mViewList.add(holder);

        if(mValues.size()> position) {
            holder.setData(mValues.get(position), position);
        }
        else {
            Log.d(TAG, "onBindViewHolder() Error!!");
            return;
        }

/*
        OrderListItem item = mValues.get(position);
        holder.onBlinkAnimationTimer(item.alarm_date, position);
*/
        holder.position = position;

    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(mValues.size()>holder.position)
            holder.onShowLimitCardView(holder.position);
/*
        Log.d(TAG, "onViewAttachedToWindow()");
        Log.d(TAG, "ALARM seq onViewAttachedToWindow() mValues.size()="+mValues.size()+" pos="+holder.position);
        if(mValues.size()>holder.position) {
            OrderListItem item = mValues.get(holder.position);
            Log.d(TAG, "ALARM seq onViewAttachedToWindow() seq="+item.order_num+" updateDate="+item.updateDate);
            holder.onBlinkAnimationTimer(item.updateDate, item.order_num);
        }
        else
        {
            Log.e(TAG, "onViewAttachedToWindow() ERR mValues.size()="+mValues.size()+" holder.pos="+holder.position);
        }
*/
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d(TAG, "onViewDetachedFromWindow()");
        if((mViewList!=null)&&(mViewList.size()>0)) {
            mViewList.remove(holder);
        }
    }

    @Override
    public int getItemCount() {
        if((mValues==null)||(mValues.size()==0))
            return 0;
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder  {
        int position;
        CardView cardView;
        public TextView textView;
        ListView listView;
        ListViewAdapter adapter;
        public LinearLayout recyclerLayout;
        public View mViewholder;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            recyclerLayout = (LinearLayout) itemView.findViewById(R.id.recycler_Layout);
            textView = (TextView) itemView.findViewById(R.id.order_num_id);
            listView = (ListView) itemView.findViewById(R.id.order_list_id);
            mViewholder = textView;

            // Adapter 생성
            adapter = new ListViewAdapter() ;
            listView.setAdapter(adapter);
            listView.setFastScrollEnabled(false);
        }

        public void setBgDrawable(int resid) {
            recyclerLayout.setBackgroundResource(resid);
        }
        public void setData(OrderListItem list, int pos) {
            position = pos;
            textView.setText(list.order_num);
            List<OrderMenuItem> orderlist = list.menulist;
            boolean addFlag = true;

            int itemCount = adapter.getCount();
            if(itemCount > 0)
                adapter.removeAllItem();

            onBlinkAnimationTimer(list.updateDate, list.order_num);
            for(int i = 0; i<orderlist.size(); i++)
            {
                OrderMenuItem item = orderlist.get(i);
                int count = adapter.getCount();
                if(!item.orderSeq.equalsIgnoreCase(list.order_num)) {
                    addFlag = false;
                }
                else {
                    addFlag = true;
                    for (int menuIdx = 0; menuIdx < count; menuIdx++) {
                        OrderMenuItem tempItem = (OrderMenuItem) adapter.getMenuItem(menuIdx);
                        if (tempItem.orderSeq.equalsIgnoreCase(list.order_num)) {
                            if (item.menu_name.equalsIgnoreCase(tempItem.menu_name)) {
                                if (item.menu_count.equalsIgnoreCase(tempItem.menu_count)) {
                                    {
                                        addFlag = false;
                                        break;
                                    }
                                }
                                else if(item.orderMenuNoti.equalsIgnoreCase("N")) {
                                    addFlag = false;
                                    break;
                                }
                            }
                            else if(item.orderMenuNoti.equalsIgnoreCase("N")) {
                                addFlag = false;
                                break;
                            }
                        }
                    }
                }
                if(addFlag) {
                    adapter.addItem(list.order_num, item.menu_name, item.menu_count, item.orderMenuNoti);
                }
            }
        }
        private void onShowLimitCardView( int pos)
        {
            if(pos > MAX_CARD_VIEW)
            {
                cardView.setVisibility(View.INVISIBLE);
            }
            else
                cardView.setVisibility(View.VISIBLE);
        }
        private void onBlinkAnimationTimer(String time, String order)
        {
            long now = System.currentTimeMillis();
            Date nowDate = new Date(now);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.S");

            if((time==null)||(time.isEmpty())) {
                Log.e(TAG, "ALARM seq onBlinkAnimationTimer() time null pos="+order);
                return;
            }
            Date alarmDate = null;
            try {
                alarmDate = dateFormat.parse(time);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = (nowDate.getTime()-alarmDate.getTime());
/*
            Log.e(TAG,"ALARM seq OnBlinkTimerTask() onBlinkAnimationTimer() nowDate.getTime()="+nowDate.getTime());
            Log.e(TAG,"ALARM seq OnBlinkTimerTask() onBlinkAnimationTimer() alarmDate.getTime()="+alarmDate.getTime());
            Log.e(TAG,"ALARM seq OnBlinkTimerTask() onBlinkAnimationTimer() diff="+diff);
*/
            if(diff < 50000)
            {
                Log.e(TAG,"ALARM seq OnBlinkTimerTask() ok order="+order);
                if(diff <0) {
                    Log.e(TAG,"ALARM seq OnBlinkTimerTask() ??? nowDate="+dateFormat.format(nowDate)+" alaramDate="+time);
                    mListener.onSetAnimation(order, time, 60000);
                }
                else
                    mListener.onSetAnimation(order, time, diff);
            }
            else {
                Log.e(TAG,"ALARM seq OnBlinkTimerTask() nowDate="+dateFormat.format(nowDate)+" alaramDate="+time);
                Log.e(TAG, "ALARM seq OnBlinkTimerTask() no order=" + order +" diff="+diff);
                textView.setTextColor(Color.BLACK);
                setBgDrawable(R.drawable.bg_white_line);
            }

        }
    }

}
