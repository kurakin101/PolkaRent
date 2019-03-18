package com.polka.rentplace.model;

public class AdminOrders {

    private String name, lastName, fatherName, phone, phoneOrder, address, city, state, date, time, totalAmount;


    public AdminOrders() {

    }

    public AdminOrders(String name, String lastName, String fatherName, String phone, String phoneOrder, String address, String city, String state, String date, String time, String totalAmount) {
        this.name = name;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.phone = phone;
        this.phoneOrder = phoneOrder;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneOrder() {
        return phoneOrder;
    }

    public void setPhoneOrder(String phoneOrder) {
        this.phoneOrder = phoneOrder;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
