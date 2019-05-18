package kungfuwander.main;

import java.util.List;

public class User {
    private String name;
    private String country;
    // consider at some point Map<Hiking, List<MyLocation>>
    private List<Hiking> hiking;


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

    public List<Hiking> getHiking() {
        return hiking;
    }

    public void setHiking(List<Hiking> hiking) {
        this.hiking = hiking;
    }

}
