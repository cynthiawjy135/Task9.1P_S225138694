package com.example.task7p_s225138694.data;

public class ItemDataModel {
    private int id;
    private String name;
    private String type;
    private String phone;
    private String description;
    private String date;

    private String location;
    private String imagePath;
    private double lat;
    private double longt;


    public ItemDataModel(String nameItem, String typeItem, String phoneItem, String descriptionItem, String dateItem, String locItem, String imagePath, double lat, double longt){
        this.name = nameItem;
        this.type = typeItem;
        this.phone = phoneItem;
        this.description = descriptionItem;
        this.date = dateItem;
        this.location = locItem;
        this.imagePath = imagePath;
        this.lat = lat;
        this.longt = longt;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
    public String getPhone(){
        return phone;
    }
    public String getDescription(){
        return description;
    }
    public String getDate(){
        return date;
    }

    public String getImagePath(){
        return imagePath;
    }

    public double getLat(){
        return lat;
    }

    public double getLong(){
        return longt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLong(double longt) {
        this.longt = longt;
    }


    public void setImagePath(String imgPath) {
        this.imagePath = imgPath;
    }

    public String getLocation() {
        return location;
    }
}
