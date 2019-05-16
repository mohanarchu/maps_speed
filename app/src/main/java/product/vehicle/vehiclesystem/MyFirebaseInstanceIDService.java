package product.vehicle.vehiclesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    SharedPreferences sharedpreferences;
    Context context;
    public static final String MyPREFERENCES = "Mytoken";
    // Context context;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences.Editor editor1 = getSharedPreferences("firebase", MODE_PRIVATE).edit();

        editor1.putString("firebasetoken", "nithya2425455");

        editor1.commit();
        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later

        // SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }


}
