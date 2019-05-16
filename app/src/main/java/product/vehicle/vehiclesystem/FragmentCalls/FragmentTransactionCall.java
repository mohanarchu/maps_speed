package product.vehicle.vehiclesystem.FragmentCalls;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;


import product.vehicle.vehiclesystem.R;
import product.vehicle.vehiclesystem.TripHome;

/**
 * Created by SasTi on 23-02-2018.
 */

public class FragmentTransactionCall {
    public static Context context;
    public static void TripHome(Activity activity) {
        TripHome replace_triphome = new TripHome();
        FragmentManager fragmentManager = ((Activity) activity).getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_replace, replace_triphome).addToBackStack(null).commit();

    }


}
