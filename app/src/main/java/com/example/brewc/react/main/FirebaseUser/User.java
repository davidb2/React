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
    private String email, userID, displayName, phoneNumber, proflePicture, bio;
    private Face details;
    private Set<Face> database;
    private Set<User> contacts;

    public User(String email,
                String userID,
                String displayName,
                Face details,
                String phoneNumber,
                String proflePicture) {
        this.email = email.trim();
        this.userID = userID;
        this.displayName = displayName.trim();
        this.details = details;
        this.database = new HashSet<Face>();
        this.contacts = new HashSet<User>();
        this.phoneNumber = phoneNumber.trim().replaceAll("[^0-9]", "");
        this.proflePicture = proflePicture;
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

    public Set<Face> getDatabase() {
        return this.database;
    }

    public Set<User> getContacts() {
        return this.contacts;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getProflePicture() {
        return this.proflePicture;
    }

    public String getBio() {
        return this.bio;
    }
}