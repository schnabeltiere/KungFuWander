package kungfuwander.main.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import kungfuwander.main.R;

public class Weather_Adapter extends RecyclerView.Adapter<Weather_Adapter.MyViewHolder> {

    Context context;
    WeatherForecast weatherForecast;

    public Weather_Adapter(Context context, WeatherForecast weatherForecast) {
        this.context = context;
        this.weatherForecast = weatherForecast;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("CREATE VIEW HOLDER", "");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").append(weatherForecast.list.get(i).weather.get(0).getIcon())
                .append(".png").toString()).into(myViewHolder.img_weather);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd.MM.YYYY");
        myViewHolder.txt_date.setText(new StringBuilder(Common.convertToDate(weatherForecast.list.get(i).dt)));
          myViewHolder.txt_time.setText(new StringBuilder(weatherForecast.list.get(i).weather.get(0).getDescription()));

        myViewHolder.txt_temp.setText(new StringBuilder(String.valueOf(weatherForecast.list.get(i).main.getTemp())).append(" °C"));
        Log.d("WORKER ", "adapter set");
    }

    @Override
    public int getItemCount() {
        return weatherForecast.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_time;
        TextView txt_date;
        TextView txt_temp;
        ImageView img_weather;
        public MyViewHolder(View itemView)
        { super(itemView);

        img_weather = itemView.findViewById(R.id.img_weather);
        txt_date = itemView.findViewById(R.id.txt_day);
        txt_temp = itemView.findViewById(R.id.txt_temperature);
        txt_time = itemView.findViewById(R.id.txt_time);
        }
    }
}
