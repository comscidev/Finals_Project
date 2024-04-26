package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profilepage_function extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth  Auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_profilepage_function);
        TextView display_name = findViewById(R.id.titleName);
        TextView display_name1 = findViewById(R.id.profile_Name);
        TextView display_email = findViewById(R.id.profile_Email);
        TextView display_phone = findViewById(R.id.profile_Phone);
        TextView display_pass = findViewById(R.id.profile_Password);
        Button edit_profile = findViewById(R.id.EditProfile);
        Button signout = findViewById(R.id.logout_btn);
        String userID = Auth.getCurrentUser().getUid();
        ImageButton back = findViewById(R.id.backIcon);


        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    display_name.setText(documentSnapshot.getString("fullname"));
                    display_name1.setText(documentSnapshot.getString("fullname"));
                    display_email.setText(documentSnapshot.getString("email"));
                    display_phone.setText(documentSnapshot.getString("phone"));
                    display_pass.setText(documentSnapshot.getString("password"));

                }
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profilepage_function.this, Edit_Profilepage.class);
                startActivity(intent);
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profilepage_function.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profilepage_function.this, EmployeeList.class);
                startActivity(intent);
            }
        });
    }

}