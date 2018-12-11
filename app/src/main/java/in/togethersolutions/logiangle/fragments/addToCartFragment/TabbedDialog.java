package in.togethersolutions.logiangle.fragments.addToCartFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.togethersolutions.logiangle.R;

public class TabbedDialog  extends DialogFragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    String orderNumber;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.popup_add_to_cart,container,false);
        if (getArguments() != null) {
            orderNumber = getArguments().getString("OrderNumber","");

           // body = getArguments().getString("body","");
        }
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setLayout(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.masterViewPager);
        getDialog().getWindow().setLayout(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
        CustomAdapter adapter = new CustomAdapter(getChildFragmentManager());
        adapter.addFragment("Add product items", ProductWiseItemFragment.createInstance(orderNumber));
        adapter.addFragment("Add non product items", NonProductWiseItemFragment.createInstance(orderNumber));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
