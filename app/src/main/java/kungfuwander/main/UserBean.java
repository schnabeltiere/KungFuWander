package kungfuwander.main;

import java.util.Objects;

public class UserBean {
    // therefore they must have a name, store friends in subcollection only with uid?
    private String uid;
    private String name;

    public UserBean(){ }

    public UserBean(String uid) {
        this.uid = uid;
        // TODO: 21.05.2019 need this at creation - or at least not allow to proceed without name
        this.name = "default_name";
    }

    public UserBean(String uid, String name) {
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
        return "UserBean{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBean userBean = (UserBean) o;
        return Objects.equals(name, userBean.name) &&
                Objects.equals(uid, userBean.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uid);
    }
}
