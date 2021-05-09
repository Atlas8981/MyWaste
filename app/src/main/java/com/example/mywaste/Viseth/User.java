package com.example.mywaste.Viseth;

public class User{

    private String fullname,username,email,password, phonenumber,userid;

    public User(){}

//    This one not by creator
        public User(String fullname, String email, String phonenumber,String userid){
            this.fullname = fullname;
            this.email = email;
            this.phonenumber = phonenumber;
            this.userid = userid;
        }
    public User(String fullname, String username, String email, String password, String phonenumber,String userid){
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.userid = userid;
    }

    public User(String fullname, String username, String email, String password, String phonenumber){
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
    }

    //Getter
    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }



    //Setter
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    //Other Not By Creator

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}