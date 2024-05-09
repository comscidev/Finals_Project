package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EmployeeDisplay extends AppCompatActivity {
    FirebaseFirestore db;
    String fullName, department, email, imageUrl, phoneNumber, status, basicPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_display);
        db = FirebaseFirestore.getInstance();
        ImageView DisplayEmpPic = findViewById(R.id.imageView0);
        TextView DisplayEmpName = findViewById(R.id.displayName);
        TextView DisplayEmpRole = findViewById(R.id.displayRole);
        TextView DisplayEMpStatus = findViewById(R.id.displayStatus);
        TextView DisplayEmpEmail = findViewById(R.id.displayEmail);
        TextView DisplayPhoneNum = findViewById(R.id.displayPhone);
        TextView Back_btn_toEmployeelist = findViewById(R.id.titleEmpInfo);
        Button DeleteEmpBtn = findViewById(R.id.btnDelete);
        Button PayrollBtn = findViewById(R.id.btnCreatePayroll);

        PayrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoPayrollPage();
            }
        });
        DeleteEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee();
            }
        });

        Back_btn_toEmployeelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoToEmployeeList = new Intent(EmployeeDisplay.this, EmployeeList.class);
                startActivity(GoToEmployeeList);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            fullName = intent.getStringExtra("fullName");
            department = intent.getStringExtra("department");
            imageUrl = intent.getStringExtra("imageUrl");
            status = intent.getStringExtra("status");
            email = intent.getStringExtra("email");
            phoneNumber = intent.getStringExtra("phoneNumber");
            basicPay = intent.getStringExtra("basicPay");


            DisplayEmpName.setText(fullName);
            DisplayEmpRole.setText(department);
            Picasso.get().load(imageUrl).into(DisplayEmpPic);
            DisplayEMpStatus.setText(status);
            DisplayEmpEmail.setText(email);
            DisplayPhoneNum.setText(phoneNumber);
        }
    }

    private void GotoPayrollPage() {
        Intent goToPayroll = new Intent(EmployeeDisplay.this, PayrollComputation.class);
        goToPayroll.putExtra("fullName", fullName);
        goToPayroll.putExtra("department", department);
        goToPayroll.putExtra("basicPay", basicPay);
        startActivity(goToPayroll);
    }

    private void deleteEmployee() {
        Intent intent = getIntent();
        if (intent != null) {
            db.collection("employees").document(fullName)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                storageRef.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EmployeeDisplay.this, "Employee Deleted!", Toast.LENGTH_SHORT).show();
                                                    Intent GotoEmployelist = new Intent(EmployeeDisplay.this, EmployeeList.class);
                                                    startActivity(GotoEmployelist);
                                                    finish();
                                                } else {
                                                    Toast.makeText(EmployeeDisplay.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(EmployeeDisplay.this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



}




