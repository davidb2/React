package com.example.brewc.react;

import com.example.brewc.react.main.Activities.RegisterPageActivity;
import com.example.brewc.react.main.FirebaseUser.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public final String
            email = "example@gmail.com",
            userID = "abc123",
            displayName = "Ken Bone",
            phoneNumber = "(555) 555-5555",
            profilePicture = "daflqfij34oifj49fuqjolafjnkg3l9j3o44ijj";
    public String id;
    DatabaseReference _rootReference;
    @Before
    public void setUp() throws Exception {
        // add a custom object to the database
        _rootReference = FirebaseDatabase.getInstance().getReference();
        User user = new User(email, userID, displayName, null, phoneNumber, profilePicture);
        this.id = _rootReference.child("users").push().getKey();
        _rootReference.child("users").child(id).setValue(user);
    }

    @Test
    public void test() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        // retrieve the data and check its validity
        _rootReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assertEquals(email, user.getEmail());
                assertEquals(userID, user.getUserID());
                assertEquals(displayName, user.getDisplayName());
                assertEquals(phoneNumber, user.getPhoneNumber());
                assertEquals(profilePicture, user.getProfilePicture());
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }
}