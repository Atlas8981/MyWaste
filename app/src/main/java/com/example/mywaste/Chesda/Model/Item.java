package com.example.mywaste.Chesda.Model;


import java.io.Serializable;


public class Item implements Serializable{

    private String itemid;
    private String name;
    private String address;
    private String category;
    private String description;
    private String userid;
    private String phone;
    private String imagename;
    private String imageurl;
    private int amount;
    private double price;

//    Without empty constructor firestore won't work
    public Item (){}
    public Item(String name, String address, String category, String description, String userid, String phone, int amount, double price, String imageurl,String imagename) {
        this.itemid = itemid;
        this.name = name;
        this.address = address;
        this.category = category;
        this.description = description;
        this.userid = userid;
        this.amount = amount;
        this.price = price;
        this.imageurl = imageurl;
        this.phone = phone;
        this.imagename = imagename;
    }
    public Item(String itemid,String name, String address, String category, String description, String userid, String phone, int amount, double price, String imageurl,String imagename) {
        this.itemid = itemid;
        this.name = name;
        this.address = address;
        this.category = category;
        this.description = description;
        this.userid = userid;
        this.amount = amount;
        this.price = price;
        this.imageurl = imageurl;
        this.phone = phone;
        this.imagename = imagename;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", userid='" + userid + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }

}

