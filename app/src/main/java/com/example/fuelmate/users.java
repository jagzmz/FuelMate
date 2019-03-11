package com.example.fuelmate;

public class users {

    public String name,colg,image;


    public users()
    {
        name=null;
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
