package kungfuwander.main;

import java.util.Date;

public class Wanderung {
    private Date date;
    private int steps;
    private String destination;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
    public double parseToMeter()
    {
        return steps * 0.75;
    }


    //TODO destination selber eingeben
    //TODO date =  new Date()

}