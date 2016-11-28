//package com.example.brewc.react.main.Activities;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.AppCompatButton;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.brewc.react.R;
//import com.example.brewc.react.main.FirebaseUser.User;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class RegisterPageActivity extends AppCompatActivity {
//
//    private final String USERS = "users";
//
//
//    private DatabaseReference _rootReference;
//
//    private AppCompatButton _registerButton;
//    private EditText _inputEmail, _inputPassword;
//
//    private FirebaseAuth _auth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        // remove title bar and make full screen
//        // source: http://stackoverflow.com/questions/30746109/how-to-remove-title-bar-from-activity-extending-actionbaractivity-or-appcompatac
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getSupportActionBar().hide();
//        this.setContentView(R.layout.login_page_activity);
//
//        _rootReference = FirebaseDatabase.getInstance().getReference();
//
//        _registerButton = (AppCompatButton) findViewById(R.id.btn_register);
//        _inputEmail = (EditText) findViewById(R.id.input_email);
//        _inputPassword = (EditText) findViewById(R.id.input_password);
//
//        _registerButton.setEnabled(false);
//
//        _inputEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                updateLoginButton();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        _inputPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                updateLoginButton();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }
//
//
//
//
//    private void tryCreateAccount() {
//        String email = _inputEmail.getText().toString();
//        String password = _inputPassword.getText().toString();
//
//        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(Task<AuthResult> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(LoginPageActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
//                } else {
//                    createAccount(task.getResult().getUser());
//                }
//            }
//        });
//    }
//
//
//    private void createAccount(FirebaseUser user) {
//        String email = user.getEmail();
//        String userID = user.getUid();
//
//        User newUser = new User(email, userID);
//        _rootReference.child(USERS).child(userID).setValue(newUser);
//    }
//}
