package kungfuwander.main;


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

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Weather extends Fragment {
    ImageView imageView;
    TextView txt_city_name;
    RecyclerView recyclerView;


    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static Fragment_Weather instance;

    public static Fragment_Weather getInstance() {
        if(instance==null)
            instance = new Fragment_Weather();
        return instance;
    }

    public Fragment_Weather() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));



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
                    public void accept(WeatherForecast weatherForecast) throws Exception {

                        if(weatherForecast==null)
                        {
                            Log.d("WORKER", "DKFSD");
                        }
                        else
                        {
                            displayWeather(weatherForecast);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                   Log.d("ERROR", "" + throwable.getMessage());
                    }
                })

        );
    }

    private void displayWeather(WeatherForecast weatherResult) {

        txt_city_name.setText("CITY");
        Log.d("SET WEATHER", "DKFSD");
        Weather_Adapter adapter = new Weather_Adapter(getContext(), weatherResult);
        Log.d("SET HELP", "DKFSD");
        recyclerView.setAdapter(adapter);

    }

}
