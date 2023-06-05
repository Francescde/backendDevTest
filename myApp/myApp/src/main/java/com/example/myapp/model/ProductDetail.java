package com.example.myapp.model;

import java.io.Serializable;
import java.util.Objects;

public class ProductDetail implements Serializable {
    private String id;
    private String name;
    private double price;
    private boolean availability;

    public ProductDetail() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductDetail other = (ProductDetail) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(price, other.price) && Objects.equals(availability, other.availability);
    }
}
