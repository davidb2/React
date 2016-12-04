package com.example.brewc.react.main.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.camera2.TotalCaptureResult;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brewc.react.R;
import com.example.brewc.react.main.Essentials.Keys;
import com.example.brewc.react.main.FirebaseUser.User;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.example.brewc.react.main.Utilities.KeyboardUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.microsoft.projectoxford.face.rest.WebServiceRequest;

public class RegisterPageActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private final String USERS = "users";
    private final int NUM_LEN = 10;


    private DatabaseReference _rootReference;

    private Bitmap _profilePic;
    private ScrollView _rootScrollView;
    private AppCompatButton _registerButton, _inputProfilePic;
    private EditText _inputEmail, _inputPassword, _inputConfirmPassword, _inputName, _inputPhoneNumber;
    private TextView _linkLogin;

    private FirebaseAuth _auth;
    private FirebaseAuth.AuthStateListener _authListener;


    private Face _face;
    private FaceServiceClient _faceServiceClient;

    private TextWatcher _defaultTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // remove title bar and make full screen
        // source: http://stackoverflow.com/questions/30746109/how-to-remove-title-bar-from-activity-extending-actionbaractivity-or-appcompatac
        this.getSupportActionBar().setTitle("Register");
        this.setContentView(R.layout.registration_page_activity);

        _faceServiceClient = new FaceServiceRestClient(Keys.FACE_KEY);
        _face = null;
        _rootReference = FirebaseDatabase.getInstance().getReference();
        _auth = FirebaseAuth.getInstance();

        _profilePic           = null;
        _inputProfilePic      = (AppCompatButton) findViewById(R.id.input_profile_pic);
        _inputName            = (EditText) findViewById(R.id.input_name);
        _inputPhoneNumber     = (EditText) findViewById(R.id.input_phone_number);
        _inputEmail           = (EditText) findViewById(R.id.input_email);
        _inputPassword        = (EditText) findViewById(R.id.input_password);
        _inputConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        _registerButton       = (AppCompatButton) findViewById(R.id.btn_signup);
        _linkLogin         = (TextView) findViewById(R.id.link_login);
        _rootScrollView       = (ScrollView) findViewById(R.id.root_scroll_view);

        // _keyboardUtil = new KeyboardUtil(RegisterPageActivity.this, _rootScrollView.getChildAt(0));

        _defaultTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateRegisterButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        _authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        _registerButton.setEnabled(false);
        addTextChangedListeners();
        _auth.addAuthStateListener(_authListener);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (_authListener != null) {
            _auth.removeAuthStateListener(_authListener);
        }
    }

    private void addTextChangedListeners() {
        _inputName.addTextChangedListener(_defaultTextWatcher);
        _inputEmail.addTextChangedListener(_defaultTextWatcher);
        _inputPassword.addTextChangedListener(_defaultTextWatcher);
        _inputConfirmPassword.addTextChangedListener(_defaultTextWatcher);
        _inputPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                updateRegisterButton();
            }
        });
        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryCreateAccount();
            }
        });
        _linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginPage = new Intent(RegisterPageActivity.this, LoginPageActivity.class);
                startActivity(loginPage);
            }
        });
        _inputProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letUserChoosePhoto();
            }
        });
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().length() == 0;
    }
    private void updateRegisterButton() {
        EditText[] editTexts = {
                _inputName, _inputEmail, _inputPhoneNumber, _inputPassword, _inputConfirmPassword
        };
        for (EditText editText : editTexts) {
            if (isEmpty(editText)) {
                _registerButton.setEnabled(false);
                return;
            }
        }

        // check phone number
        String numberOnlyPhoneNumber =
                this
                        ._inputPhoneNumber
                        .getText()
                        .toString()
                        .replaceAll("[^0-9]", "");
        if (numberOnlyPhoneNumber.length() != NUM_LEN) {
            this._inputPhoneNumber.setError("Invalid phone number");
            _registerButton.setEnabled(false);
            return;
        }

        // passwords don't match
        if (!this._inputPassword.getText().toString().equals(this._inputConfirmPassword.getText().toString())) {
            this._inputConfirmPassword.setError("Does not match password");
            _registerButton.setEnabled(false);
            return;
        }

        _registerButton.setEnabled(true);
    }

    private void tryCreateAccount() {
        if (this._face == null) {
            Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
            this._inputProfilePic.setError("No image uploaded");
            return;
        }
        String email = _inputEmail.getText().toString();
        String password = _inputPassword.getText().toString();
        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegisterPageActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    onCreateAccountFailure();
                } else {
                    onCreateAccountSuccess();
                    createAccount();
                }
            }
        });
    }

    private void onCreateAccountFailure() {
        // do nothing
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                _profilePic = bitmap;
                detectAndFrame(_profilePic);
            } catch (Exception exception) {
                Log.e(TAG, "No Image");
            }
        }
    }

    private void onCreateAccountSuccess() {
        // go to login page
        Intent loginPage = new Intent(RegisterPageActivity.this, LoginPageActivity.class);
        startActivity(loginPage);
    }

    private void createAccount() {
        // something was wrong with the picture
        if (this._face == null) {
            // Toast.makeText(getApplicationContext(), "invalid face", Toast.LENGTH_SHORT).show();
            return;
        }

        // add user to firebase database under special key
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String userID = user.getUid();
        String name = _inputName.getText().toString();
        String phoneNumber = _inputPhoneNumber.getText().toString();
        Face details = this._face;
        User newUser = new User(email, userID, name, details, phoneNumber, BitmapUtilities.bitmapToBase64(this._profilePic));
        newUser.addContact();
        _rootReference.child(USERS).child(userID).setValue(newUser);
    }

    /**
     *    Detect faces by uploading face images
     *    Frame faces after detection
     */
    private Face detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
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
                            }
                            publishProgress(
                                    String.format(Locale.getDefault(),
                                            "Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            Log.e(TAG, e.getMessage());
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
                            Log.v(TAG, result[0].toString());
                            _face = result.length == 1 ? result[0] : null;
                            if (result.length == 0) {
                                Toast.makeText(getApplicationContext(), "No face detected", Toast.LENGTH_SHORT).show();
                            } else if (result.length > 1) {
                                Toast.makeText(getApplicationContext(), "Multiple faces detected", Toast.LENGTH_SHORT).show();
                            } else if (result.length == 1) {
                                _inputProfilePic.setError(null);
                            }
                        }
                        updateRegisterButton();
                    }
                };
        detectTask.execute(inputStream);
        return _face;
    }
}
