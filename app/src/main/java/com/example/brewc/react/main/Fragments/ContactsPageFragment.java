package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.brewc.react.R;
import com.example.brewc.react.main.Adapters.UserAdapter;
import com.example.brewc.react.main.FirebaseUser.SimplifiedUser;
import com.example.brewc.react.main.FirebaseUser.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.contract.Face;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * About page
 */

public class ContactsPageFragment extends Fragment {
    private ListView _listView;
    private List<SimplifiedUser> _contacts;
    private UserAdapter _userAdapter;

    private FirebaseAuth _auth;
    private FirebaseUser _user;
    private DatabaseReference _rootReference;
    private DatabaseReference _userContacts;
    private DatabaseReference _users;


    public ContactsPageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this._rootReference = FirebaseDatabase.getInstance().getReference();
        this._auth = FirebaseAuth.getInstance();
        this._user = this._auth.getCurrentUser();
        this._users = this._rootReference.child("users");
        this._userContacts =
                this._rootReference.child("users").child(this._user.getUid()).child("contacts");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.contacts_page_fragment, null);

        this._listView = (ListView) rootView.findViewById(R.id.listview_user);
        this._contacts = new ArrayList<>();
        this._userAdapter = new UserAdapter(getContext(), R.layout.list_item_user, this._contacts);
        this._listView.setAdapter(this._userAdapter);
        this._listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SimplifiedUser simplifiedUser = (SimplifiedUser) _listView.getItemAtPosition(position);
                String phoneNumber = simplifiedUser.getPhoneNumber();
                // copy password to clipboard
                ClipboardManager clipboard =
                        (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Phone Number", phoneNumber);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Copied phone number to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this._userContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ids = new ArrayList<String>();
                for (DataSnapshot userIDSnapshot : dataSnapshot.getChildren()) {
                    String id = userIDSnapshot.getValue(String.class);
                    ids.add(id);
                }
                getUsersFromIds(ids);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsersFromIds(List<String> ids) {
        DatabaseReference[] dbrs = new DatabaseReference[ids.size()];
        for (int i = 0; i < dbrs.length; i++) {
            dbrs[i] = this._users.child(ids.get(i));
            dbrs[i].addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                    SimplifiedUser simplifiedUser = objectToSimplifiedUser(user);
                    _contacts.add(simplifiedUser);
                    _userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private SimplifiedUser objectToSimplifiedUser(Map<String, Object> objectMap) {
        String name = (String) objectMap.get("displayName");
        String phoneNumber = (String) objectMap.get("phoneNumber");
        String profilePic = (String) objectMap.get("profilePicture");
        String bio = (String) objectMap.get("bio");

        SimplifiedUser user = new SimplifiedUser(
                name,
                phoneNumber,
                profilePic,
                bio
        );
        return user;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
