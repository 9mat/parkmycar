package example.dhlong.com.parkmycar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import example.dhlong.com.parkmycar.CarParkFragment.OnListFragmentInteractionListener;
import example.dhlong.com.parkmycar.sync.CarParkSyncAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class CarParkRecyclerViewAdapter extends RecyclerView.Adapter<CarParkRecyclerViewAdapter.ViewHolder> {

    private List<CarParkSyncAdapter.CarPark> mValues;
    private final OnListFragmentInteractionListener mListener;

    public CarParkRecyclerViewAdapter(List<CarParkSyncAdapter.CarPark> carParks, OnListFragmentInteractionListener listener) {
        mValues = carParks;
        mListener = listener;
    }

    public CarParkRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://datamall2.mytransport.sg/ltaodataservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarParkSyncAdapter.CarParkAPI carParkAPI = retrofit.create(CarParkSyncAdapter.CarParkAPI.class);
        Call<CarParkSyncAdapter.CarParkResponse> call = carParkAPI.loadCarParks("xvfeFFQxTcOS4OozxWTwYA==", "47d36256-bc7c-41c4-ad42-4f248c87b849");
        call.enqueue(new Callback<CarParkSyncAdapter.CarParkResponse>() {
            @Override
            public void onResponse(Call<CarParkSyncAdapter.CarParkResponse> call, Response<CarParkSyncAdapter.CarParkResponse> response) {
                if(response.isSuccessful()) {
                    mValues = response.body().value;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CarParkSyncAdapter.CarParkResponse> call, Throwable t) {
            }
        });
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_carpark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCarPark = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mCarPark);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public CarParkSyncAdapter.CarPark mCarPark;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
