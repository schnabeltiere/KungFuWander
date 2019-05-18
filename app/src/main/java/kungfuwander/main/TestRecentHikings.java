package kungfuwander.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

public class TestRecentHikings extends AppCompatActivity {

    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recent_hikings);

        FireBaseHelper helper = new FireBaseHelper();
        helper.fetchUserHikings(this::initChart);
    }

    private void initChart(List<Hiking> hikings) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        // funny mistake: if they have the same x-name it is the same value
        // they need individual names
        for (int i = 0; i < hikings.size(); i++) {
            Hiking h = hikings.get(i);
            ValueDataEntry entry = new ValueDataEntry(i+"", h.getSteps());
            data.add(entry);
        }

        Log.d(TAG, "Size of Data Entries: " + data.size());

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Your recent hikings");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // just naming x and y axis
        cartesian.xAxis(0).title("Days");
        cartesian.yAxis(0).title("Steps");

        anyChartView.setChart(cartesian);
    }
}
