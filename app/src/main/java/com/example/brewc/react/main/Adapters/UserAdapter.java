package com.example.brewc.react.main.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brewc.react.R;
import com.example.brewc.react.main.FirebaseUser.SimplifiedUser;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.Utilities.BitmapUtilities;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;

/**
 * Custom User Adapter for contacts
 */

public class UserAdapter extends ArrayAdapter<SimplifiedUser> {
    private List<SimplifiedUser> _users;

    public UserAdapter(Context context, int textViewResourceId, List<SimplifiedUser> users) {
        super(context, textViewResourceId, users);
        this._users = users;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_user, null);
        }

        TextView userName = (TextView) view.findViewById(R.id.name);
        TextView userPhoneNumber = (TextView) view.findViewById(R.id.number);
        ImageView userProfilePic = (ImageView) view.findViewById(R.id.profile_pic);

        SimplifiedUser user = this._users.get(position);
        String phoneNumber = PhoneNumberUtils.formatNumber(
                user.getPhoneNumber(),
                Locale.getDefault().getCountry()
        );
        String name = user.getName();
        Bitmap profilePic = BitmapUtilities.base64ToBitmap(user.getProfilePicture());

        userName.setText(name);
        userPhoneNumber.setText(phoneNumber);
        userProfilePic.setImageBitmap(profilePic);

        Log.v("tag", phoneNumber);
        return view;
    }
}