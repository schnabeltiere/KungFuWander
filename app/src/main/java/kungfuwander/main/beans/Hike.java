package kungfuwander.main.beans;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hike {
    private int steps;
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

    public Hike() {
        geoPoints = new ArrayList<>();
    }

    public void addGeoPoint(GeoPoint geoPoint){
        geoPoints.add(geoPoint);
    }

    public List<LatLng> locationsAsLatLng(){
        return geoPoints.stream()
                .map(gp -> new LatLng(gp.getLatitude(), gp.getLongitude()))
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
        Hike hike = (Hike) o;
        return steps == hike.steps &&
                Objects.equals(geoPoints, hike.geoPoints) &&
                Objects.equals(start, hike.start) &&
                Objects.equals(end, hike.end);
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
        return "Hike{" +
                "steps=" + steps +
                ", geoPoints=" + geoPoints +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}