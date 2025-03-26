package com.shopping.nhom5.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String ref;
    private Double totalPrice;
    private List<CartItem> games;
    private String status;
    private String userId;
    private String userEmail;
    private Long createdAt;

    public final static String PENDING = "pending";
    public final static String COMPLETED = "completed";
    public final static String CANCELED = "canceled";

    public Order() {
    }

    public Order(String ref, Double totalPrice, List<CartItem> games, String status, String userId, String userEmail) {
        this.ref = ref;
        this.totalPrice = totalPrice;
        this.games = games;
        this.status = status;
        this.userId = userId;
        this.userEmail = userEmail;
        this.createdAt = new Date().getTime();
    }

    public String getRef() {
        return ref;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public List<CartItem> getGames() {
        return games;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
