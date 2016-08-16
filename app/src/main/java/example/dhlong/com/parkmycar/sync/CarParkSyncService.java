package example.dhlong.com.parkmycar.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CarParkSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static CarParkSyncAdapter sCarParkSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("CarParkSyncService", "onCreate - CarParkSyncService");
        synchronized (sSyncAdapterLock) {
            if (sCarParkSyncAdapter == null) {
                sCarParkSyncAdapter = new CarParkSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sCarParkSyncAdapter.getSyncAdapterBinder();
    }
}
