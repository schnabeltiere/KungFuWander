package kungfuwander.main;

public class UserBean {
    private int cheat;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserBean(){

    }
    public UserBean(int cheat) {
        this.cheat = cheat;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "cheat=" + cheat +
                ", name='" + name + '\'' +
                '}';
    }

    public int getCheat() {
        return cheat;
    }

    public void setCheat(int cheat) {
        this.cheat = cheat;
    }
}
