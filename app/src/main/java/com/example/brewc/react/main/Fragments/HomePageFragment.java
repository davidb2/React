package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brewc.react.R;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Home page
 */

public class HomePageFragment extends Fragment {
    private final String TAG = this.getTag();

    private TextView _userProfileName, _userProfileBio;
    private EditText _inputName, _inputBio;
    private CircleImageView _userProfilePic;


    private FirebaseAuth _auth;
    private FirebaseUser _user;
    private DatabaseReference _rootReference;
    private DatabaseReference _userProfileNameRef;
    private DatabaseReference _userProfileBioRef;
    private DatabaseReference _userProfilePicRef;





    public HomePageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this._rootReference = FirebaseDatabase.getInstance().getReference();
        this._auth = FirebaseAuth.getInstance();
        this._user = this._auth.getCurrentUser();
        this._userProfileNameRef =
                this._rootReference.child("users").child(this._user.getUid()).child("displayName");
        this._userProfileBioRef =
                this._rootReference.child("users").child(this._user.getUid()).child("bio");
        this._userProfilePicRef =
                this._rootReference.child("users").child(this._user.getUid()).child("profilePicture");

        Log.v(TAG, String.format("currentUser: %s", this._user.toString()));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.home_page_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // hide keyboard by default
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);


        this._userProfileName = (TextView) this.getView().findViewById(R.id.user_profile_name);
        this._userProfileBio = (TextView) this.getView().findViewById(R.id.user_profile_bio);
        this._inputName = (EditText) this.getView().findViewById(R.id.input_name);
        this._inputBio = (EditText) this.getView().findViewById(R.id.input_bio);
        this._userProfilePic = (CircleImageView) this.getView().findViewById(R.id.user_profile_photo);

        setDefaults();
        addTextChangedListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        this._userProfileNameRef.setValue(this._inputName.getText().toString().trim());
        this._userProfileBioRef.setValue(this._inputBio.getText().toString().trim());
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    private void setDefaults() {
        Log.v(TAG, "setting defaults");
        this._userProfileNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, dataSnapshot.toString());
                _userProfileName.setText(dataSnapshot.getValue(String.class));
                _inputName.setText(_userProfileName.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this._userProfileBioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _userProfileBio.setText(dataSnapshot.getValue(String.class));
                _inputBio.setText(_userProfileBio.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this._userProfilePicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue(String.class) == null) {
                    return;
                }
                _userProfilePic.setImageBitmap(
                        BitmapUtilities.base64ToBitmap(dataSnapshot.getValue(String.class))
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addTextChangedListeners() {
        this._inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _userProfileName.setText(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this._inputBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _userProfileBio.setText(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
