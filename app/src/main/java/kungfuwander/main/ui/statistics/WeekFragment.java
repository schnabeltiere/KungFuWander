package kungfuwander.main.ui.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.R;
import kungfuwander.main.chartview.ChartView;
import kungfuwander.main.chartview.draw.data.InputData;

public class WeekFragment extends Fragment {

    private static final String TAG = WeekFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChartView chartView = view.findViewById(R.id.chartViewWeek);

        // nested so data gets loaded all or nothing
        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            List<InputData> inputData = extractDataOutOfWeek(hikes);
            chartView.setData(inputData);
        });
    }

    private List<InputData> extractDataOutOfWeek(List<Hike> hikes) {
        List<InputData> data = new ArrayList<>();

        // makes it easier
        hikes.sort(Comparator.comparing(Hike::getStart));

        LocalDate startOfWeek = startOfWeek();
        LocalDate endOfWeek = endOfWeek(startOfWeek);
        Log.d(TAG, "Start is: " + startOfWeek + " end is: " + endOfWeek);

        for (LocalDate currentDay = startOfWeek; currentDay.isBefore(endOfWeek);
             currentDay = currentDay.plusDays(1)) {

            List<Hike> hikesOnSameDay = hikesOnSameDay(hikes, currentDay);
            Log.d(TAG, "Same day: " + hikesOnSameDay.size());

            int steps = sumSteps(hikesOnSameDay);

//            String format = currentDay.format(DateTimeFormatter.ofPattern("dd.MM"));
            // TODO: 02.06.2019 change back
            data.add(new InputData(steps+currentDay.getDayOfMonth()));
        }

        setUpMillisForData(data);
        return data;
    }
    private List<Hike> hikesOnSameDay(List<Hike> hikes, LocalDate currentDay) {
        return hikes.stream()
                .filter(hiking -> hiking.startAsLocalDate().isEqual(currentDay))
                .collect(Collectors.toList());
    }

    private LocalDate endOfWeek(LocalDate startDate) {
        return startDate
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); // TODO: 20.05.2019 change to monday
    }

    private LocalDate startOfWeek() {
        // TODO: 20.05.2019 this could be a unit test
        // what happens if today is monday?
        return LocalDate.now()
                .with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); // TODO: 20.05.2019 change to monday
    }
    
    private int sumSteps(List<Hike> allHikes) {
        return allHikes.stream()
                .mapToInt(Hike::getSteps)
                .sum();
    }

    private void setUpMillisForData(List<InputData> data) {
        long currMillis = System.currentTimeMillis();
        currMillis -= currMillis % TimeUnit.DAYS.toMillis(1);

        for (int i = 0; i < data.size(); i++) {
            long position = (long) (data.size() - 1 - i);
            long offsetMillis = TimeUnit.DAYS.toMillis(position);

            long millis = currMillis - offsetMillis;
            Log.wtf(TAG, "And now the millis: " + millis);

            // ok like for real what is this shit?
            // i mean how can i control the text on the bottom?
            data.get(i).setMillis(millis);
        }
    }

}
