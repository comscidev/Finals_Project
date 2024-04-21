package com.example.mobilepayroll;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class Profilepage_function extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profilepage_function);
        TextView fname = findViewById(R.id.textFullName);
        TextView username = findViewById(R.id.textusername);
        TextView email = findViewById(R.id.showemail);
        TextView change_pass = findViewById(R.id.showpass);
        ImageView back = findViewById(R.id.backIcon);
        ImageButton change_email = findViewById(R.id.profile_editIcon);
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = Auth.getCurrentUser().getUid();


        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    fname.setText(documentSnapshot.getString("email"));
                    username.setText(documentSnapshot.getString("email"));
                    email.setText(documentSnapshot.getString("email"));
                }
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder password_reset = new AlertDialog.Builder(v.getContext());
                password_reset.setTitle("Reset Password");
                password_reset.setMessage("Enter new password " +
                        "(at least 6 characters with letters and numbers):");

                EditText newPasswordInput = new EditText(v.getContext());
                newPasswordInput.setHint("New Password");
                password_reset.setView(newPasswordInput);

                password_reset.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = newPasswordInput.getText().toString();
                        if (isValidPassword(newPassword)) {
                            Auth.getCurrentUser().updatePassword(newPassword)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Profilepage_function.this,
                                                    "Password changed successfully!",
                                                    Toast.LENGTH_SHORT).show();

                                            documentReference.update("password", newPassword)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(Profilepage_function.this,
                                                                    "New password stored in Firestore!",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Profilepage_function.this,
                                                                    "Failed to store new password in Firestore",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Profilepage_function.this,
                                                    "Failed to change password: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Profilepage_function.this,
                                    "Invalid password format. Password must be at least 6" +
                                            " characters long and contain both letters and numbers.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                password_reset.setNegativeButton("Cancel", null);
                password_reset.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profilepage_function.this,
                        emlist_function.class);
                startActivity(intent);
            }
        });
        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder email_reset = new AlertDialog.Builder(Profilepage_function.this);
                email_reset.setTitle("Change Email");
                email_reset.setMessage("Enter your new email address:");

                EditText newEmailInput = new EditText(Profilepage_function.this);
                newEmailInput.setHint("New Email");
                email_reset.setView(newEmailInput);

                email_reset.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmail = newEmailInput.getText().toString();
                        if (!TextUtils.isEmpty(newEmail)) {
                            FirebaseUser user = Auth.getCurrentUser();
                            if (user != null) {
                                user.verifyBeforeUpdateEmail(newEmail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                documentReference.update("email", newEmail)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                email.setText(newEmail); // Update UI
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Profilepage_function.this,
                                                        "Failed to update email: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Profilepage_function.this,
                                    "Please enter a new email address",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                email_reset.setNegativeButton("Cancel", null);
                email_reset.show();
            }
        });


    }
    public boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches
                ("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }
}
