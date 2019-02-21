package com.polka.rentplace.model;

import android.content.res.Resources;

public class Cart {
    private String pid, pname, price, quantiny, duscount,image;

    public Cart() {
    }



    public Cart(String pid, String pname, String price, String quantiny, String duscount, String image) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantiny = quantiny;
        this.duscount = duscount;
        this.image = image;



    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantiny() {
        return quantiny;
    }

    public void setQuantiny(String quantiny) {
        this.quantiny = quantiny;
    }

    public String getDuscount() {
        return duscount;
    }

    public void setDuscount(String duscount) {
        this.duscount = duscount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
