package com.example.brewc.react.main.FirebaseUser;

/**
 * Contains name, phonenumber, and picture
 */

public class SimplifiedUser {
    private String name, phoneNumber, profilePicture, bio;

    public SimplifiedUser(String name, String phoneNumber, String profilePicture, String bio) {
        this.name           = name;
        this.phoneNumber    = phoneNumber;
        this.profilePicture = profilePicture;
        this.bio            = bio;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public String getBio() {
        return this.bio;
    }
}
