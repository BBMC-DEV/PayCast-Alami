package kr.co.bbmc.paycastdid;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import android.view.View;

import java.util.List;

public class MenuPagerAdapter extends PagerAdapter {

    Context context;
    DidExternalVarApp didExternalVarApp;

    public MenuPagerAdapter(FragmentManager supportFragmentManager, DidExternalVarApp didApp, Context c) {
        didExternalVarApp = didApp;
        context = c;
    }

    @Override
    public int getCount() {
        List<OrderListItem> menuList = didExternalVarApp.getMenuObject();
        if(menuList==null)
            return 0;
        return menuList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        boolean result = (view==object);
        return result;
    }
/*
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lst_items);

        return super.instantiateItem(container, position);
    }
*/
}
