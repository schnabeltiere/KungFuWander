package kungfuwander.main.weather;


import io.reactivex.Observable;
import kungfuwander.main.beans.WeatherResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng (@Query("lat") String lat, @Query("lon") String lng, @Query("appid") String appid, @Query("units") String unit);

    @GET("cities")
    Observable<WeatherResult> getWeatherByCityName(@Query("q") String cityName,  @Query("appid") String appid, @Query("units") String unit);
}