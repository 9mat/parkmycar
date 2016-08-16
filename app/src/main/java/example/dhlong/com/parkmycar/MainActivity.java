package example.dhlong.com.parkmycar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import example.dhlong.com.parkmycar.sync.CarParkSyncAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CarParkSyncAdapter.initializeSyncAdapter(this);
    }
}
