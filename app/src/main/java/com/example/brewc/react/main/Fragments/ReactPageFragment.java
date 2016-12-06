package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brewc.react.R;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * React page
 */

public class ReactPageFragment extends Fragment {
    private final long TIME_TO_REACT = 5000L;
    public final String TAG = this.getClass().getSimpleName();
    private final String NONE = "none";
    private boolean _iClicked, _otherPersonClicked;
    FirebaseAuth _auth;
    FirebaseUser _user;
    DatabaseReference _rootReference;
    DatabaseReference _matchmaker;
    DatabaseReference _roomsReference;
    Activity _activity;
    ValueEventListener _valueEventListener, _valueEventListener1;


    AppCompatButton _matchButton;
    TextView _name;
    CircleImageView _profilePic;


    public ReactPageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._iClicked = false;
        this._otherPersonClicked = false;
        this._valueEventListener = null;
        this._valueEventListener1 = null;
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
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.react_page_fragment, null);
        this._matchButton = (AppCompatButton) rootView.findViewById(R.id.match_button);
        this._name = (TextView) rootView.findViewById(R.id.user_name);
        this._profilePic = (CircleImageView) rootView.findViewById(R.id.user_profile_photo);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        findMatch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void startMatch(final String matchmakerKey) {
        final DatabaseReference roomReference = this._roomsReference.child(matchmakerKey);
        _valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> users = (HashMap<String, String> ) dataSnapshot.getValue();
                String otherUserId = null;
                for (String id : users.keySet()) {
                    if (!users.get(id).equals(_user.getUid())) {
                        otherUserId = users.get(id);
                        break;
                    }
                }
                if (otherUserId == null || users.size() > 2) {
                    Log.e(TAG, "something went wrong...");
                    roomReference.removeEventListener(this);
                    roomReference.setValue(null);
                    return;
                }
                final String someUserId = otherUserId;
                _valueEventListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String localOtherUserId = someUserId;
                        HashMap<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (user == null) {
                            Log.wtf(TAG, "user is null!!!!!");
                        }
                        _matchButton.setBackgroundColor(Color.parseColor("#00E676"));
                        _matchButton.setText(getResources().getString(R.string.match));
                        _profilePic.setImageBitmap(BitmapUtilities.base64ToBitmap((String) user.get("profilePicture")));
                        _name.setText((String) user.get("displayName"));
                        Log.v(TAG, "about to execute async task!");
                        new CustomTask(matchmakerKey, localOtherUserId).execute();
                        _rootReference.child("users").child(localOtherUserId).removeEventListener(this);
                        roomReference.removeEventListener(_valueEventListener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                _rootReference.child("users").child(otherUserId).addListenerForSingleValueEvent(_valueEventListener1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomReference.addListenerForSingleValueEvent(_valueEventListener);

    }

    private class CustomTask extends AsyncTask<Void, Void, Void> {
        private String matchmakerKey, localOtherUserId;
        private ChildEventListener childEventListener;
        public CustomTask(String matchmakerKey, String localOtherUserId) {
            this.matchmakerKey = matchmakerKey;
            this.localOtherUserId = localOtherUserId;
            this.childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals(_user.getUid())) {
                        _otherPersonClicked = true;
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _iClicked = false;
            _otherPersonClicked = false;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "You clicked me!");
                    _iClicked = true;
                    _roomsReference.child(matchmakerKey).push().setValue(_user.getUid());
                }
            };
            _matchButton.setOnClickListener(onClickListener);
        }

        @Override
        protected Void doInBackground(Void... params) {
            _roomsReference.child(matchmakerKey).addChildEventListener(childEventListener);

            try {
                Thread.sleep(TIME_TO_REACT);
            } catch (Exception exception) {
                Log.e(TAG, exception.getMessage());
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAdded()) {
                _matchButton.setOnClickListener(null);
                _roomsReference.child(matchmakerKey).removeEventListener(childEventListener);
                // if both users clicked each other
                if (_iClicked && _otherPersonClicked) {
                    // add yourself to their contacts
                    _rootReference
                            .child("users")
                            .child(localOtherUserId)
                            .child("contacts")
                            .push()
                            .setValue(_user.getUid());
                    Toast.makeText(_activity, "Added to contacts", Toast.LENGTH_SHORT).show();
                }
                _roomsReference.child(matchmakerKey).setValue(null);
                resetView();
                findMatch();
            }
        }
    }

    private void resetView() {
        this._matchButton.setBackgroundColor(Color.parseColor("#F44336"));
        this._matchButton.setText(getResources().getString(R.string.waiting));
        this._profilePic.setImageResource(R.drawable.default_avatar);
        this._name.setText("");
    }


    private void waitForOtherUser(final String matchmakerKey) {
        final DatabaseReference roomReference = this._roomsReference.child(matchmakerKey);
        roomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals(_user.getUid())) {
                    startMatch(matchmakerKey);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                roomReference.removeEventListener(this);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                if (commit) {
                    waitForOtherUser(matchmaker);
                } else {
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
                    startMatch(matchmaker);
                } else {
                    findMatch();
                }
            }
        });
    }
}
