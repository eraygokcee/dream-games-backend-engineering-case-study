package com.dreamgames.backendengineeringcasestudy.users.model;
import jakarta.persistence.*;
//example
@Entity
@Table(name = "users")

public class User {
    @Id
    private String id;
    private String country;
    private int level;
    private int coins;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
