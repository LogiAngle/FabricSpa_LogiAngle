package in.togethersolutions.logiangle.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments.Tab1Info;
import in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments.Tab2Items;
import in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments.Tab3POD;
import in.togethersolutions.logiangle.modals.OrderNumberForHistory;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderHistoryInformation extends AppCompatActivity {

    String userName = null;
    String orderNumber = null;
    OrderNumberForHistory orderNumberForHistory;
    ProgressBar progressBar;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.getChildAt(1).setClickable(false);

        inItComponent();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void inItComponent() {
        userName= SessionManagement.getLoggedInUserName(this);
        orderNumber = getIntent().getStringExtra("orderNumber");
        orderNumberForHistory = new OrderNumberForHistory();
        orderNumberForHistory.setOrderNumber(orderNumber);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_history_information, menu);
        return true;
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    Tab1Info tab1Info = new Tab1Info();
                    tab1Info.setOrderNumber(orderNumber);
                    tab1Info.setUserName(userName);
                    return tab1Info;
                case 1:
                    Tab2Items tab2Items = new Tab2Items();
                    tab2Items.setOrderNumber(orderNumber);
                    tab2Items.setUserName(userName);
                    return tab2Items;
                case 2:
                    Tab3POD tab3POD = new Tab3POD();
                    tab3POD.setOrderNumber(orderNumber);
                    tab3POD.setUserName(userName);
                    return tab3POD;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Items";
                case 2:
                    return "POD";
            }
            return null;
        }
    }

}

