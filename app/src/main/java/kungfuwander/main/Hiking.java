package kungfuwander.main;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hiking {
    private int steps;
    private List<MyLocation> locations;
    private List<GeoPoint> geoPoints;

    private Timestamp start;
    private Timestamp end;

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Hiking() {
        // TODO: 20.05.2019 remove this - only for testing
        geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(10, 20));
        geoPoints.add(new GeoPoint(23, 13));
        geoPoints.add(new GeoPoint(23, 77));
        geoPoints.add(new GeoPoint(54, 36));
    }

    @Deprecated // maybe need this later
    public List<LatLng> locationsAsLatLng(){
        return locations.stream()
                .map(MyLocation::toLatLng)
                .collect(Collectors.toList());
    }

    public LocalDate startAsLocalDate(){
        return start.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public LocalDate endAsLocalDate(){
        return end.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hiking hiking = (Hiking) o;
        return steps == hiking.steps &&
                Objects.equals(geoPoints, hiking.geoPoints) &&
                Objects.equals(start, hiking.start) &&
                Objects.equals(end, hiking.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps, geoPoints, start, end);
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double inMeter()
    {
        return steps * 0.75;
    }

    @Override
    public String toString() {
        return "Hiking{" +
                "steps=" + steps +
                ", locations=" + locations +
                ", geoPoints=" + geoPoints +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}