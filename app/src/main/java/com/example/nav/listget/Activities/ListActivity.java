package com.example.nav.listget.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
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
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private OwnedListAdapter adapter;
        LayoutInflater inf;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_list, container, false);
            // Create the list fragment's content view by calling the super method
            inf = inflater;
            // Now create a SwipeRefreshLayout to wrap the fragment's content view
            mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

            // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
            // the SwipeRefreshLayout
            mSwipeRefreshLayout.addView(view,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Make sure that the SwipeRefreshLayout will fill the fragment
            mSwipeRefreshLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            Mongo.getMongo( this ).get( Mongo.COLL_LISTS, Mongo.KEY_OWNER, email );
            mSwipeRefreshLayout.setColorSchemeResources(R.color.white, R.color.grey, R.color.black, R.color.txt_color);
            setOnRefreshListener(new ListRefreshListener(this, mSwipeRefreshLayout, Mongo.KEY_OWNER));
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
            setRefreshing( true );
            // Now return the SwipeRefreshLayout as this fragment's content view
            return mSwipeRefreshLayout;
        }


        /**
         * Set the {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener} to listen for
         * initiated refreshes.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#setOnRefreshListener(android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener)
         */
        public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }

        /**
         * Returns whether the {@link android.support.v4.widget.SwipeRefreshLayout} is currently
         * refreshing or not.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#isRefreshing()
         */
        public boolean isRefreshing() {
            return mSwipeRefreshLayout.isRefreshing();
        }

        /**
         * Set whether the {@link android.support.v4.widget.SwipeRefreshLayout} should be displaying
         * that it is refreshing or not.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#setRefreshing(boolean)
         */
        public void setRefreshing(boolean refreshing) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }

        /**
         * @return the fragment's {@link android.support.v4.widget.SwipeRefreshLayout} widget.
         */
        public SwipeRefreshLayout getSwipeRefreshLayout() {
            return mSwipeRefreshLayout;
        }

        /**
         * Sub-class of {@link android.support.v4.widget.SwipeRefreshLayout} for use in this
         * {@link android.support.v4.app.ListFragment}. The reason that this is needed is because
         * {@link android.support.v4.widget.SwipeRefreshLayout} only supports a single child, which it
         * expects to be the one which triggers refreshes. In our case the layout's child is the content
         * view returned from
         * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
         * which is a {@link android.view.ViewGroup}.
         *
         * <p>To enable 'swipe-to-refresh' support via the {@link android.widget.ListView} we need to
         * override the default behavior and properly signal when a gesture is possible. This is done by
         * overriding {@link #canChildScrollUp()}.
         */
        private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

            public ListFragmentSwipeRefreshLayout(Context context) {
                super(context);
            }

            /**
             * As mentioned above, we need to override this method to properly signal when a
             * 'swipe-to-refresh' is possible.
             *
             * @return true if the {@link android.widget.ListView} is visible and can scroll up.
             */
            @Override
            public boolean canChildScrollUp() {
                final ListView listView = getListView();
                if (listView.getVisibility() == View.VISIBLE) {
                    return canListViewScrollUp(listView);
                } else {
                    return false;
                }
            }

        }

        /**
         * Utility method to check whether a {@link ListView} can scroll up from it's current position.
         * Handles platform version differences, providing backwards compatible functionality where
         * needed.
         */
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

        public void processResult(String result) {
            ArrayList<ListObject> lists = ListObject.getLists( result );
            adapter = new OwnedListAdapter(inf.getContext(), R.layout.owned_list_line, R.id.oListName,lists);
            setListAdapter(adapter);
            setRefreshing(false);
        }

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

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SharedListsFragment extends ListFragment implements MongoInterface {
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private SharedListAdapter adapter;
        LayoutInflater inf;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_list, container, false);
            // Create the list fragment's content view by calling the super method
            inf = inflater;
            // Now create a SwipeRefreshLayout to wrap the fragment's content view
            mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

            // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
            // the SwipeRefreshLayout
            mSwipeRefreshLayout.addView(view,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Make sure that the SwipeRefreshLayout will fill the fragment
            mSwipeRefreshLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            Mongo.getMongo( this ).getListByContributor( email );
            mSwipeRefreshLayout.setColorSchemeResources(R.color.white, R.color.grey, R.color.black, R.color.txt_color);
            setOnRefreshListener(new ListRefreshListener( this, mSwipeRefreshLayout, Mongo.KEY_CONTRIBUTORS ));
            setRefreshing( true );
            // Now return the SwipeRefreshLayout as this fragment's content view
            return mSwipeRefreshLayout;
        }


        /**
         * Set the {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener} to listen for
         * initiated refreshes.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#setOnRefreshListener(android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener)
         */
        public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }

        /**
         * Returns whether the {@link android.support.v4.widget.SwipeRefreshLayout} is currently
         * refreshing or not.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#isRefreshing()
         */
        public boolean isRefreshing() {
            return mSwipeRefreshLayout.isRefreshing();
        }

        /**
         * Set whether the {@link android.support.v4.widget.SwipeRefreshLayout} should be displaying
         * that it is refreshing or not.
         *
         * @see android.support.v4.widget.SwipeRefreshLayout#setRefreshing(boolean)
         */
        public void setRefreshing(boolean refreshing) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }

        /**
         * @return the fragment's {@link android.support.v4.widget.SwipeRefreshLayout} widget.
         */
        public SwipeRefreshLayout getSwipeRefreshLayout() {
            return mSwipeRefreshLayout;
        }

        /**
         * Sub-class of {@link android.support.v4.widget.SwipeRefreshLayout} for use in this
         * {@link android.support.v4.app.ListFragment}. The reason that this is needed is because
         * {@link android.support.v4.widget.SwipeRefreshLayout} only supports a single child, which it
         * expects to be the one which triggers refreshes. In our case the layout's child is the content
         * view returned from
         * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
         * which is a {@link android.view.ViewGroup}.
         *
         * <p>To enable 'swipe-to-refresh' support via the {@link android.widget.ListView} we need to
         * override the default behavior and properly signal when a gesture is possible. This is done by
         * overriding {@link #canChildScrollUp()}.
         */
        private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

            public ListFragmentSwipeRefreshLayout(Context context) {
                super(context);
            }

            /**
             * As mentioned above, we need to override this method to properly signal when a
             * 'swipe-to-refresh' is possible.
             *
             * @return true if the {@link android.widget.ListView} is visible and can scroll up.
             */
            @Override
            public boolean canChildScrollUp() {
                final ListView listView = getListView();
                if (listView.getVisibility() == View.VISIBLE) {
                    return canListViewScrollUp(listView);
                } else {
                    return false;
                }
            }

        }

        /**
         * Utility method to check whether a {@link ListView} can scroll up from it's current position.
         * Handles platform version differences, providing backwards compatible functionality where
         * needed.
         */
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

        public void processResult(String result) {
            ArrayList<ListObject> lists = ListObject.getLists( result );
            adapter = new SharedListAdapter(inf.getContext(), R.layout.owned_list_line, R.id.oListName,lists);
            setListAdapter(adapter);
            setRefreshing(false);
        }
    }

    private static class ListRefreshListener implements SwipeRefreshLayout.OnRefreshListener
    {
        private MongoInterface activity;
        private SwipeRefreshLayout layout;
        private String key;

        ListRefreshListener( MongoInterface a, SwipeRefreshLayout l, String k )
        {
            super();
            activity = a;
            layout = l;
            key = k;
        }
        @Override
        public void onRefresh() {
            layout.setRefreshing( true );
            if( key.equals(Mongo.KEY_CONTRIBUTORS) )
                Mongo.getMongo( activity ).getListByContributor( email );
            else
                Mongo.getMongo( activity ).get( Mongo.COLL_LISTS, key, email );
        }
    }

}
