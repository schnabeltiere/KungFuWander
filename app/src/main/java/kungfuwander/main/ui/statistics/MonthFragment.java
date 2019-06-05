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

import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.chartview.ChartView;
import kungfuwander.main.chartview.draw.data.InputData;
import kungfuwander.main.helper.FirebaseHelper;

public class MonthFragment extends Fragment {

    private static final String TAG = MonthFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChartView chartView = view.findViewById(R.id.chartViewMonth);

        // nested so data gets loaded all or nothing
        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            List<InputData> inputData = extractDataOutOfMonth(hikes);
            chartView.setData(inputData);
        });
    }

    private List<InputData> extractDataOutOfMonth(List<Hike> hikes) {
        List<InputData> data = new ArrayList<>();

        // makes it easier
        hikes.sort(Comparator.comparing(Hike::getStart));

        LocalDate startOfMonth = startOfMonth();
        LocalDate endOfMonth = endOfMonth();
        Log.d(TAG, "Start is: " + startOfMonth + " end is: " + endOfMonth);

        final int NUMBER_OF_WEEKS = 4;
        for (int i = 0; i < NUMBER_OF_WEEKS; i++){
            LocalDate startOfWeek = LocalDate.now().plusWeeks(i);
            List<Hike> hikesInWeek = extractOfWeek(hikes, startOfWeek);
            int steps = sumSteps(hikesInWeek);

            // TODO: 05.06.2019 change back
            data.add(new InputData(steps + i));
        }

        setUpMillisForData(data);
        return data;
    }

    private LocalDate startOfMonth() {
        return LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth());
    }
    private LocalDate endOfMonth(){
        return LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    private List<Hike> extractOfWeek(List<Hike> hikes, LocalDate week) {
        // because it's not included
        LocalDate startOfWeek = week.minusDays(1);
        LocalDate endOfWeek = week.plusDays(7);

        return hikes.stream()
                .filter(hike -> hike.startAsLocalDate().isAfter(startOfWeek)
                        && hike.startAsLocalDate().isBefore(endOfWeek))
                .collect(Collectors.toList());
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
