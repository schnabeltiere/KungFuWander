package kungfuwander.main;

import java.util.List;
import java.util.UUID;

public class User {
    private String UsrName,UsrEmail;
    private int hikingCount, friendcount, challangeCount;
    private List<User> friends;
    private String UsrId;
    public static class Builder {

        private String UsrName,UsrEmail;
        private int hikingCount, friendcount, challangeCount;
        private List<User> friends;
        private String UsrId;

        public Builder(){
            UsrId = UUID.randomUUID().toString();
        }


        public Builder setUsrName(String UsrName){
            this.UsrName = UsrName;
            return this;
        }

        public Builder setUsrEmail(String UsrEmail){
            this.UsrEmail = UsrEmail;
            return this;
        }

        public Builder setHikingCount(int hcount){
            this.hikingCount = hcount;
            return this;
        }

        public Builder setFriendsCount(int fcount){
            this.friendcount  = fcount;
            return this;
        }

        public Builder setChallangeCount(int ccount){
            this.challangeCount = ccount;
            return this;
        }

        public Builder setFriendList(List<User> friends){
            this.friends = friends;
            return this;
        }

        public Builder setFriend(User u){
            friends.add(u);
            return this;
        }

        public User create(){
            User user = new User();
            user.UsrId = this.UsrId;
            user.UsrName = this.UsrName;
            user.UsrEmail = this.UsrEmail;
            user.friendcount = this.friendcount;
            user.challangeCount = this.challangeCount;
            user.hikingCount = this.hikingCount;
            user.friends = this.friends;
            return user;
        }


    }

    private User(){

    }
}
