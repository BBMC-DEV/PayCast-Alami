package kr.co.bbmc.paycastdid;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

public class AutoFitGridLayoutManager extends GridLayoutManager {
    private static String TAG ="AutoFitGridLayoutManager";
    private int columnWidth;
    private boolean columnWidthChanged = true;
    private int spanCount = 1;

    public AutoFitGridLayoutManager(Context context, int columnWidth) {
        super(context, 1);

//        setColumnWidth(columnWidth);
        setColumnCount(columnWidth);
    }

    public void setColumnCount(int newColumnCount) {
        if (newColumnCount > 0 && spanCount != newColumnCount) {
            spanCount = newColumnCount;
            columnWidthChanged = true;
        }
    }

    public void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth;
            columnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
/*
        if (columnWidthChanged && columnWidth > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / columnWidth);
            setSpanCount(spanCount);
            columnWidthChanged = false;
        }
*/

        if (columnWidthChanged && spanCount > 0) {
            setSpanCount(spanCount);
            columnWidthChanged = false;
        }
        Log.d(TAG, "spanCount="+spanCount+" state="+state);
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public void setSpanCount(int spanCount) {
        super.setSpanCount(spanCount);
    }
}
