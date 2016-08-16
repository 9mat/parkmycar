package example.dhlong.com.parkmycar.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by 9mat on 16/8/2016.
 */

public interface CarParkContract extends ProviGenBaseContract {
    @Column(Column.Type.TEXT)
    String CARPARK_ID = "id";

    @Column(Column.Type.TEXT)
    String AREA = "area";

    @Column(Column.Type.TEXT)
    String LANDMARK = "landmark";

    @Column(Column.Type.REAL)
    String LATITUDE = "latitude";

    @Column(Column.Type.REAL)
    String LONGITUDE = "longitude";

    @Column(Column.Type.INTEGER)
    String LOTS = "lots";

    @ContentUri
    Uri CONTENT_URI = Uri.parse("content://dhlong.parkmycar.app/carparks");
    Uri CARPARK_ID_URI = CONTENT_URI.buildUpon().appendPath("carpark_id").build();
}
