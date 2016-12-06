package com.example.brewc.react.main.FirebaseUser;

import com.microsoft.projectoxford.face.contract.FacialHair;

/**
 * Created by brewc on 12/4/2016.
 */

public class UserFacialHair extends FacialHair {
    private double beard, moustache, sideburns;

    public UserFacialHair(double beard, double moustache, double sideburns) {
        this.beard = beard;
        this.moustache = moustache;
        this.sideburns = sideburns;
    }

    private UserFacialHair() {}

    public double getBeard() {
        return beard;
    }

    public double getMoustache() {
        return moustache;
    }

    public double getSideburns() {
        return sideburns;
    }
}
