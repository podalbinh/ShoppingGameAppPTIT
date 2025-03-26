package com.shopping.nhom5.models;

public class CartItem {
    private String id;
    private String title;
    private String story;
    private String price;
    private String img;
    private int amount;

    public CartItem() {
    }

    public CartItem(String id, String title, String story, String price, String img) {
        this.id = id;
        this.title = title;
        this.story = story;
        this.price = price;
        this.img = img;
        this.amount = 1;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStory() {
        return story;
    }

    public String getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void plus() {
        amount++;
    }

    public void minus() {
        if (amount > 1) {
            amount--;
        }
    }
}
