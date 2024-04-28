package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    DocumentSnapshot documentSnapshot; // Declare documentSnapshot here

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
        ImageButton back = findViewById(R.id.backIcon2);

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    documentSnapshot = snapshot;
                    fname.setText(snapshot.getString("fullname"));
                    name.setText(snapshot.getString("fullname"));
                    change_email.setText(snapshot.getString("email"));
                    phone.setText(snapshot.getString("phone"));
                    change_pass.setText(snapshot.getString("password"));
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

                boolean emailChanged = !newEmail.equals(documentSnapshot.getString("email"));
                boolean phoneChanged = !newPhone.equals(documentSnapshot.getString("phone"));

                if (emailChanged) {
                    if (isValidEmail(newEmail)) {
                        FirebaseUser user = Auth.getCurrentUser();
                        if (user != null) {
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
                        documentReference.update("email", newEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Email updated successfully!",
                                                Toast.LENGTH_SHORT).show();
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
                    } else {
                        change_email.setError("Invalid email format.");
                        return;
                    }
                }

                if (phoneChanged) {
                    if (TextUtils.isDigitsOnly(newPhone)) {
                        documentReference.update("phone", newPhone)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Phone number updated successfully!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Failed to update phone number: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        phone.setError("Invalid phone number format. Please enter only numbers.");
                        return;
                    }
                }

                if (!TextUtils.isEmpty(newPassword) && isValidPassword(newPassword)) {
                    FirebaseUser user = Auth.getCurrentUser();
                    if (user != null) {
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
                                                        // Password updated in Firestore
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
                }

                if (!newName.equals(documentSnapshot.getString("fullname"))) {
                    documentReference.update("fullname", newName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Name updated successfully!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Failed to update name: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                Intent intent = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                startActivity(intent);
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
