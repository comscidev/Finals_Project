package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class EmployeeList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textAdmin, textAll, textProduction, textSupport, textLogistics, displayFullName;
    private UserAdapter userAdapter;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        initializeViews();
        setupFirestore();
        setupAuth();
        setupSearchView();
        setupDepartmentFilters();
        setupBottomNavigation();
        fetchCurrentUser();
    }

    private void initializeViews() {
        displayFullName = findViewById(R.id.current_user);
        textAdmin = findViewById(R.id.textAdmin);
        textAll = findViewById(R.id.textAll);
        textProduction = findViewById(R.id.textProduction);
        textSupport = findViewById(R.id.textSupport);
        textLogistics = findViewById(R.id.textLogistics);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ImageButton addEmployeeButton = findViewById(R.id.add_employee_btn);
        addEmployeeButton.setOnClickListener(v -> navigateToAddEmployee());
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupAuth() {
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchbar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchEmployees("ALL");
                } else {
                    searchEmployees(newText);
                }
                return false;
            }
        });
    }

    private void setupDepartmentFilters() {
        textAdmin.setOnClickListener(v -> fetchEmployeesByDepartment("Admin"));
        textAll.setOnClickListener(v -> fetchEmployeesByDepartment("ALL"));
        textProduction.setOnClickListener(v -> fetchEmployeesByDepartment("Production"));
        textSupport.setOnClickListener(v -> fetchEmployeesByDepartment("Support"));
        textLogistics.setOnClickListener(v -> fetchEmployeesByDepartment("Logistics"));
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.bottom_employees);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), Profilepage_function.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.bottom_employees:
                    return true;
                case R.id.bottom_payroll:
                    startActivity(new Intent(getApplicationContext(), PayrollComputation.class));
                    overridePendingTransition(0, 0);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void fetchCurrentUser() {
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    displayFullName.setText(documentSnapshot.getString("fullName"));
                }
            }
        });
    }

    private void navigateToAddEmployee() {
        Intent goToAddEmployee = new Intent(EmployeeList.this, AddEmployeeActivity.class);
        startActivity(goToAddEmployee);
    }

    private void fetchEmployees(String searchText) {
        Query query = db.collection("employees");
        if (!searchText.equals("ALL")) {
            query = query.orderBy("fullName")
                    .whereGreaterThanOrEqualTo("fullName", searchText)
                    .whereLessThanOrEqualTo("fullName", searchText + "\uf8ff");
        }
        setupRecyclerView(query);
    }

    private void searchEmployees(String searchText) {
        Query query = db.collection("employees")
                .orderBy("fullName")
                .whereGreaterThanOrEqualTo("fullName", searchText)
                .whereLessThanOrEqualTo("fullName", searchText + "\uf8ff");
        setupRecyclerView(query);
    }

    private void fetchEmployeesByDepartment(String department) {
        Query query = db.collection("employees");
        if (!department.equals("ALL")) {
            query = query.whereEqualTo("department", department);
        }
        setupRecyclerView(query);
    }

    private void setupRecyclerView(Query query) {
        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<UserModel> options =
                new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();
        userAdapter = new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }

}
