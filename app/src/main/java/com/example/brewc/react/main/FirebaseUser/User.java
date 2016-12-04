package com.example.brewc.react.main.FirebaseUser;

import android.graphics.Bitmap;
import android.util.Base64;

import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String email, userID, displayName, phoneNumber, profilePicture, bio;
    private Face details;
    private ArrayList<Face> database;
    private ArrayList<User> contacts;

    public User(String email,
                String userID,
                String displayName,
                Face details,
                String phoneNumber,
                String profliePicture) {
        this.email = email.trim();
        this.userID = userID;
        this.displayName = displayName.trim();
        this.details = details;
        this.database = new ArrayList<Face>();
        this.contacts = new ArrayList<User>();
        this.phoneNumber = phoneNumber.trim().replaceAll("[^0-9]", "");
        this.profilePicture = profliePicture;
        this.bio = "";
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

    public Face getDetails() {
        return this.details;
    }

    public ArrayList<Face> getDatabase() {
        return this.database;
    }

    public ArrayList<User> getContacts() {
        return this.contacts;
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

    public void addContact() {
        this.contacts.add(new User(
                "example@gmail.com",
                "fasdlkfjs;dlkf",
                "Sean Combs",
                null,
                "4352432523",
                "faslkfj;dslkfj;kds;"
        ));
    }
}