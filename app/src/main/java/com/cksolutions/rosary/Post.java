package com.cksolutions.rosary;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    private String age;
    private String mobile;
    private String name;
    private String parish;
    private String country;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(com.cksolutions.rosary.Post.class)
    }

    public Post(String age, String mobile, String name, String parish, String country) {
        this.age = age;
        this.mobile = mobile;
        this.name = name;
        this.parish = parish;
        this.country = country;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("age", age);
        result.put("mobile", mobile);
        result.put("name", name);
        result.put("parish", parish);
        result.put("country", country);

        return result;
    }
}
