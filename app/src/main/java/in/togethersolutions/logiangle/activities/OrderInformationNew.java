package in.togethersolutions.logiangle.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.fragments.orderInfoFragments.OrderCashInfo;
import in.togethersolutions.logiangle.fragments.orderInfoFragments.OrderItemInfo;
import in.togethersolutions.logiangle.fragments.orderInfoFragments.OrderPODInfo;
import in.togethersolutions.logiangle.session.SessionManagement;


public class OrderInformationNew extends AppCompatActivity {
    String userName;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    ProgressBar progressBar;
    String orderNumber;
    int selectViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information_new);
        userName = SessionManagement.getLoggedInUserName(OrderInformationNew.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(viewPagerAdapter);

        orderNumber = getIntent().getStringExtra("orderNumber");
        selectViewPager = getIntent().getIntExtra("toggal1",0);
        viewPager.setCurrentItem(selectViewPager);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.getChildAt(0).setClickable(false);
        tabStrip.getChildAt(2).setClickable(false);


        inItComponent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void inItComponent() {
        userName = SessionManagement.getLoggedInUserName(OrderInformationNew.this);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    OrderItemInfo orderItemInfo = new OrderItemInfo();

                    return orderItemInfo;
                case 1:
                    OrderPODInfo orderPODInfo = new OrderPODInfo();
                    return orderPODInfo;
                case 2:
                    OrderCashInfo orderCashInfo = new OrderCashInfo();
                    return orderCashInfo;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Items";
                case 1:
                    return "POD";
                case 2:
                    return "Cash";
            }
            return null;
        }

    }

}
