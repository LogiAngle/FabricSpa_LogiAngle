package in.togethersolutions.logiangle.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;

import java.util.List;

import in.togethersolutions.logiangle.modals.OrderMapDAO;

public class MapPagerAdapter extends FragmentPagerAdapter {
    LayoutInflater layoutInflater;
    List<OrderMapDAO> myDealsList;
    Context mContext;
    public MapPagerAdapter(List<OrderMapDAO> myDealsList, FragmentManager supportFragmentManager, Context context) {
        super(supportFragmentManager);
        layoutInflater = LayoutInflater.from(context);
        this.myDealsList = myDealsList;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new MapAdapter(position,myDealsList.get(position));
    }

    @Override
    public int getCount() {
        return this.myDealsList.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.95f;
    }
}
