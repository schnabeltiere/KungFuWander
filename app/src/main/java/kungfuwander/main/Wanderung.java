package kungfuwander.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wanderung {
    private long startPointSince1970;
    private long endPointSince1970;
    private int steps;
    private List<MyLocation> locations;

    public Wanderung() {
        startPointSince1970 = -1; // TODO: 16.05.2019 change later, it's default
        endPointSince1970 = -1;
        steps = -1;
        locations = new ArrayList<>();
        locations.add(new MyLocation(13, 24));
        locations.add(new MyLocation(132, -32));
        locations.add(new MyLocation(11, 242));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wanderung wanderung = (Wanderung) o;
        return startPointSince1970 == wanderung.startPointSince1970 &&
                endPointSince1970 == wanderung.endPointSince1970 &&
                steps == wanderung.steps &&
                Objects.equals(locations, wanderung.locations);
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