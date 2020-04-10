package com.decalthon.helmet.stability.firestore;

public class FirestoreUserModel {

    private String name;

    private String email;

    private String phone_no;


    public FirestoreUserModel() {
    }


    public String getName() {

        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email.toUpperCase();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }
}
