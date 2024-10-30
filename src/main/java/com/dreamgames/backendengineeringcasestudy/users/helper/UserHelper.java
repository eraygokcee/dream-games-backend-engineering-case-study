package com.dreamgames.backendengineeringcasestudy.users.helper;

import com.dreamgames.backendengineeringcasestudy.users.constant.Country;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class UserHelper {
    public String randomCountry() {
        Country[] countries = Country.values();
        int randomIndex = new Random().nextInt(countries.length);
        return countries[randomIndex].name();
    }
}
