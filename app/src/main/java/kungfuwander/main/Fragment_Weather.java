package kungfuwander.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    TextView txt_city_name, txt_temperature, txt_dateTime;
    LinearLayout weather_panel;
    ProgressBar loading;

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
        txt_temperature = itemView.findViewById(R.id.txt_temperature);
        txt_dateTime = itemView.findViewById(R.id.txt_dateTime);


        weather_panel = (LinearLayout) itemView.findViewById(R.id.weather_panel);


        getWeatherInformation();
        return itemView;
    }

    private void getWeatherInformation() {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        final String date = df.format(Calendar.getInstance().getTime());
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_lcation.getLatitude()),String.valueOf(Common.current_lcation.getLongitude()),
                Common.APP_ID,
                "metric")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherResult>() {
                                       @Override
                                       public void accept(WeatherResult weatherResult) throws Exception {
//
                                           Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").append(weatherResult.getWeather().get(0).getIcon())
                                                   .append(".png").toString()).into(imageView);

                                           txt_city_name.setText(weatherResult.getName());
                                           txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("° C"));
                                           txt_dateTime.setText(date);


                                           weather_panel.setVisibility(View.VISIBLE);
                                       }

                                   },  new Consumer<Throwable>()
                                   {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception
                                       {
                                           Toast.makeText(getActivity(), " " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   }

                        )

        );
    }

}
