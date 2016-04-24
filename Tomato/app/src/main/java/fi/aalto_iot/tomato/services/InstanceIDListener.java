package fi.aalto_iot.tomato.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceIDListener extends InstanceIDListenerService {

    private static final String TAG = "InstanceIDListerner";

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this.getApplicationContext(), RegistrationIntentService.class);
        startService(intent);
    }
}
