package com.example.brewc.react;

import com.example.brewc.react.main.Activities.RegisterPageActivity;
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
    DatabaseReference _rootReference;
    @Before
    public void setUp() throws Exception {
        _rootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Test
    public void test() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);

        _rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertEquals("expected value", dataSnapshot.getValue(String.class));
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }
}