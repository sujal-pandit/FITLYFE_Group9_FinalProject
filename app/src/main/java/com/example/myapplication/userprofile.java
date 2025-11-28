package com.example.myapplication;

public class userprofile {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private String birthday;

    public userprofile() {
        // empty constructor for Firestore
    }

    public userprofile(String uid, String name, String email, String phone, String birthday) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBirthday() { return birthday; }

    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
}
