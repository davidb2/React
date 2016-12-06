package com.example.brewc.react.main.FirebaseUser;

import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.HeadPose;

/**
 * Part of UserFace
 */

public class UserFaceAttribute extends FaceAttribute {
    private double age, smile;
    private String gender;
    private UserHeadPose headPose;
    private UserFacialHair facialHair;

    public UserFaceAttribute(double age, double smile, String gender, UserHeadPose headPose, UserFacialHair facialHair) {
        this.age = age;
        this.smile = smile;
        this.gender = gender;
        this.headPose = headPose;
        this.facialHair = facialHair;
    }

    private UserFaceAttribute() {}

    public double getAge() {
        return age;
    }

    public double getSmile() {
        return smile;
    }

    public String getGender() {
        return gender;
    }

    public UserHeadPose getHeadPose() {
        return headPose;
    }

    public UserFacialHair getFacialHair() {
        return facialHair;
    }
}
