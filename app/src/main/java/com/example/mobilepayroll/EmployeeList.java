package com.example.mobilepayroll;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.TextureView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EmployeeList extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        TextView display_name = findViewById(R.id.current_user);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        String userID = Auth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    display_name.setText(documentSnapshot.getString("fullname"));

                    bottomNavigationView = findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.setSelectedItemId(R.id.bottom_employees);

                    bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.bottom_profile:
                                    startActivity(new Intent(getApplicationContext(), Profilepage_function.class));
                                    overridePendingTransition(0, 0);
                                    return true;
                                case R.id.bottom_employees:
                                    return true;
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }
}