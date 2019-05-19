package kungfuwander.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kungfuwander.main.FriendsList.*;

public class CompareFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_friends);

        Intent intent = getIntent();
        String uuidCompare = intent.getStringExtra(UUID_COMPARE);

        // nested so data gets loaded all or nothing
        FireBaseHelper.fetchLoggedInUserHikings(hikings -> {
            FireBaseHelper.fetchSpecificUserHikings(uuidCompare, compareHikings -> {
                List<DataEntry> dataEntries = extractDataOutOfHikings(hikings, compareHikings);
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

    private List<DataEntry> extractDataOutOfHikings(List<Hiking> hikings, List<Hiking> compareHikings) {
        List<DataEntry> seriesData = new ArrayList<>();

        // makes it easier
        hikings.sort(Comparator.comparingLong(Hiking::getStartPointSince1970));
        compareHikings.sort(Comparator.comparingLong(Hiking::getStartPointSince1970));

        // TODO: 19.05.2019 just for testing start with 1 and go up to 5
        // sum all steps

        int steps1 = 0, steps2 = 0;

        for (int i = 1; i < 6; i++){
            int finalI = i;
            // what a mess
            Hiking hiking1 = hikings.stream().filter(hiking -> hiking.getStartPointSince1970() == finalI).findFirst().orElse(null);
            Hiking hiking2 = compareHikings.stream().filter(hiking -> hiking.getStartPointSince1970() == finalI).findFirst().orElse(null);

            steps1 += hiking1 == null ? 0 : hiking1.getSteps();
            steps2 += hiking2 == null ? 0 : hiking2.getSteps();

            seriesData.add(new CustomDataEntry(i+"", steps1, steps2));
        }

        return seriesData;
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

