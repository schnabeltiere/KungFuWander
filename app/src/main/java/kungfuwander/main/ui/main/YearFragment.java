package kungfuwander.main.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kungfuwander.main.R;
import kungfuwander.main.chartview.ChartView;
import kungfuwander.main.chartview.draw.data.InputData;

public class YearFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);
        ChartView chartView = view.findViewById(R.id.chartViewYear);

        List<InputData> chartData = createChartData();
        chartView.setData(chartData);
        return view;
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
