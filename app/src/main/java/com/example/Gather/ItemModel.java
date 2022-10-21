package com.example.Gather;

import java.util.Date;

public class ItemModel {
    String name;
    String brand;
    String category;
    String last_updated_by;
    String group_name;
    Date expiration_date;
    Date last_updated_date;
    Integer quantity;
    Integer barcode;
    Integer rating;
    Integer priority;
    Double percentage;
    Double price;

    public ItemModel(String name, String brand, String category, String last_updated_by,
                     String group_name, Date expiration_date, Date last_updated_date, Integer quantity,
                     Integer barcode, Integer rating, Integer priority, Double percentage, Double price) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.last_updated_by = last_updated_by;
        this.group_name = group_name;
        this.expiration_date = expiration_date;
        this.last_updated_date = last_updated_date;
        this.quantity = quantity;
        this.barcode = barcode;
        this.rating = rating;
        this.priority = priority;
        this.percentage = percentage;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getLast_updated_by() {
        return last_updated_by;
    }

    public String getGroup_name() {
        return group_name;
    }

    public Date getExpiration_date() {
        return expiration_date;
    }

    public Date getLast_updated_date() {
        return last_updated_date;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getBarcode() {
        return barcode;
    }

    public Integer getRating() {
        return rating;
    }

    public Integer getPriority() {
        return priority;
    }

    public Double getPercentage() {
        return percentage;
    }

    public Double getPrice() {
        return price;
    }
}
