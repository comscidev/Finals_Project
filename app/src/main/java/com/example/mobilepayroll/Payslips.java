package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Payslips extends AppCompatActivity {
        private BottomNavigationView bottomNavigationView;
        FirebaseFirestore db;
        RecyclerView recyclerView;

        NewAdapter newAdapter;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payslips);
        db = FirebaseFirestore.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_payslip);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_profile:
                        startActivity(new Intent(getApplicationContext(), Profilepage_function.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_employees:
                        startActivity(new Intent(getApplicationContext(), EmployeeList.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_payroll:
                        startActivity(new Intent(getApplicationContext(), PayrollComputation.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bottom_payslip:
                        return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewId2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = db.collection("payroll");
        FirestoreRecyclerOptions<PayModel> options =
                new FirestoreRecyclerOptions.Builder<PayModel>()
                        .setQuery(query, PayModel.class)
                        .build();
        newAdapter = new NewAdapter(options);
        recyclerView.setAdapter(newAdapter);
        newAdapter.startListening();
    }
}