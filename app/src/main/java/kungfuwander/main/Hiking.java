package kungfuwander.main;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

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
//        locations = new ArrayList<>();
////        locations.add(new MyLocation(13, 24));
////        locations.add(new MyLocation(132, -32));
////        locations.add(new MyLocation(-32, 242));
////        locations.add(new MyLocation(-21, -12));
////        locations.add(new MyLocation(11, 65));
////        locations.add(new MyLocation(79, 23));
//        locations.add(new MyLocation(10, 23));
//        locations.add(new MyLocation(15, 23));
//        locations.add(new MyLocation(20, 23));
//        locations.add(new MyLocation(25, 28));
//        locations.add(new MyLocation(30, 28));
//        locations.add(new MyLocation(35, 28));
//        locations.add(new MyLocation(40, 28));
//        locations.add(new MyLocation(45, 33));
        geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(10, 20));
        geoPoints.add(new GeoPoint(23, 13));
        geoPoints.add(new GeoPoint(23, 77));
        geoPoints.add(new GeoPoint(54, 36));
    }

    public List<LatLng> locationsAsLatLng(){
        return locations.stream()
                .map(MyLocation::toLatLng)
                .collect(Collectors.toList());
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

//
//    public List<MyLocation> getLocations() {
//        return locations;
//    }
//
//    public void setLocations(List<MyLocation> locations) {
//        this.locations = locations;
//    }


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

}