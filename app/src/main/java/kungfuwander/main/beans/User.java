package kungfuwander.main.beans;

import java.util.Objects;

public class User {
    // therefore they must have a name, store friends in subcollection only with uid?
    private String uid;
    private String name;

    public User(){ }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uid);
    }
}
