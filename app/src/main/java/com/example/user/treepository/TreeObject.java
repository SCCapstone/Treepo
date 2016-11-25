package com.example.user.treepository;

/**
 * Created by brycebware on 11/23/16.
 */

public class TreeObject {

    private float latitude, longitude;
    private String type, address, description, age, height, lifeSpan;


    public TreeObject() {

    }


    public float getLat() {
        return latitude;
    }

    public void setLat(float latitude) {
        this.latitude = latitude;
    }

    public float getLong() {
        return longitude;
    }

    public void setLong(float longitude) {
        this.longitude = longitude;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height){
        this.height = height;
    }

    public String getLifeSpan(){
        return lifeSpan;
    }

    public void setLifeSpan(String lifespan) {
        this.lifeSpan = lifespan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}