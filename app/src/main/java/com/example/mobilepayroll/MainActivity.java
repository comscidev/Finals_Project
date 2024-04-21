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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        EditText usernameEditText = findViewById(R.id.inputEmail);
        EditText passwordEditText = findViewById(R.id.inputPassword);
        Button loginButton = findViewById(R.id.login_btn);
        TextView signup = findViewById(R.id.signup_link);
        TextView frgt_pass = findViewById(R.id.ForgotPass);

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty((password))) {
                Toast.makeText(MainActivity.this, "Please fill in all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Auth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(MainActivity.this,
                                        emlist_function.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Verify email first",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(MainActivity.this,
                                        "Email address not found", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this,
                                        "Wrong email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed: " +
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        frgt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder password_reset = new AlertDialog.Builder(v.getContext());
                password_reset.setTitle("Reset Password");
                password_reset.setTitle("Enter email to reset password");
                password_reset.setView(resetMail);
                password_reset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = resetMail.getText().toString();
                        Auth.sendPasswordResetEmail(email).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this,
                                                "Reset link sent to the email",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,
                                        "Reset link not set: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                password_reset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                password_reset.create().show();
            }
        });
    }
}
