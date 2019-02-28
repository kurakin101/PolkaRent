package com.polka.rentplace.model;

import android.content.res.Resources;

public class Cart {
    private String pid, pname, price, quantity, duscount,image,phone;

    public Cart() {
    }



    public Cart(String pid, String phone, String pname, String price, String quantity, String duscount, String image) {
        this.pid = pid;
        this.phone = phone;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.duscount = duscount;
        this.image = image;



    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
