package com.ronda.audiodemo.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ronda.audiodemo.R;
import com.ronda.audiodemo.utils.LogHelper;


/**
 *  Created by Ronda on 2017/12/28.
 *
 * Abstract activity with toolbar, navigation drawer and cast support. Needs to be extended by
 * any activity that wants to be shown as a top level activity.
 *
 * The requirements for a subclass is to call {@link #initializeToolbar()} on onCreate, after
 * setContentView() is called and have three mandatory layout elements:
 * a {@link Toolbar} with id 'toolbar',
 * a {@link DrawerLayout} with id 'drawerLayout' and
 * a {@link android.widget.ListView} with id 'drawerList'.
 *
 */

public abstract class ActionBarCastActivity extends AppCompatActivity {

    private static final String TAG = LogHelper.makeLogTag(ActionBarCastActivity.class);


    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;


    private boolean mToolbarInitialized;

    private int mItemToOpenWhenDrawerCloses = -1;


    // fragmentManager 回退栈的监听器
    FragmentManager.OnBackStackChangedListener mBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            Log.d("Liu", "onBackStackChanged");
            updateDrawerToggle();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mToolbarInitialized) {
            throw new IllegalStateException("You must run super.initializeToolbar at " +
                    "the end of your onCreate method");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Whenever the fragment back stack changes, we may need to update the
        // action bar toggle: only top level screens show the hamburger-like icon(像汉堡包的图标,即三个横线的图表), inner
        // screens - either Activities or fragments - show the "Up" icon instead.
        getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Liu", "OptionsItemSelected: " + item.getItemId());
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // If not handled by drawerToggle, home needs to be handled by(在...之前) returning to previous
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open, back will close it
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        // Otherwise, it may return to the previous fragment stack
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            // Lastly, it will rely on the system behavior for back
            super.onBackPressed();
        }
    }


    protected void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id " +
                    "'toolbar'");
        }

        // Log.d("Liu", "before setSupportActionBar --> " + getSupportActionBar()); // null
        setSupportActionBar(mToolbar);
        // Log.d("Liu", "after setSupportActionBar --> " + getSupportActionBar()); // android.support.v7.app.ToolbarActionBar@52818840


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView == null) {
                throw new IllegalStateException("Layout requires a NavigationView " +
                        "with id 'nav_view'");
            }

            // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(R.string.app_name);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);

                    if (mItemToOpenWhenDrawerCloses >= 0) {
                        Bundle extras = ActivityOptionsCompat.makeCustomAnimation(ActionBarCastActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = null;
                        switch (mItemToOpenWhenDrawerCloses) {
                            case R.id.navigation_allmusic:
                                activityClass = MusicPlayerActivity.class;
                                break;
                            case R.id.navigation_playlists:
                                activityClass = PlaceholderActivity.class;
                                break;
                        }
                        if (activityClass != null) {
                            // TODO: 2017/12/28  这里每次关闭侧边栏时都会启动一下Activity, 觉得有问题
                            startActivity(new Intent(ActionBarCastActivity.this, activityClass), extras);
                            finish();
                            Log.d("Liu", "activityClass: " + activityClass);
                        }


                    }
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle); // 建议使用 addDrawerListener

            populateDrawerItems(navigationView);
        }
        mToolbarInitialized = true;
    }

    private void populateDrawerItems(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mItemToOpenWhenDrawerCloses = item.getItemId();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        if (getClass().isAssignableFrom(MusicPlayerActivity.class)) {
            navigationView.setCheckedItem(R.id.navigation_allmusic);
        } else if (getClass().isAssignableFrom(PlaceholderActivity.class)) {
            navigationView.setCheckedItem(R.id.navigation_playlists);
        }

//        if (MusicPlayerActivity.class.isAssignableFrom(getClass())) {
//            navigationView.setCheckedItem(R.id.navigation_allmusic);
//        } else if (PlaceholderActivity.class.isAssignableFrom(getClass())) {
//            navigationView.setCheckedItem(R.id.navigation_playlists);
//        }

    }


    private void updateDrawerToggle() {

        if (mDrawerToggle != null) {
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }

        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }
}
