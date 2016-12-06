package com.example.brewc.react.main.FirebaseUser;

import com.microsoft.projectoxford.face.contract.FeatureCoordinate;

/**
 * Allows reflection
 */

public class UserFeatureCoordinate extends FeatureCoordinate {
    private double x, y;

    public UserFeatureCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    private UserFeatureCoordinate() {}

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

}
