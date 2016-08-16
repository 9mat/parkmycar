package example.dhlong.com.parkmycar.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import example.dhlong.com.parkmycar.R;
import example.dhlong.com.parkmycar.data.CarParkContract;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class CarParkSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public CarParkSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://datamall2.mytransport.sg/ltaodataservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarParkAPI carParkAPI = retrofit.create(CarParkAPI.class);
        Call<CarParkResponse> call = carParkAPI.loadCarParks("xvfeFFQxTcOS4OozxWTwYA==", "47d36256-bc7c-41c4-ad42-4f248c87b849");
        try {
            ContentValues[] cVArray = getContentValues(call.execute().body().value);
            if(cVArray.length > 0) {
                getContext().getContentResolver().bulkInsert(CarParkContract.CONTENT_URI, cVArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ContentValues[] getContentValues(List<CarPark> carParks) {
        ContentValues[] cVArray = new ContentValues[carParks.size()];
        int i = 0;
        for (CarPark carPark: carParks) {
            ContentValues cV = new ContentValues();

            cV.put(CarParkContract.CARPARK_ID,  carPark.CarParkID);
            cV.put(CarParkContract.AREA,        carPark.Area);
            cV.put(CarParkContract.LANDMARK,    carPark.Development);
            cV.put(CarParkContract.LATITUDE,    carPark.Latitude);
            cV.put(CarParkContract.LONGITUDE,   carPark.Longitude);
            cV.put(CarParkContract.LOTS,        carPark.Lots);

            cVArray[i] = cV;
            i++;
        }

        return  cVArray;
    }

    public static class CarPark {
        String CarParkID;
        String Area;
        String Development;
        double Latitude;
        double Longitude;
        int Lots;

        @Override
        public String toString() {
            return Development + " car park " + CarParkID + " at " + Area + " has " + Lots + " available lots.";
        }
    }

    public static class CarParkResponse {
        public List<CarPark> value;
    }

    public interface CarParkAPI {
        @GET("CarParkAvailability")
        Call<CarParkResponse> loadCarParks(@Header("AccountKey") String accountKey, @Header("UniqueUserID") String uniqueUserId);
    }




    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        CarParkSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(CarParkSyncAdapter.class.getSimpleName(), "initializeSyncAdapter");
        getSyncAccount(context);
    }
}
