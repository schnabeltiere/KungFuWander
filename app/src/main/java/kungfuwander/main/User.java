package kungfuwander.main;

import java.util.List;

public class User {
    private String name;
    private String country;
    private List<Wanderung> wanderung;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Wanderung> getWanderung() {
        return wanderung;
    }

    public void setWanderung(List<Wanderung> wanderung) {
        this.wanderung = wanderung;
    }

}
