package com.koakh.androidrealmobjectserverstartup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.koakh.androidrealmobjectserverstartup.model.CRDTCounter;
import com.koakh.androidrealmobjectserverstartup.model.User;
import com.koakh.androidrealmobjectserverstartup.ui.fragments.DummyFragment;
import com.koakh.androidrealmobjectserverstartup.ui.fragments.RecycleViewFragment;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    RecycleViewFragment.OnFragmentInteractionListener,
    DummyFragment.OnFragmentInteractionListener {

  //Application
  private App mApp;
  //Application UI
  DrawerLayout mDrawer;

  private Realm mRealm;
  private SyncUser mSyncUser;
  private CRDTCounter counter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    //Setup Initial Fragment
    setupInitialFragment(savedInstanceState);

    // Check if we have a valid user, otherwise redirect to login
    if (SyncUser.currentUser() == null) {
      gotoLoginActivity();
    }

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    mDrawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    //Application
    mApp = (App) getApplication();
  }

  @Override
  protected void onStart() {
    super.onStart();

    mSyncUser = SyncUser.currentUser();
    if (mSyncUser != null) {

      //Assign SyncUser to App
      mApp.setSyncUser(mSyncUser);

      // This will automatically sync all changes in the background for as long as the Realm is open
      mRealm = Realm.getInstance(mApp.getRealmConfiguration(mSyncUser));

      counter = mRealm.where(CRDTCounter.class).findFirstAsync();
      counter.addChangeListener(new RealmChangeListener<CRDTCounter>() {
        @Override
        public void onChange(CRDTCounter counter) {
          Long countCRDTCounter = mRealm.where(CRDTCounter.class).count();
          Long countUsers = mRealm.where(User.class).count();
          Log.d(App.TAG, String.format("CRDTCounter counter: %d", countCRDTCounter));
          Log.d(App.TAG, String.format("user counter: %d", countUsers));
        }
      });
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    closeRealm();

    mSyncUser = null;
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      case R.id.action_logout:
        closeRealm();
        mSyncUser.logout();
        gotoLoginActivity();
        return true;
      case R.id.action_settings:
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      selectDrawerItem(item);
    } else if (id == R.id.nav_gallery) {
      selectDrawerItem(item);
    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  // In order to receive event callbacks from the fragments,
  // the activity that hosts it must implement the interface defined in the fragment class.
  @Override
  public void onFragmentInteraction(Uri uri) {
    Log.d(App.TAG, String.format("onFragmentInteraction: %s", uri));
  }

  public void setupInitialFragment(Bundle savedInstanceState) {

    // Check that the activity is using the layout version with
    // the fragment_container FrameLayout
    if (findViewById(R.id.fragment_container) != null) {

      // However, if we're being restored from a previous state,
      // then we don't need to do anything and should return or else
      // we could end up with overlapping fragments.
      if (savedInstanceState != null) {
        return;
      }

      // Create a new Fragment to be placed in the activity layout
      RecycleViewFragment fragmentLocation = new RecycleViewFragment();

      // In case this activity was started with special instructions from an
      // Intent, pass the Intent's extras to the fragment as arguments
      fragmentLocation.setArguments(getIntent().getExtras());

      // Add the fragment to the 'fragment_container' FrameLayout
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.fragment_container, fragmentLocation)
          .commit();
    }
  }

  public void selectDrawerItem(MenuItem menuItem) {
    // Create a new fragment and specify the fragment to show based on nav item clicked
    Fragment fragment = null;
    Class fragmentClass;
    switch (menuItem.getItemId()) {
      case R.id.nav_camera:
        fragmentClass = RecycleViewFragment.class;
        break;
      case R.id.nav_gallery:
        fragmentClass = DummyFragment.class;
        break;
      default:
        fragmentClass = RecycleViewFragment.class;
    }

    try {
      fragment = (Fragment) fragmentClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Insert the fragment by replacing any existing fragment
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit()
    ;

    // Highlight the selected item has been done by NavigationView
    menuItem.setChecked(true);
    // Set action bar title
    setTitle(menuItem.getTitle());
    // Close the navigation drawer
    mDrawer.closeDrawers();
  }

  private void closeRealm() {
    if (mRealm != null && !mRealm.isClosed()) {
      mRealm.close();
    }
  }

  private void gotoLoginActivity() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }

  private void adjustCounter(final int adjustment) {
    // A synchronized Realm can get written to at any point in time, so doing synchronous writes on the UI
    // thread is HIGHLY discouraged as it might block longer than intended. Only use async transactions.
    mRealm.executeTransactionAsync(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        CRDTCounter counter = realm.where(CRDTCounter.class).findFirst();
        counter.add(adjustment);
      }
    });
  }
}
