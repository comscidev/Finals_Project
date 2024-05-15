package com.example.mobilepayroll;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpLink;
    private TextView forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        initializeUI();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordResetDialog();
            }
        });
    }

    private void initializeUI() {
        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.login_btn);
        signUpLink = findViewById(R.id.signup_link);
        forgotPasswordLink = findViewById(R.id.ForgotPass);
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    startActivity(new Intent(MainActivity.this, EmployeeList.class));
                } else {
                    Toast.makeText(MainActivity.this, "Verify email first", Toast.LENGTH_SHORT).show();
                }
            } else {
                handleSignInFailure(task);
            }
        });
    }

    private void handleSignInFailure(@NonNull Task<AuthResult> task) {
        Exception exception = task.getException();
        if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(MainActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
        } else {
            assert exception != null;
            Toast.makeText(MainActivity.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPasswordResetDialog() {
        EditText resetEmailEditText = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter email to reset password");
        passwordResetDialog.setView(resetEmailEditText);

        passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
            String email = resetEmailEditText.getText().toString();
            sendPasswordResetEmail(email);
        });

        passwordResetDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        passwordResetDialog.create().show();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Reset link has been sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to send reset link", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, EmployeeList.class));
        }
    }
}
