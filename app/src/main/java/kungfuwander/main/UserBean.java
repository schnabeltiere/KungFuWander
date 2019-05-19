package kungfuwander.main;

public class UserBean {
    // TODO: 19.05.2019 consider creating database for each user immediately
    // therefore they must have a name, store friends in subcollection only with uuid?
    private int cheat;
    private String name;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
