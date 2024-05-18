package com.example.mobilepayroll;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpPage, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupFirebaseAuth();
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.login_btn);
        signUpPage = findViewById(R.id.signup_link);
        forgotPassword = findViewById(R.id.ForgotPass);
    }

    private void setupFirebaseAuth() {
        auth = FirebaseAuth.getInstance();
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        signUpPage.setOnClickListener(v -> navigateToSignUp());

        forgotPassword.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void attemptLogin() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkEmailVerification();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private void checkEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            navigateToEmployeeList();
        } else {
            Toast.makeText(MainActivity.this, "Verify email first", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginFailure(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(MainActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(MainActivity.this, Signup.class);
        startActivity(intent);
    }

    private void showResetPasswordDialog() {
        EditText resetPasswordEditText = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);

        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter email to reset password");
        passwordResetDialog.setView(resetPasswordEditText);

        passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
            String email = resetPasswordEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                sendPasswordResetEmail(email);
            } else {
                Toast.makeText(MainActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
            }
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

    private void navigateToEmployeeList() {
        Intent intent = new Intent(MainActivity.this, EmployeeList.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfUserLoggedIn();
    }

    private void checkIfUserLoggedIn() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                auth.signOut();
            } else {
                navigateToEmployeeList();
            }
        }
    }
}
