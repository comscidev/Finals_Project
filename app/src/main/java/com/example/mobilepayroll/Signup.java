package com.example.mobilepayroll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    String userID;
    FirebaseAuth Auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        EditText Full_Name = findViewById(R.id.textName);
        EditText Email = findViewById(R.id.textEmail);
        EditText Password = findViewById(R.id.textPassword);
        EditText Confirm_Password = findViewById(R.id.textConfirmPassword);
        Button RegisterButton = findViewById(R.id.signup_btn);
        TextView LogInPage = findViewById(R.id.login_link);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GetEmail = Email.getText().toString();
                String GetPassword = Password.getText().toString();
                String GetConfirmPassword = Confirm_Password.getText().toString();

                if (TextUtils.isEmpty(GetEmail) || TextUtils.isEmpty(GetPassword) ||
                        TextUtils.isEmpty(GetConfirmPassword)) {
                    Toast.makeText(Signup.this, "Please fill in all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(GetPassword)) {
                    Toast.makeText(Signup.this,
                            "Must contain 6-8 characters\nMust contain at least one number",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!GetPassword.equals(GetConfirmPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Auth.createUserWithEmailAndPassword(GetEmail, GetPassword).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = Auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        firebaseUser.sendEmailVerification().
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Signup.this, "Created Successfully," +
                                                                    "Verify your email address", Toast.LENGTH_SHORT).show();
                                                            Auth.signOut();
                                                            finish();
                                                            Intent GoToLoginPage = new Intent(Signup.this, MainActivity.class);
                                                            startActivity(GoToLoginPage);
                                                        } else {
                                                            Toast.makeText(Signup.this, "Error Sending Verification Email",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        userID = Auth.getCurrentUser().getUid();
                                        DocumentReference documentReference = db.collection("users").
                                                document(userID);
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("fullname", Full_Name.getText().toString());
                                        userData.put("email", GetEmail);
                                        userData.put("password", GetPassword);
                                        documentReference.set(userData).addOnSuccessListener
                                                (new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(Signup.this, "Error Occurred",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Signup.this, "Error Occurred",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            public boolean isValidPassword(String password) {
                return password.length() >= 6 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
            }
        });

        LogInPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoToLogInPage = new Intent(Signup.this, MainActivity.class);
                startActivity(GoToLogInPage);
            }
        });
    }
}
