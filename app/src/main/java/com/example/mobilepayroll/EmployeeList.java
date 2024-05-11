
package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private FirebaseFirestore db;
    private UserAdapter userAdapter;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        TextView displayFullName = findViewById(R.id.current_user);
        searchEditText = findViewById(R.id.search);
        ImageButton addEmployeeButton = findViewById(R.id.add_employee_btn);
        recyclerView = findViewById(R.id.recyclerViewId);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();

        setUpBottomNavigation();
        setUpSearchListener();
        setUpRecyclerView();

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    displayFullName.setText(documentSnapshot.getString("fullname"));
                }
            }
        });

        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeList.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpBottomNavigation() {
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
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void setUpSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = db.collection("employees");
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();
        userAdapter = new UserAdapter(options, this); // Updated initialization with 'this'
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }

    private void search(String string) {
        Query baseQuery = db.collection("employees").orderBy("fullName");
        if (!string.isEmpty()) {
            Query filteredQuery = baseQuery
                    .startAt(string)
                    .endAt(string + "\uf8ff");
            FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                    .setQuery(filteredQuery, UserModel.class)
                    .build();
            userAdapter.updateOptions(options);
        } else {
            FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                    .setQuery(baseQuery, UserModel.class)
                    .build();
            userAdapter.updateOptions(options);
        }
    }

}
