package fi.aalto_iot.tomato;

import android.app.Application;
import android.util.Log;

import fi.aalto_iot.tomato.db.data.RoomModel;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseApplication extends Application {
    private final int REALM_VERSION = 1;
    private String TAG = "BaseActivity";

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration conf = new RealmConfiguration.Builder(this)
                .name("room_db.realm")
                .schemaVersion(42)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(conf);
        Realm realm = Realm.getDefaultInstance();

        Log.d(TAG, "Realm: " + realm.getVersion());

        // let's add tomato here
        realm.beginTransaction();
        RoomModel tomato = new RoomModel();

        tomato.setRoomName("Tomato");
        tomato.setOccupation(false);
        realm.copyToRealmOrUpdate(tomato);
        realm.commitTransaction();

        realm.close();
    }
}
