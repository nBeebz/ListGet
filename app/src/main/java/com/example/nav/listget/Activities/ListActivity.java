package com.example.nav.listget.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.nav.listget.Adapters.OwnedListAdapter;
import com.example.nav.listget.Adapters.SharedListAdapter;
import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.Mongo;
import com.example.nav.listget.MyDialogFragment;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.ArrayList;
import java.util.Locale;


public class ListActivity extends Activity implements ActionBar.TabListener {

    private static String email;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        email = getIntent().getStringExtra( "email" );
    }

    public  FragmentPagerAdapter getSectionsPagerAdapter(){
        return mSectionsPagerAdapter;
    }

    public  ViewPager getViewPager(){
        return mViewPager;
    }

    /**
     * get list fragment by position
     * @param position
     * @return
     */
    public Fragment findFragmentByPosition(int position) {
        return this.getFragmentManager().findFragmentByTag(
                "android:switcher:" + getViewPager().getId() + ":"
                        + mSectionsPagerAdapter.getItemId(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
        public ListFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0: return new OwnedListsFragment();
                case 1: return new SharedListsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.list_owned).toUpperCase(l);
                case 1:
                    return getString(R.string.list_shared).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class OwnedListsFragment extends ListFragment implements MongoInterface {
        private ArrayList<ListObject> lists;
        private OwnedListAdapter adapter;
        private LayoutInflater inf;
        SwipeRefreshLayout swipeLayout;

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Intent myIntent = new Intent( getActivity() , ItemActivity.class );
            myIntent.putExtra( "list", lists.get(position) );
            myIntent.putExtra("userid",email);
            startActivity( myIntent );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.activity_list, container, false);
            inf = inflater;
            swipeLayout = new ListFragmentSwipeRefreshLayout( container.getContext(), this );
            swipeLayout.addView( view );
            swipeLayout.setColorSchemeResources( R.color.bg_color, R.color.grey, R.color.txt_color, R.color.btn_blue);
            swipeLayout.setOnRefreshListener( new ListRefreshListener( this, Mongo.KEY_OWNER, swipeLayout ));

            // set onclick listener for add list button
            LinearLayout btn = (LinearLayout) view.findViewById(R.id.LinearLayout);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ListObject selectedCat = new ListObject( "", email);
                    MyDialogFragment dialog = new MyDialogFragment();
                    dialog.setCategory(selectedCat);
                    dialog.setOnCloseListener(new onMyClickListener());
                    dialog.show(getActivity().getFragmentManager(), "");
                }
            });
            Mongo.getMongo( this ).get( Mongo.COLL_LISTS, Mongo.KEY_OWNER, email );
            swipeLayout.setRefreshing( true );
            return swipeLayout;
        }

        /**
         * listener for　fragmentDialog.
         * listens action from fragment dialog and does stuff in this fragment
         */
        public class onMyClickListener implements MyDialogFragment.OnMyClickListener {
            @Override
            public void onDelete(ListObject deletedList) {
                adapter.remove(deletedList);
                setListAdapter(adapter);
            }

            public void onSave(ListObject addedList) {
                adapter.add(addedList);
                setListAdapter(adapter);
            }
        }

        public void deleteListFromAdapter(ListObject deletedList){
            adapter.remove(deletedList);
            setListAdapter(adapter);
        }

        @Override
        public void processResult(String result) {
            lists = ListObject.getLists( result );
            adapter = new OwnedListAdapter(inf.getContext(), R.layout.owned_list_line, R.id.oListName,lists);
            setListAdapter(adapter);
            swipeLayout.setRefreshing( false );
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SharedListsFragment extends ListFragment implements MongoInterface {
        private ArrayList<ListObject> lists;
        SwipeRefreshLayout swipeLayout;

        private LayoutInflater inf;
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Intent myIntent = new Intent( getActivity() , ItemActivity.class );
            myIntent.putExtra( "list", lists.get(position) );
            startActivity(myIntent);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.fragment_shared_lists, container, false);
            Mongo.getMongo(this).getListByContributor(email);
            inf = inflater;
            swipeLayout = new ListFragmentSwipeRefreshLayout( container.getContext(), this );
            swipeLayout.addView( view );
            swipeLayout.setColorSchemeResources( R.color.bg_color, R.color.grey, R.color.txt_color, R.color.btn_blue);
            swipeLayout.setOnRefreshListener( new ListRefreshListener( this, Mongo.KEY_CONTRIBUTORS, swipeLayout ));
            swipeLayout.setRefreshing( true );
            return swipeLayout;
        }

        @Override
        public void processResult(String result) {
            lists = ListObject.getLists(result);
            SharedListAdapter adapter = new SharedListAdapter(
                    inf.getContext(), R.layout.shared_list_line, R.id.sListName,
                    lists);
            setListAdapter(adapter);
            swipeLayout.setRefreshing( false );
        }
    }

    private static class ListRefreshListener implements SwipeRefreshLayout.OnRefreshListener
    {
        private MongoInterface activity;
        SwipeRefreshLayout layout;
        private String key;

        ListRefreshListener( MongoInterface a, String k, SwipeRefreshLayout l )
        {
            activity = a;
            key = k;
            layout = l;
        }
        @Override
        public void onRefresh() {
            Mongo m = Mongo.getMongo( activity );
            if( key.equals(Mongo.KEY_CONTRIBUTORS) )
                m.getListByContributor( email );
            else
                m.get( Mongo.COLL_LISTS, Mongo.KEY_OWNER, email );
            layout.setRefreshing( true );
        }
    }

    private static class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {
        ListFragment frag;

        public ListFragmentSwipeRefreshLayout(Context context, ListFragment f) {
            super(context);
            frag = f;
        }

        @Override
        public boolean canChildScrollUp() {
            final ListView listView = frag.getListView();
            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }
        }

    }

    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }

}
