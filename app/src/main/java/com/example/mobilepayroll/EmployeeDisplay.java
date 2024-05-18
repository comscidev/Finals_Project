package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EmployeeDisplay extends AppCompatActivity {
    private static final int EDIT_EMPLOYEE_REQUEST_CODE = 1;

    private FirebaseFirestore db;
    private String fullName, department, email, imageUrl, phoneNumber, status, basicPay;
    private ImageView displayEmpPic;
    private TextView displayEmpName, displayEmpRole, displayEmpStatus, displayEmpEmail, displayPhoneNum;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_display);

        db = FirebaseFirestore.getInstance();
        initializeViews();

        handleIntent(getIntent());
        setButtonListeners();
    }

    private void initializeViews() {
        displayEmpPic = findViewById(R.id.displayphoto);
        displayEmpName = findViewById(R.id.Payslip_Name);
        displayEmpRole = findViewById(R.id.displayRole);
        displayEmpStatus = findViewById(R.id.display_status);
        displayEmpEmail = findViewById(R.id.display_email);
        displayPhoneNum = findViewById(R.id.display_earnings);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            fullName = intent.getStringExtra("fullName");
            department = intent.getStringExtra("department");
            imageUrl = intent.getStringExtra("imageUrl");
            status = intent.getStringExtra("status");
            email = intent.getStringExtra("email");
            phoneNumber = intent.getStringExtra("phoneNumber");
            basicPay = intent.getStringExtra("basicPay");

            displayEmployeeDetails();
        }
    }

    private void displayEmployeeDetails() {
        displayEmpName.setText(fullName);
        displayEmpRole.setText(department);
        Picasso.get().load(imageUrl).into(displayEmpPic);
        displayEmpStatus.setText(status);
        displayEmpEmail.setText(email);
        displayPhoneNum.setText(phoneNumber);
    }

    private void setButtonListeners() {
        Button editEmpBtn = findViewById(R.id.edit_Emp_Info);
        Button payrollBtn = findViewById(R.id.pay_btn);
        Button deleteEmpBtn = findViewById(R.id.delete_btnn);

        editEmpBtn.setOnClickListener(v -> navigateToEditEmployee());
        payrollBtn.setOnClickListener(v -> navigateToPayrollPage());
        deleteEmpBtn.setOnClickListener(v -> showDeleteDialog());
    }

    private void navigateToEditEmployee() {
        Intent goToEditEmployee = new Intent(EmployeeDisplay.this, EditEmployee.class);
        goToEditEmployee.putExtra("fullName", fullName);
        goToEditEmployee.putExtra("department", department);
        goToEditEmployee.putExtra("imageUrl", imageUrl);
        goToEditEmployee.putExtra("status", status);
        goToEditEmployee.putExtra("email", email);
        goToEditEmployee.putExtra("phoneNumber", phoneNumber);
        startActivityForResult(goToEditEmployee, EDIT_EMPLOYEE_REQUEST_CODE);
    }

    private void navigateToPayrollPage() {
        Intent goToPayroll = new Intent(EmployeeDisplay.this, PayrollComputation.class);
        goToPayroll.putExtra("fullName", fullName);
        goToPayroll.putExtra("department", department);
        goToPayroll.putExtra("basicPay", basicPay);
        goToPayroll.putExtra("email", email);
        goToPayroll.putExtra("status", status);
        startActivity(goToPayroll);
    }

    private void showDeleteDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);

        Button btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        Button btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        btnDialogYes.setOnClickListener(v -> {
            deleteEmployee();
            dialog.dismiss();
        });

        btnDialogNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteEmployee() {
        db.collection("employees").document(fullName)
                .delete()
                .addOnCompleteListener(this::handleEmployeeDeletion);
    }

    private void handleEmployeeDeletion(Task<Void> task) {
        if (task.isSuccessful()) {
            deleteEmployeeImage();
        } else {
            Toast.makeText(EmployeeDisplay.this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEmployeeImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageRef.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmployeeDisplay.this, "Employee Deleted!", Toast.LENGTH_SHORT).show();
                        navigateToEmployeeList();
                    } else {
                        Toast.makeText(EmployeeDisplay.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToEmployeeList() {
        Intent gotoEmployeeList = new Intent(EmployeeDisplay.this, EmployeeList.class);
        startActivity(gotoEmployeeList);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EMPLOYEE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            handleEditedEmployeeData(data);
        }
    }

    private void handleEditedEmployeeData(Intent data) {
        fullName = data.getStringExtra("fullName");
        department = data.getStringExtra("department");
        imageUrl = data.getStringExtra("imageUrl");
        status = data.getStringExtra("status");
        email = data.getStringExtra("email");
        phoneNumber = data.getStringExtra("phoneNumber");

        displayEmployeeDetails();
    }
}
