package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
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
import com.example.brewc.react.main.Essentials.Keys;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.FirebaseUser.UserFace;
import com.example.brewc.react.main.FirebaseUser.UserUUID;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.example.brewc.react.main.Utilities.ColorUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * React page
 */

public class ReactPageFragment extends Fragment {
    private final long TIME_TO_REACT = 5000L;
    public final String TAG = this.getClass().getSimpleName();
    private final String NONE = "none";
    private boolean _iClicked, _otherPersonClicked, _inFirst, _inSecond;
    FirebaseAuth _auth;
    FirebaseUser _user;
    DatabaseReference _rootReference;
    DatabaseReference _matchmaker;
    DatabaseReference _roomsReference;
    String _faceId;
    Activity _activity;
    ValueEventListener _valueEventListener, _valueEventListener1;
    private FaceServiceClient _faceServiceClient;
    private DatabaseReference _roomReference;

    AppCompatButton _matchButton;
    TextView _name, _score;
    CircleImageView _profilePic;


    public ReactPageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._faceServiceClient = new FaceServiceRestClient(Keys.FACE_KEY);
        this._iClicked = false;
        this._inFirst = false;
        this._roomReference = null;
        this._inSecond = false;
        this._otherPersonClicked = false;
        this._faceId = null;
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
        this._rootReference
                .child("users")
                .child(this._user.getUid())
                .child("details")
                .child("faceId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserUUID userUUID = dataSnapshot.getValue(UserUUID.class);
                        UUID uuid = new UUID(userUUID.getMostSignificantBits(), userUUID.getLeastSignificantBits());
                        _faceId = uuid.toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.react_page_fragment, null);
        this._matchButton = (AppCompatButton) rootView.findViewById(R.id.match_button);
        this._name = (TextView) rootView.findViewById(R.id.user_name);
        this._profilePic = (CircleImageView) rootView.findViewById(R.id.user_profile_photo);
        this._score = (TextView) rootView.findViewById(R.id.score);
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

    @Override
    public void onPause() {
        super.onPause();
        if (this._roomReference != null) {
            _roomReference.setValue(null);
            _rootReference.child("matchmaker").setValue(NONE);
        }
    }

    private void startMatch(final String matchmakerKey) {
        final DatabaseReference roomReference = this._roomsReference.child(matchmakerKey);
        this._roomReference = roomReference;
        _valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, String> users = (HashMap<String, String> ) dataSnapshot.getValue();
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
                            return;
                        }
                        long ls = ((HashMap<String, Long>)user.get("personId")).get("leastSignificantBits");
                        long ms = ((HashMap<String, Long>)user.get("personId")).get("mostSignificantBits");
                        UUID uuid = new UUID(ms, ls);
                        new FaceTask(_faceId, uuid.toString(), localOtherUserId.toLowerCase()).execute();
                        _matchButton.setBackgroundColor(Color.parseColor("#00E676"));
                        if (getActivity() != null && isAdded()) {
                            _matchButton.setText(getResources().getString(R.string.match));
                        }
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

    private class FaceTask extends AsyncTask<Void, Void, Void> {
        private String faceId, personId, personGroupId;
        private String hex, text;
        public FaceTask(String faceId, String personId, String personGroupId) {
            this.faceId = faceId;
            this.personId = personId;
            this.personGroupId = personGroupId;
            this.hex = null;
            this.text = null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://api.projectoxford.ai/face/v1.0/verify");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key", Keys.FACE_KEY);
                urlConnection.connect();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("faceId", this.faceId);
                jsonObject.put("personId", this.personId);
                jsonObject.put("personGroupId", this.personGroupId);
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonObject.toString());
                out.close();

                int httpResult = urlConnection.getResponseCode();
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    Log.v(TAG, sb.toString());
                    JSONObject json = new JSONObject(sb.toString());
                    int score = (int) Math.round(json.getDouble("confidence") * 100);
                    Log.v(TAG, score+"");
                    String hexColor =
                            String.format("#%6s", Integer.toHexString(ColorUtilities.colors[score/5]))
                                    .replace(' ', '0');
                    this.hex = hexColor;
                    this.text = String.format(Locale.getDefault(), "%d", score);

                } else {
                    Log.v(TAG, urlConnection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (this.hex == null) {
                _score.setTextColor(Color.MAGENTA);
            } else {
                _score.setTextColor(Color.parseColor(hex));
            }
            _score.setText(this.text == null ? "NA" : this.text);
        }
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
        this._score.setText("");
    }

    private void waitForOtherUser(final String matchmakerKey) {
        this._inFirst = true;
        final DatabaseReference roomReference = this._roomsReference.child(matchmakerKey);
        roomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals(_user.getUid())) {
                    Log.v("id", id + " " + _user.getUid());
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
        this._inFirst = false;
        this._inSecond = false;
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
                    _inSecond = true;
                    startMatch(matchmaker);
                } else {
                    findMatch();
                }
            }
        });
    }
}
