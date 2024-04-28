
package com.example.mobilepayroll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
        EditText user_name = findViewById(R.id.textUsername);
        EditText user_pass = findViewById(R.id.textPassword);
        EditText confirm_Password = findViewById(R.id.textConfirmPassword);
        Button registerButton = findViewById(R.id.signup_btn);
        TextView login = findViewById(R.id.login_link);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user_name.getText().toString();
                String password = user_pass.getText().toString();
                String confirmPassword = confirm_Password.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Signup.this, "Please fill in all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(Signup.this,
                            "Must contain 6-8 characters\nMust contain at least one number",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                Auth.createUserWithEmailAndPassword(username, password).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification().
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task){
                                        Toast.makeText(Signup.this, "Created Successfully,"+
                                                "Verify your email address", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        userID = Auth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").
                                document(userID);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", username);
                        userData.put("password", password);
                        documentReference.set(userData).addOnSuccessListener
                                (new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                        Intent intent = new Intent(Signup.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Signup.this, "Error Occurred" +
                                        task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                        });

            }
            public boolean isValidPassword(String password) {
                return password.length() >= 6 && password.matches
                        ("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
