package com.example.brewc.react.main.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.brewc.react.R;
import com.example.brewc.react.main.Adapters.ImageAdapter;
import com.example.brewc.react.main.Essentials.Keys;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.FirebaseUser.UserFace;
import com.example.brewc.react.main.FirebaseUser.UserUUID;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.example.brewc.react.main.Utilities.Photo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import static android.app.Activity.RESULT_OK;

/**
 * Database page
 */

public class DatabasePageFragment extends Fragment {
    private DatabaseReference _rootReference;
    private DatabaseReference _databaseReference;
    private FirebaseAuth _auth;
    private GridView _gridView;
    private ImageAdapter _imageAdapter;
    private List<Photo> _photos;
    private Face _face;
    private ViewGroup _container;
    private Bitmap _pic;
    private View _rootView;
    private FaceServiceClient _faceServiceClient;
    private AppCompatButton _btn_pic;
    private FirebaseUser _user;
    private UserUUID _personId;
    private boolean _ready;

    public DatabasePageFragment() {
        // nothing
    }

    // This is called when the activity to which this fragment is attached is created.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this._faceServiceClient = new FaceServiceRestClient(Keys.FACE_KEY);
        this._face = null;
        this._pic = null;
        this._personId = null;
        this._ready = false;
        this._auth = FirebaseAuth.getInstance();
        this._user = this._auth.getCurrentUser();
        this._rootReference = FirebaseDatabase.getInstance().getReference();
        this._databaseReference =
                this._rootReference
                        .child("users")
                        .child(this._auth.getCurrentUser().getUid())
                        .child("database");
        this._rootReference
                .child("users")
                .child(this._auth.getCurrentUser().getUid())
                .child("personId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        _personId = dataSnapshot.getValue(UserUUID.class);
                        _ready = true;
                        Log.v("tag", "got user person id " + _personId.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        this._photos = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this._rootView = inflater.inflate(R.layout.database_page_fragment,  null);
        this._container = container;
        this._gridView = (GridView) this._rootView.findViewById(R.id.gridview);
        this._imageAdapter = new ImageAdapter(getContext(), R.layout.grid_item_image, this._photos);
        this._gridView.setAdapter(this._imageAdapter);
        this._btn_pic = (AppCompatButton) this._rootView.findViewById(R.id.photo_btn);

        this._btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letUserChoosePhoto();
            }
        });
        if (this._photos.size() == 0) {
            getPhotos();
        }
        return this._rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // getPhotos();
    }

    private void getPhotos() {
        this._photos.clear();
        this._imageAdapter.clear();
        this._imageAdapter.notifyDataSetChanged();
        this._databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int c = 0;
                    for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                        Photo photo = idSnapshot.getValue(Photo.class);
                        _photos.add(photo);
                        _imageAdapter.notifyDataSetChanged();
                        c++;
                    }
                    Log.v("count:", c+"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void letUserChoosePhoto() {
        // source: http://stackoverflow.com/questions/2708128/single-intent-to-let-user-take-picture-or-pick-image-from-gallery-in-android
        Intent picIntent = new Intent();
        picIntent.setType("image/*");
        picIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String picTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(picIntent, picTitle);
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                new Intent[] { takePhotoIntent }
        );
        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                _pic = bitmap;
                detectAndFrame(_pic);
            } catch (Exception exception) {
                Log.e("ha", "No Image\n" + exception.getMessage());
                exception.printStackTrace();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Face detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayInputStream inputStream1 =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = _faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    true,        // returnFaceLandmarks
                                    new FaceServiceClient.FaceAttributeType[] {
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender,
                                            FaceServiceClient.FaceAttributeType.Smile,
                                            FaceServiceClient.FaceAttributeType.FacialHair,
                                            FaceServiceClient.FaceAttributeType.HeadPose
                                    }
                            );
                            if (result == null) {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            } else if (result.length == 1) {
                                _face = result[0];
                                DatabaseReference key = _databaseReference.push();
                                _databaseReference.child(key.getKey()).setValue(
                                        new Photo(BitmapUtilities.bitmapToBase64(_pic), new UserFace(_face))
                                );
                                UUID uuid =
                                        new UUID(
                                                _personId.getMostSignificantBits(),
                                                _personId.getLeastSignificantBits()
                                        );
                                _faceServiceClient.addPersonFace(
                                        _user.getUid().toLowerCase(),
                                        uuid,
                                        params[1],
                                        "face",
                                        _face.faceRectangle);
                                _faceServiceClient.trainPersonGroup(_user.getUid().toLowerCase());
                            }
                            publishProgress(
                                    String.format(Locale.getDefault(),
                                            "Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            Log.e("ha", e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    }
                    @Override
                    protected void onPreExecute() {
                        // nothing to do
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        // do nothing
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        // set prof pic
                        if (result != null) {
                            _face = result.length == 1 ? result[0] : null;
                            if (result.length == 0) {
                                Toast.makeText(getActivity().getApplicationContext(), "No face detected", Toast.LENGTH_SHORT).show();
                            } else if (result.length > 1) {
                                Toast.makeText(getActivity().getApplicationContext(), "Multiple faces detected", Toast.LENGTH_SHORT).show();
                            } else if (result.length == 1) {
                                getPhotos();
                            }
                        }

                    }
                };
        detectTask.execute(inputStream, inputStream1);
        return _face;
    }
}