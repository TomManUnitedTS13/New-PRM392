package com.example.a30shinehaircutapp.Main;

public class Service {
    private String name;
    private String description;
    private int price;
    private int duration;
    private int imageUrl;

    public Service(String name, String description, int price, int duration, int imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
