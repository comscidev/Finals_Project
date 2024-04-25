package com.example.mobilepayroll;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployeeList extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_employees);

//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()){
//                case R.id.bottom_employees:
//                    return true;
//                case R.id.bottom_profile:
//                    startActivity(new Intent(getApplicationContext(), Profilepage_function.class));
//                    finish();
//                    return true;
//            }
//            return false;
//        });
    }
}