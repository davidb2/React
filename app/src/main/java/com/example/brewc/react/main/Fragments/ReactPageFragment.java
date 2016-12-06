package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * React page
 */

public class ReactPageFragment extends Fragment {
    public final String TAG = this.getClass().getSimpleName();
    private final String NONE = "none";
    FirebaseAuth _auth;
    FirebaseUser _user;
    DatabaseReference _rootReference;
    DatabaseReference _matchmaker;
    DatabaseReference _roomsReference;
    Activity _activity;
    public ReactPageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._activity = getActivity();
        this._rootReference = FirebaseDatabase.getInstance().getReference();
        this._matchmaker = this._rootReference.child("matchmaker");
        Log.v(TAG, this._matchmaker.toString());
        if (this._matchmaker == null) {
            this._rootReference.setValue("matchmaker");
            this._matchmaker = this._rootReference.child("matchmaker");
        }
        Log.v(TAG, this._matchmaker.toString());
        this._roomsReference = this._rootReference.child("rooms");
        if (this._roomsReference == null) {
            this._rootReference.setValue("rooms");
            this._roomsReference = this._rootReference.child("rooms");
        }
        this._auth = FirebaseAuth.getInstance();
        this._user = this._auth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        findMatch();
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void findMatch() {
        this._matchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String matchmaker = dataSnapshot.getValue(String.class);
                Log.v(TAG, "matchmaker: " + matchmaker);

                if (matchmaker == null || matchmaker.equals(NONE)) {
                    findMatchFirstArriver();
                } else {
                    findMatchSecondArriver(matchmaker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findMatchFirstArriver() {
        final DatabaseReference databaseReference = _roomsReference.push();
        databaseReference.push().setValue(this._user.getUid());
        final String matchmaker = databaseReference.getKey();

        this._matchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // if no games posted
                if (mutableData.getValue(String.class).equals(NONE)) {
                    mutableData.setValue(matchmaker);
                    return Transaction.success(mutableData);

                } else {
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commit, DataSnapshot dataSnapshot) {
                Toast.makeText(_activity.getApplicationContext(),
                        commit ? "transaction success" : "transaction failed",
                        Toast.LENGTH_SHORT).show();
                if (!commit) {
                    databaseReference.removeValue();
                    findMatch();
                }
            }
        });
    }

    private void findMatchSecondArriver(final String matchmaker) {
        this._matchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(String.class).equals(matchmaker)) {
                    mutableData.setValue(NONE);
                    return Transaction.success(mutableData);
                }
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {
                if (committed) {
                    DatabaseReference gameReference = _roomsReference.child(matchmaker);
                    gameReference.push().setValue(_user.getUid());
                    _matchmaker.setValue(NONE);
                } else {
                    findMatch();
                }
            }
        });
    }
}
