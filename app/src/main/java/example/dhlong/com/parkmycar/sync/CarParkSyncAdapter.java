package example.dhlong.com.parkmycar.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class CarParkSyncAdapter extends AbstractThreadedSyncAdapter {

    public CarParkSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://datamall2/mytransport.sg/ltaodataservice")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarParkAPI carParkAPI = retrofit.create(CarParkAPI.class);
        Call<CarParkResponse> call = carParkAPI.loadCarParks("xvfeFFQxTcOS4OozxWTwYA==", "47d36256-bc7c-41c4-ad42-4f248c87b849");
        try {
            Response response = call.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
