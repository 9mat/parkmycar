package example.dhlong.com.parkmycar.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.model.Constraint;

import example.dhlong.com.parkmycar.data.CarParkContract;


public class CarParkContentProvider extends ProviGenProvider {
    private static Class[] contracts = new Class[]{CarParkContract.class};

    private static final int DATABASE_VERSION = 1;
    private static final int URI_CARPARK_ID = 101;

    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        super.onCreate();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CarParkContract.CARPARK_ID_URI.getAuthority(),
                CarParkContract.CARPARK_ID_URI.getPath() + "/#", URI_CARPARK_ID);
        return true;
    }

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "carparks", null, DATABASE_VERSION, contracts){
            @Override
            public void onCreate(SQLiteDatabase database) {
                new TableBuilder(CarParkContract.class)
                        .addConstraint(CarParkContract.CARPARK_ID, Constraint.UNIQUE, Constraint.OnConflict.REPLACE)
                        .createTable(database);
            }
        };
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri) == URI_CARPARK_ID){
            SQLiteDatabase db = openHelper(getContext()).getReadableDatabase();
            return db.query(
                    findMatchingContract(CarParkContract.CONTENT_URI).getTable(),
                    projection,
                    CarParkContract.CARPARK_ID + " = ? ",
                    new String[]{uri.getLastPathSegment()},
                    null, null, null
            );
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(uriMatcher.match(uri) == URI_CARPARK_ID){
            SQLiteDatabase db = openHelper(getContext()).getWritableDatabase();
            return db.update(
                    findMatchingContract(CarParkContract.CONTENT_URI).getTable(),
                    values, CarParkContract.CARPARK_ID + " = ? ",
                    new String[]{uri.getLastPathSegment()});
        } else {
            return super.update(uri, values, selection, selectionArgs);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int returnCount;

        if (uriMatcher.match(uri) == URI_CARPARK_ID) {
            SQLiteDatabase db = openHelper(getContext()).getWritableDatabase();
            db.beginTransaction();
            returnCount = db.delete(
                    findMatchingContract(CarParkContract.CONTENT_URI).getTable(),
                    CarParkContract.CARPARK_ID + " = ? ",
                    new String[]{uri.getLastPathSegment()}
            );
            db.setTransactionSuccessful();
            db.endTransaction();
        } else {
            returnCount = super.delete(uri, selection, selectionArgs);
        }

        return returnCount;
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
