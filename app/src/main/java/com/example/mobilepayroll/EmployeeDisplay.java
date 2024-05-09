package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class EmployeeDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_display);
        ImageView DisplayEmpPic = findViewById(R.id.imageView0);
        TextView DisplayEmpName = findViewById(R.id.displayName);
        TextView DisplayEmpRole = findViewById(R.id.displayRole);
        TextView DisplayEMpStatus = findViewById(R.id.displayStatus);
        TextView DisplayEmpEmail = findViewById(R.id.displayEmail);
        TextView DisplayPhoneNum = findViewById(R.id.displayPhone);

        TextView Back_btn_toEmployeelist = findViewById(R.id.titleEmpInfo);

        Back_btn_toEmployeelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoToEmployeeList = new Intent(EmployeeDisplay.this, EmployeeList.class);
                startActivity(GoToEmployeeList);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String fullName = intent.getStringExtra("fullName");
            String department = intent.getStringExtra("department");
            String imageUrl = intent.getStringExtra("imageUrl");
            String status = intent.getStringExtra("status");
            String email = intent.getStringExtra("email");
            String phoneNumber = intent.getStringExtra("phoneNumber");

            DisplayEmpName.setText(fullName);
            DisplayEmpRole.setText(department);
            Picasso.get().load(imageUrl).into(DisplayEmpPic);
            DisplayEMpStatus.setText(status);
            DisplayEmpEmail.setText(email);
            DisplayPhoneNum.setText(phoneNumber);
        }
    }
}
