package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {

    private Integer orderID;
    private Integer userID;
    private LocalDate date;
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal amount;

    public Order() {}

    public Order(Integer orderID, Integer userID, LocalDate date, String address, String city, String state, String zip, BigDecimal amount) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.amount = amount;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
