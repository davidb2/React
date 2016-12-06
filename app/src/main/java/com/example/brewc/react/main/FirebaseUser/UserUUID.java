package com.example.brewc.react.main.FirebaseUser;

import java.util.UUID;

/**
 * Part of UserFace
 */

public class UserUUID {
    private long leastSignificantBits, mostSignificantBits;

    public UserUUID(long leastSignificantBits, long mostSignificantBits) {
        this.leastSignificantBits = leastSignificantBits;
        this.mostSignificantBits = mostSignificantBits;
    }

    private UserUUID() {}

    public long getLeastSignificantBits() {
        return this.leastSignificantBits;
    }

    public long getMostSignificantBits() {
        return this.mostSignificantBits;
    }
}
