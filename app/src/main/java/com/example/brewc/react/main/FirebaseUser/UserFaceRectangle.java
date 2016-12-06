package com.example.brewc.react.main.FirebaseUser;

import com.microsoft.projectoxford.face.contract.FaceRectangle;

/**
 * Created by brewc on 12/4/2016.
 */

public class UserFaceRectangle extends FaceRectangle {
    private int height, left, top, width;

    public UserFaceRectangle(int height, int left, int top, int width) {
        this.height = height;
        this.left = left;
        this.top = top;
        this.width = width;
    }

    private UserFaceRectangle() {}

    public int getHeight() {
        return height;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }
}
