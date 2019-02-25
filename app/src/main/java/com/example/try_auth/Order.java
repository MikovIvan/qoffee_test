package com.example.try_auth;

public class Order {

    String id, name, coffee, time, coffeeHouse, orderStatus, coffeeHouseName;

    public Order() {

    }

    public Order(String name, String coffee, String time, String coffeeHouse) {
        this.name = name;
        this.coffee = coffee;
        this.time = time;
        this.coffeeHouse = coffeeHouse;
    }

    public Order(String id, String name, String coffee, String time, String coffeeHouse) {
        this.id = id;
        this.name = name;
        this.coffee = coffee;
        this.time = time;
        this.coffeeHouse = coffeeHouse;
    }

    public Order(String id, String name, String coffee, String time, String coffeeHouse, String coffeeHouseName, String orderStatus) {
        this.id = id;
        this.name = name;
        this.coffee = coffee;
        this.time = time;
        this.coffeeHouse = coffeeHouse;
        this.orderStatus = orderStatus;
        this.coffeeHouseName = coffeeHouseName;
    }

    public String getCoffeeHouseName() {
        return coffeeHouseName;
    }

    public void setCoffeeHouseName(String coffeeHouseName) {
        this.coffeeHouseName = coffeeHouseName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCoffeeHouse() {
        return coffeeHouse;
    }

    public void setCoffeeHouse(String coffeeHouse) {
        this.coffeeHouse = coffeeHouse;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoffee() {
        return coffee;
    }

    public void setCoffee(String coffee) {
        this.coffee = coffee;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
