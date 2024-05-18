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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        initializeViews();
        setupSignUpButton();
        setupLoginLink();
    }

    private void initializeViews() {
        editTextFullName = findViewById(R.id.textName);
        editTextEmail = findViewById(R.id.textEmail);
        editTextPassword = findViewById(R.id.textPassword);
        editTextConfirmPassword = findViewById(R.id.textConfirmPassword);
    }

    private void setupSignUpButton() {
        Button buttonSignUp = findViewById(R.id.signup_btn);
        buttonSignUp.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification(email);
                        saveUserDetailsToFirestore(fullName, email);
                    } else {
                        Toast.makeText(Signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(String email) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Signup.this, "Verification email sent to " + email, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(Signup.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUserDetailsToFirestore(String fullName, String email) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference documentReference = firestore.collection("users").document(userId);

            Map<String, Object> user = new HashMap<>();
            user.put("fullName", fullName);
            user.put("email", email);

            documentReference.set(user)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Failed to save user details to Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupLoginLink() {
        TextView textViewLogin = findViewById(R.id.login_link);
        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(Signup.this, MainActivity.class));
            finish();
        });
    }
}

