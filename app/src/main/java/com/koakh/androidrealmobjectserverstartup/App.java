package com.koakh.androidrealmobjectserverstartup;

import android.app.Application;
import android.util.Log;

import com.koakh.androidrealmobjectserverstartup.model.CRDTCounter;
import com.koakh.androidrealmobjectserverstartup.model.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.log.RealmLog;

public class App extends Application {

  public static String TAG;

  public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/default";
  public static final String REALM_AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
  public static final byte[] REALM_DB_ENCKEY = "6DA3651B4BB532A984CCA92B530AD6EF174FB400ABCEA1CF76F6029627C8934B".getBytes();
  public static boolean REALM_DB_DELETE_ON_START = false;

  private SyncUser mSyncUser;

  @Override
  public void onCreate() {
    super.onCreate();

    TAG = getResources().getString(R.string.app_tag);

    Realm.init(this);

    // Enable full log output when debugging
    if (BuildConfig.DEBUG) {
      RealmLog.setLevel(Log.VERBOSE);
    }
  }

  public SyncConfiguration getRealmConfiguration(SyncUser syncUser) {

    SyncConfiguration result = null;

    try {
      // Create a RealmConfiguration for our user
      SyncConfiguration config = new SyncConfiguration.Builder(syncUser, App.REALM_URL)
          .encryptionKey(App.REALM_DB_ENCKEY)
          //Executed When Create Database in FirstTime
          .initialData(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
              realm.createObject(CRDTCounter.class, 1);

              //Koakh
              User user1 = realm.createObject(User.class, UUID.randomUUID().toString());
              user1.setName("Bob");
              user1.setAge(28);
              // Moke
              User user2 = realm.createObject(User.class, UUID.randomUUID().toString());
              user2.setName("Luke");
              user2.setAge(14);
            }
          })
          .build();

      result = config;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public SyncUser getSyncUser() {
    return mSyncUser;
  }

  public void setSyncUser(SyncUser mSyncUser) {
    this.mSyncUser = mSyncUser;
  }
}
