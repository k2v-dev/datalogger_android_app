package com.decalthon.helmet.stability.firestore;

public class FirestoreProfileModel {
    private String name = "";
    private float height = 0.0f;
    private float weight = 0.0f;
    private long dob = 0;
    private String gender = "M";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getHeight() {

        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
