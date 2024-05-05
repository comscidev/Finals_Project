package com.example.mobilepayroll;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
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
        setContentView(R.layout.activity_profilepage_function);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView displayName = findViewById(R.id.adminName);
        TextView displayPosition = findViewById(R.id.profile_position);
        TextView displayEmail = findViewById(R.id.profile_Email);
        Button editProfile = findViewById(R.id.EditProfile);
        Button sign_out = findViewById(R.id.logout_btn);
        ImageView adminProfileImage = findViewById(R.id.admin_profpic); // Check this ID
        ImageButton BackToEmployeeList = findViewById(R.id.backIcon);

        String userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    displayName.setText(documentSnapshot.getString("fullname"));
                    displayPosition.setText(documentSnapshot.getString("position"));
                    displayEmail.setText(documentSnapshot.getString("email"));

                    String imageUrl = documentSnapshot.getString("profileImageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(Uri.parse(imageUrl))
                                .networkPolicy(NetworkPolicy.OFFLINE) // Load from cache first
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) // Disable caching
                                .into(adminProfileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(Uri.parse(imageUrl)).into(adminProfileImage);
                                    }
                                });
                    } else {
                        adminProfileImage.setImageResource(R.drawable.default_image);
                    }
                }
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoEditProfilePage = new Intent(Profilepage_function.this, Edit_Profilepage.class);
                startActivity(gotoEditProfilePage);
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent goToMainActivity = new Intent(Profilepage_function.this, MainActivity.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToMainActivity);
                    finish();
                } else {
                    Toast.makeText(Profilepage_function.this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BackToEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoEmployeeList = new Intent(Profilepage_function.this, EmployeeList.class);
                startActivity(gotoEmployeeList);
            }
        });
    }
}
