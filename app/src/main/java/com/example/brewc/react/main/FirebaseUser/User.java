package com.example.brewc.react.main.FirebaseUser;

import android.graphics.Bitmap;
import android.util.Base64;

import com.example.brewc.react.main.Utilities.Photo;
import com.google.firebase.database.Exclude;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class User {
    private String email, userID, displayName, phoneNumber, profilePicture, bio;
    private UserFace details;
    private Map<String, Photo> database;
    private List<String> contacts;
    private UserUUID personId;

    private User() {}

    public User(String email,
                String userID,
                String displayName,
                Face details,
                String phoneNumber,
                String profilePicture) {
        this.email = email.trim();
        this.userID = userID;
        this.displayName = displayName.trim();
        this.details = new UserFace(details);
        this.database = new HashMap<>();
        this.contacts = new ArrayList<String>();
        this.phoneNumber = phoneNumber.trim().replaceAll("[^0-9]", "");
        this.profilePicture = profilePicture;
        this.bio = "";
        this.personId = null;
    }

    @Exclude
    public void addFaceToDatabase(String photo, Face face) {
        this.database.put("f9e8fu398afuw9", new Photo(photo, new UserFace(face)));
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

    public UserFace getDetails() {
        return this.details;
    }

    public Map<String, Photo> getDatabase() {
        return this.database;
    }

    public List<String> getContacts() {
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

    @Exclude
    public void setBio(String message) {
        this.bio = message;
    }

    @Exclude
    public void addContact() {
        this.contacts.add("DvMzHKD0fdbjAVUCw9a5XJZEeYb2");
    }

    public UserUUID getPersonId() {
        return this.personId;
    }

    @Exclude
    public void setPersonId(UUID uuid) {
        this.personId = new UserUUID(uuid.getLeastSignificantBits(), uuid.getMostSignificantBits());
    }
}