package com.example.mobilepayroll;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

public class EmployeeList extends AppCompatActivity {

    RecyclerView recyclerView;

    TextView textAdmin, textAll, textProduction, textSupport, textLogistics;
    UserAdapter userAdapter ;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        TextView Display_FullName = findViewById(R.id.current_user);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        String userID = Auth.getCurrentUser().getUid();
        ImageButton add_employee = findViewById(R.id.add_employee_btn);
        recyclerView("ALL");
        SearchView searchView = findViewById(R.id.searchbar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().equals("")) {
                    recyclerView("ALL");
                } else {
                    recyclerView("" + searchView.getQuery());
                }
                return false;
            }
        });

        textAdmin = findViewById(R.id.textAdmin);
        textAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewByDepartment("Admin");
            }
        });

        textAll = findViewById(R.id.textAll);
        textAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewByDepartment("ALL");
            }
        });

        textProduction = findViewById(R.id.textProduction);
        textProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewByDepartment("Production");
            }
        });

        textSupport = findViewById(R.id.textSupport);
        textSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewByDepartment("Support");
            }
        });

        textLogistics = findViewById(R.id.textLogistics);
        textLogistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewByDepartment("Logistics");
            }
        });

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Display_FullName.setText(documentSnapshot.getString("fullname"));

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
                                case R.id.bottom_payroll:
                                    startActivity(new Intent(getApplicationContext(), PayrollComputation.class));
                                    overridePendingTransition(0,0);
                                    return true;
                            }
                            return false;
                        }
                    });
                }
            }
        });
        add_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoToAddEmployeeList = new Intent(EmployeeList.this, AddEmployeeActivity.class);
                startActivity(GoToAddEmployeeList);
            }
        });
    }
    private void recyclerView(String searchText) {
        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query;
        if (searchText.equals("ALL")) {
            query = db.collection("employees");
        } else {
            query = db.collection("employees")
                    .orderBy("fullName")
                    .whereGreaterThanOrEqualTo("fullName", searchText)
                    .whereLessThanOrEqualTo("fullName", searchText + "\uf8ff");
        }

        FirestoreRecyclerOptions<UserModel> options =
                new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();
        userAdapter = new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }

    private void recyclerViewByDepartment(String searchText) {
        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query;
        if (searchText.equals("ALL")) {
            query = db.collection("employees");
        } else {
            query = db.collection("employees").whereEqualTo("department", searchText);
        }

        FirestoreRecyclerOptions<UserModel> options =
                new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();
        userAdapter = new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }
}


