package com.example.brewc.react.main.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email, userID, displayName;

    public User(String email, String userID) {
        this.email = email;
        this.userID = userID;
        this.displayName = "";
    }

    public String getEmail() {
        return this.email;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getDisplayName() {
        return this.displayName;
    }


}