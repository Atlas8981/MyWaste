package com.example.mywaste.David.model;

import java.io.Serializable;

public class ItemData implements Serializable {

    private String name;
    private String category;
    private int amount;
    private double price;
    private String address;
    private String phone;
    private String description;
    private String imageurl;
    private String imagename;

    public ItemData(){}

    public ItemData(String name, String category, int amount, double price, String address, String phone, String description, String imageurl, String imagename) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.imageurl = imageurl;
        this.imagename = imagename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

}
