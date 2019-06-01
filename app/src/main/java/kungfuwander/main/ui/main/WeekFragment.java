package kungfuwander.main.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.chart.common.dataentry.DataEntry;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import kungfuwander.main.FireBaseHelper;
import kungfuwander.main.Hiking;
import kungfuwander.main.R;
import kungfuwander.main.chartview.ChartView;
import kungfuwander.main.chartview.draw.data.InputData;

public class WeekFragment extends Fragment {

    private final String TAG = getClass().getName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);
        ChartView chartView = view.findViewById(R.id.chartViewWeek);

//        List<InputData> chartData = createChartData();
//        chartView.setData(chartData);

        // nested so data gets loaded all or nothing
        FireBaseHelper.fetchLoggedInUserHikes(hikes -> {
            List<InputData> inputData = extractDataOutOfHikes(hikes);
            chartView.setData(inputData);
        });

        return view;
    }

    private List<InputData> extractDataOutOfHikes(List<Hiking> hikes) {
        List<InputData> data = new ArrayList<>();

        // makes it easier
        hikes.sort(Comparator.comparing(Hiking::getStart));

        LocalDate previousMonday = determineStartOfWeek();
        LocalDate endOfWeek = determineEndOfWeek(previousMonday);
        Log.d(TAG, "Start is: " + previousMonday + " end is: " + endOfWeek);

        for (LocalDate currentDay = previousMonday; currentDay.isBefore(endOfWeek);
             currentDay = currentDay.plusDays(1)) {

            List<Hiking> hikesOnSameDay = hikesOnSameDay(hikes, currentDay);
            Log.d(TAG, "Same day: " + hikesOnSameDay.size());

            int steps = sumSteps(hikesOnSameDay);

//            String format = currentDay.format(DateTimeFormatter.ofPattern("dd.MM"));
            // TODO: 02.06.2019 change back
            data.add(new InputData(steps+currentDay.getDayOfMonth()));
        }

        long currMillis = System.currentTimeMillis();
        currMillis -= currMillis % TimeUnit.DAYS.toMillis(1);

        Log.d(TAG, "ok what is currentMillis? " + currMillis);
        for (int i = 0; i < data.size(); i++) {
            long position = (long) (data.size() - 1 - i);
            long offsetMillis = TimeUnit.DAYS.toMillis(position);
            Log.d(TAG, "the position: " + position);
            Log.d(TAG, "The offset: " + offsetMillis);

            long millis = currMillis - offsetMillis;
            Log.wtf(TAG, "And now the millis: " + millis);

            // ok like for real what is this shit?
            // i mean how can i control the text on the bottom?
            data.get(i).setMillis(i*5_000_000_000_00L);
        }

        return data;
    }

    private int sumSteps(List<Hiking> allHikes) {
        return allHikes.stream()
                .mapToInt(Hiking::getSteps)
                .sum();
    }

    private List<Hiking> hikesOnSameDay(List<Hiking> hikes, LocalDate currentDay) {
        return hikes.stream().filter(hiking -> hiking.startAsLocalDate().isEqual(currentDay)).collect(Collectors.toList());
    }

    private LocalDate determineEndOfWeek(LocalDate startDate) {
        return startDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); // TODO: 20.05.2019 change to monday
    }

    private LocalDate determineStartOfWeek() {
        // TODO: 20.05.2019 this could be a unit test
        // what happens if today is monday?
        return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); // TODO: 20.05.2019 change to monday
    }

    private List<InputData> createChartData() {
        ArrayList<InputData> dataList = new ArrayList<>();
        dataList.add(new InputData(1));
        dataList.add(new InputData(5));
        dataList.add(new InputData(40));
        dataList.add(new InputData(3));
        dataList.add(new InputData(2));
        dataList.add(new InputData(5));
        dataList.add(new InputData(40));

        long currMillis = System.currentTimeMillis();
        currMillis -= currMillis % TimeUnit.DAYS.toMillis(1);

        for (int i = 0; i < dataList.size(); i++) {
            long position = (long) (dataList.size() - 1 - i);
            long offsetMillis = TimeUnit.DAYS.toMillis(position);

            long millis = currMillis - offsetMillis;
            dataList.get(i).setMillis(millis);
        }

        return dataList;
    }
}
