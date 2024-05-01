package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
        TextView display_position = findViewById(R.id.profile_position);
        TextView display_email = findViewById(R.id.profile_Email);
        Button edit_profile = findViewById(R.id.EditProfile);
        Button sign_out = findViewById(R.id.logout_btn);
        String userID = Auth.getCurrentUser().getUid();
        ImageButton back = findViewById(R.id.backIcon);


        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    display_position.setText(documentSnapshot.getString("position"));
                    display_email.setText(documentSnapshot.getString("email"));

                }
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GotoEditProfilePage = new Intent(Profilepage_function.this, Edit_Profilepage.class);
                startActivity(GotoEditProfilePage);
            }
        });
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent GotoMainActivity = new Intent(Profilepage_function.this, MainActivity.class);
                GotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(GotoMainActivity);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GotoEmployeeList = new Intent(Profilepage_function.this, EmployeeList.class);
                startActivity(GotoEmployeeList);
            }
        });
    }

}