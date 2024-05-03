package com.example.mobilepayroll;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Edit_Profilepage extends AppCompatActivity {
    FirebaseAuth Auth;
    FirebaseFirestore db;

    String userID;
    ImageView admin_profile_image;
    DocumentSnapshot documentSnapshot; // Declare documentSnapshot here

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profilepage);
        EditText Profile_Name = findViewById(R.id.edit_profile_Name);
        EditText Change_AdminEmail = findViewById(R.id.edit_profile_Email);
        EditText Change_AdminPassword = findViewById(R.id.edit_profile_Password);
        EditText Change_ProfilePositon = findViewById(R.id.edit_profile_Job);
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        admin_profile_image = findViewById(R.id.profile_image);
        ImageButton add_profile_image = findViewById(R.id.set_profimage);
        Button SaveEditButton = findViewById(R.id.save_btn);
        ImageButton BackButton = findViewById(R.id.backIcon2);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileref  = storageReference.child("users/"+ Auth.getCurrentUser().getUid()+ "/profile.jpg");
        profileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(admin_profile_image);
            }
        });
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    documentSnapshot = snapshot;
                    Profile_Name.setText(snapshot.getString("fullname"));
                    Change_AdminEmail.setText(snapshot.getString("email"));
                    Change_ProfilePositon.setText(snapshot.getString("position"));
                }
            }
        });
        SaveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GetNewEmail =Change_AdminEmail.getText().toString();
                String GetNewPassword = Change_AdminPassword.getText().toString();
                String GetNewPosition = Change_ProfilePositon.getText().toString();

                boolean emailChanged = !GetNewEmail.equals(documentSnapshot.getString("email"));
                if (emailChanged) {
                    if (isValidEmail(GetNewEmail
                    )) {
                        FirebaseUser user = Auth.getCurrentUser();
                        if (user != null) {
                            user.verifyBeforeUpdateEmail(GetNewEmail)
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
                        documentReference.update("email", GetNewEmail)
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
                        Change_AdminEmail.setError("Invalid email format.");
                    }
                }

                if (!TextUtils.isEmpty(GetNewPassword) && isValidPassword(GetNewPassword)) {
                    FirebaseUser user = Auth.getCurrentUser();
                    if (user != null) {
                        user.updatePassword(GetNewPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Edit_Profilepage.this,
                                                "Password changed successfully!",
                                                Toast.LENGTH_SHORT).show();
                                        documentReference.update("password", GetNewPassword)
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

                if (!GetNewPosition.equals(documentSnapshot.getString("position"))) {
                    documentReference.update("position", GetNewPosition)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Position updated successfully!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Edit_Profilepage.this,
                                            "Failed to update : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                Intent GotoProfilePageFunction = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                startActivity(GotoProfilePageFunction);
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToProfilePageFunction = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                startActivity(BackToProfilePageFunction);
            }
        });

        add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent OpenGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGallery, 1000);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImageToFirebaseStorage(imageUri);
            }

        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference fileref =storageReference.child("users/"+ Auth.getCurrentUser().getUid()+ "/profile.jpg");
        fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(admin_profile_image);

                        saveImageUrlToFirestore(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Edit_Profilepage.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        DocumentReference userRef = db.collection("users").document(userID);
        userRef.update("profileImageUrl", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Edit_Profilepage.this, "Profile image saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Edit_Profilepage.this, "Failed to save image URL", Toast.LENGTH_SHORT).show();
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


