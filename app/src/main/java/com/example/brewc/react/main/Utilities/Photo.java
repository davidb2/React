package com.example.brewc.react.main.Utilities;

import com.example.brewc.react.main.FirebaseUser.UserFace;
import com.microsoft.projectoxford.face.contract.Face;

/**
 * Stores a photo
 */

public class Photo {
    private String photo;
    private UserFace details;

    public Photo(String photo, UserFace details) {
        this.photo = photo;
        this.details = details;
    }

    private Photo() {}

    public String getPhoto() {
        return this.photo;
    }

    public UserFace getDetails() {
        return this.details;
    }
}
