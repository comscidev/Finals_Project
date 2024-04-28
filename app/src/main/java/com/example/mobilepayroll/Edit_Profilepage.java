package com.example.mobilepayroll;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Edit_Profilepage extends AppCompatActivity {
    FirebaseAuth Auth;
    FirebaseFirestore db;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profilepage);
        EditText name = findViewById(R.id.edit_profile_Name);
        EditText change_email = findViewById(R.id.edit_profile_Email);
        EditText phone = findViewById(R.id.edit_profile_Phone);
        EditText change_pass = findViewById(R.id.edit_profile_Password);
        TextView fname = findViewById(R.id.titleName2);
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        Button save = findViewById(R.id.save_btn);

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    fname.setText(documentSnapshot.getString("fullname"));
                    name.setText(documentSnapshot.getString("fullname"));
                    change_email.setText(documentSnapshot.getString("email"));
                    phone.setText(documentSnapshot.getString("phone"));
                    change_pass.setText(documentSnapshot.getString("password"));
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = name.getText().toString();
                String newEmail = change_email.getText().toString();
                String newPhone = phone.getText().toString();
                String newPassword = change_pass.getText().toString();

                if (TextUtils.isEmpty(newEmail)) {
                    change_email.setError("Email is required.");
                    return;
                }

                if (!isValidEmail(newEmail)) {
                    change_email.setError("Invalid email format.");
                    return;
                }

                if (TextUtils.isEmpty(newPhone)) {
                    phone.setError("Phone number is required.");
                    return;
                }

                if (!TextUtils.isDigitsOnly(newPhone)) {
                    phone.setError("Invalid phone number format. Please enter only numbers.");
                    return;
                }

                if (isValidPassword(newPassword)) {
                    FirebaseUser user = Auth.getCurrentUser();
                    if (user != null) {
                        if (!newEmail.equals(user.getEmail())) {
                            user.verifyBeforeUpdateEmail(newEmail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.sendEmailVerification()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(Edit_Profilepage.this,
                                                                    "A verification email has been sent to your new email address.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Edit_Profilepage.this,
                                                    "Failed to update email: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Password changed successfully!",
                                                Toast.LENGTH_SHORT).show();

                                        documentReference.update("password", newPassword)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Failed to change password: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("fullname", newName);
                    userData.put("email", newEmail);
                    userData.put("phone", newPhone);
                    userData.put("password", newPassword);

                    documentReference.update(userData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Profile updated successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Edit_Profilepage.this,
                                            Profilepage_function.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Failed to update profile: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Edit_Profilepage.this,
                            "Invalid password format. Password must be at least 6 characters long" +
                                    " and contain both letters and numbers.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
