package kungfuwander.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kungfuwander.main.WeatherForecast;
import kungfuwander.main.Weather_Adapter;
import kungfuwander.main.weather.Common;
import kungfuwander.main.weather.IOpenWeatherMap;
import kungfuwander.main.R;
import kungfuwander.main.weather.RetrofitClient;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    ImageView imageView;
    TextView txt_city_name;
    RecyclerView recyclerView;


    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static WeatherFragment instance;

    public static WeatherFragment getInstance() {
        if(instance==null)
            instance = new WeatherFragment();
        return instance;
    }

    public WeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_weather, container, false);
        imageView = (ImageView) itemView.findViewById(R.id.img_weather);
        txt_city_name = itemView.findViewById(R.id.txt_city_name);

        recyclerView = itemView.findViewById(R.id.recycler_forecast);
        LinearLayoutManager ll = new LinearLayoutManager(this.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(ll);



        getWeatherInformation();
        return itemView;
    }

    private void getWeatherInformation() {

        compositeDisposable.add(mService.getForcastWeather(String.valueOf(Common.current_lcation.getLatitude()),String.valueOf(Common.current_lcation.getLongitude()),
                Common.APP_ID,
                "metric")
                        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecast>() {
                    @Override
                    public void accept(WeatherForecast weatherForecast)  {

                        if(weatherForecast==null)
                        {
                            Log.d("NULL POINT", "DKFSD");
                        }
                        else
                        {
                            displayWeather(weatherForecast);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable){
                   Log.d("ERROR", "" + throwable.getMessage());
                    }
                })

        );
    }

    private void displayWeather(WeatherForecast weatherResult) {

        txt_city_name.setText(weatherResult.city.name);
        Log.d("SET WEATHER", "DKFSD");
        Weather_Adapter adapter = new Weather_Adapter(this.getContext(), weatherResult);
        Log.d("SET HELP", "DKFSD");


        recyclerView.setAdapter(adapter);

    }

}
