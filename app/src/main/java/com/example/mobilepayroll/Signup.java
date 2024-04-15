package com.example.mobilepayroll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class Signup extends AppCompatActivity {

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText user_name = findViewById(R.id.textUsername);
        EditText user_pass = findViewById(R.id.textPassword);
        EditText confirm_Password = findViewById(R.id.textConfirmPassword);
        Button registerButton = findViewById(R.id.signup_btn);
        TextView login = findViewById(R.id.login_link);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user_name.getText().toString().trim();
                String password = user_pass.getText().toString().trim();
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


                Auth.createUserWithEmailAndPassword(username, encryptPassword(password)).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Created Successfully",
                                Toast.LENGTH_SHORT).show();
                        userID = Auth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").
                                document(userID);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", username);
                        userData.put("password", encryptPassword(password));
                        documentReference.set(userData).addOnSuccessListener
                                (new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "OnSuccess: Created Successfully"+ userID);
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

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

            public String encryptPassword(String password) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : hash) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }
                    return hexString.toString();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
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
