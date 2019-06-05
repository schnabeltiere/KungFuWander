package kungfuwander.main.deprecated;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.helper.FirebaseHelper;

public class TestRecentHikes extends AppCompatActivity {

    private static final String TAG = TestRecentHikes.class.getName();
    private List<Hike> hikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recent_hikings);

        FirebaseHelper.fetchLoggedInUserHikes(this::initChart);
    }

    private void initChart(List<Hike> hikes) {
        this.hikes = hikes;

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        // funny mistake made by me: if they have the same x-name it is the same value
        // they need individual names
        for (int i = 0; i < hikes.size(); i++) {
            Hike h = hikes.get(i);
            ValueDataEntry entry = new ValueDataEntry(i + "", h.getSteps());
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
        cartesian.title("Your recent hikes");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // just naming x and y axis
        cartesian.xAxis(0).title("Days");
        cartesian.yAxis(0).title("Steps");

        anyChartView.setChart(cartesian);

        showInfoOnClick(cartesian);
    }

    private void showInfoOnClick(Cartesian cartesian) {
        cartesian.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                String x = event.getData().get("x");
                String value = event.getData().get("value");

                // TODO: 18.05.2019 remove this cheat with x
                showDialogAbout(hikes.get(Integer.parseInt(x)));
            }
        });
    }

    private void showDialogAbout(Hike hike) {
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View alertView = inflater.inflate(R.layout.alert_about_hiking, null);

        // set default price
        TextView tvDate = alertView.findViewById(R.id.atvDate);
        TextView tvSteps = alertView.findViewById(R.id.atvSteps);
        TextView tvMeter = alertView.findViewById(R.id.atvMeter);

        tvDate.setText("Everything started on " + hike.getStart());
        tvSteps.setText("With small feet you went " + hike.getSteps() + " steps");
        tvMeter.setText("That's about " + hike.inMeter() + " meters. Congrats");

        new AlertDialog.Builder(this)
                .setView(alertView)
                .setPositiveButton("Ok", (dialog, which) -> dialog.cancel())
                .show();
    }
}
