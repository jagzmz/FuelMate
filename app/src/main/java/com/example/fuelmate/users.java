package com.example.fuelmate;

public class users {

    public String name;
    public String colg;
    public String image;
    private String uid;

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String cell;


    public users() {
        name = null;
    }

    public users(String name, String colg, String cell) {
        this.name = name;
        this.colg = colg;
        this.image = image;
        this.cell = cell;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public users(String uid, String name, String colg, String cell) {
        this.uid = uid;
        this.name = name;
        this.colg = colg;
        this.image = image;
        this.cell = cell;
    }

    public users(String name, String colg) {
        this.name = name;
        this.colg = colg;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColg() {
        return colg;
    }

    public void setColg(String colg) {
        this.colg = colg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
