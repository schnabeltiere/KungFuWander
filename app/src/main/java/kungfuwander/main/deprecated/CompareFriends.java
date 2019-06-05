package kungfuwander.main.deprecated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import kungfuwander.main.MainActivity;
import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.helper.FirebaseHelper;

import static kungfuwander.main.deprecated.FriendsList.*;

public class CompareFriends extends AppCompatActivity {

    private static final String TAG = CompareFriends.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_friends);

        Intent intent = getIntent();
        String uuidCompare = intent.getStringExtra(UID_COMPARE);

        // nested so data gets loaded all or nothing
        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            FirebaseHelper.fetchSpecificUserHikes(uuidCompare, compareHikes -> {
                List<DataEntry> dataEntries = extractDataOutOfHikes(hikes, compareHikes);
                initChart(uuidCompare, dataEntries);
            });
        });

    }

    private void initChart(String uuidCompare, List<DataEntry> seriesData) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Win the race against your friend");

        cartesian.yAxis(0).title("Steps");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        Set set = Set.instantiate();
        set.data(seriesData);

        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");

        createLine(cartesian, series1Mapping, MainActivity.currentFirebaseUser.getUid());
        createLine(cartesian, series2Mapping, uuidCompare);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private List<DataEntry> extractDataOutOfHikes(List<Hike> allHikes, List<Hike> allCompareHikes) {
        List<DataEntry> seriesData = new ArrayList<>();

        // makes it easier
        allHikes.sort(Comparator.comparing(Hike::getStart));
        allCompareHikes.sort(Comparator.comparing(Hike::getStart));

        LocalDate previousMonday = determineStartOfWeek();
        LocalDate endOfWeek = determineEndOfWeek(previousMonday);
        Log.d(TAG, "Start is: " + previousMonday + " end is: " + endOfWeek);

        int sumSteps1 = 0, sumSteps2 = 0;
        for (LocalDate currentDay = previousMonday; currentDay.isBefore(endOfWeek); currentDay = currentDay.plusDays(1)) {

            // TODO: 20.05.2019 merge this into map with LocalDate, Steps
            List<Hike> hikesOnSameDay = hikesOnSameDay(allHikes, currentDay);
            List<Hike> hikesOnSameDayCompare = hikesOnSameDay(allCompareHikes, currentDay);

            Log.d(TAG, "Same day: " + hikesOnSameDay.size());
            Log.d(TAG, "Same compare: " + hikesOnSameDayCompare.size());

            // what a mess
            // TODO: 20.05.2019 sort by week, month, year... maybe by tab view
            int steps1 = sumSteps(hikesOnSameDay);
            int steps2 = sumSteps(hikesOnSameDayCompare);

            sumSteps1 += steps1; sumSteps2 += steps2;

            String format = currentDay.format(DateTimeFormatter.ofPattern("dd.MM"));
            seriesData.add(new CustomDataEntry(format, sumSteps1, sumSteps2));
        }

        return seriesData;
    }

    private int sumSteps(List<Hike> allHikes) {
        return allHikes.stream()
                .mapToInt(Hike::getSteps)
                .sum();
    }

    private List<Hike> hikesOnSameDay(List<Hike> hikes, LocalDate currentDay) {
        return hikes.stream().filter(hiking -> hiking.startAsLocalDate().isEqual(currentDay)).collect(Collectors.toList());
    }

    private LocalDate determineEndOfWeek(LocalDate startDate) {
        return startDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)); // TODO: 20.05.2019 change to monday
    }

    private LocalDate determineStartOfWeek() {
        // TODO: 20.05.2019 this could be a unit test
        // what happens if today is monday?
        return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)); // TODO: 20.05.2019 change to monday
    }

    private void createLine(Cartesian cartesian, Mapping series1Mapping, String name) {
        Line series1 = cartesian.line(series1Mapping);
        series1.name(name);
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }

    }
}

