package kungfuwander.main;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hiking {
    private long startPointSince1970;
    private long endPointSince1970;
    private int steps;
    private List<MyLocation> locations;

    public Hiking() {
        startPointSince1970 = -1; // TODO: 16.05.2019 change later, it's default
        endPointSince1970 = -1;
        steps = -1;
        locations = new ArrayList<>();
        locations.add(new MyLocation(13, 24));
        locations.add(new MyLocation(132, -32));
        locations.add(new MyLocation(-32, 242));
        locations.add(new MyLocation(-21, -12));
        locations.add(new MyLocation(11, 65));
        locations.add(new MyLocation(79, 23));
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
        return startPointSince1970 == hiking.startPointSince1970 &&
                endPointSince1970 == hiking.endPointSince1970 &&
                steps == hiking.steps &&
                Objects.equals(locations, hiking.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPointSince1970, endPointSince1970, steps, locations);
    }

    public long getStartPointSince1970() {
        return startPointSince1970;
    }

    public void setStartPointSince1970(long startPointSince1970) {
        this.startPointSince1970 = startPointSince1970;
    }

    public long getEndPointSince1970() {
        return endPointSince1970;
    }

    public void setEndPointSince1970(long endPointSince1970) {
        this.endPointSince1970 = endPointSince1970;
    }

    public List<MyLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<MyLocation> locations) {
        this.locations = locations;
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

    //TODO destination selber eingeben
    //TODO date =  new Date()
}