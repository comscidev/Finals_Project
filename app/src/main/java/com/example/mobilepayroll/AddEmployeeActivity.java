package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEmployeeActivity extends AppCompatActivity {

    private String[] items = {"Regular", "Probationary", "Part-time"};
    private AutoCompleteTextView autoCompleteTxt;
    private ArrayAdapter<String> adapterItems;
    private EditText empFullName, empEmail, empPhone, empDepartment, empBasicPay;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        InitializeUI();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getFullName = empFullName.getText().toString();
                String getEmail = empEmail.getText().toString();
                String getPhoneNumber = empPhone.getText().toString();
                String getDepartment = empDepartment.getText().toString();
                String getBasicPay = empBasicPay.getText().toString();
                String getEmpStatus = autoCompleteTxt.getText().toString();

                if (validateInputs(getFullName, getEmail, getPhoneNumber, getDepartment, getBasicPay, getEmpStatus)) {
                    navigateToAddEmployeePicture(getFullName, getEmail, getPhoneNumber, getDepartment, getBasicPay, getEmpStatus);
                }
            }
        });
    }

    private void InitializeUI() {
        empFullName = findViewById(R.id.add_fname);
        empEmail = findViewById(R.id.add_email);
        empPhone = findViewById(R.id.add_phone);
        empDepartment = findViewById(R.id.add_designation);
        empBasicPay = findViewById(R.id.add_basicpay);
        button = findViewById(R.id.next_btn);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_status, items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private boolean validateInputs(String fullName, String email, String phoneNumber, String department, String basicPay, String empStatus) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(department) || TextUtils.isEmpty(basicPay) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(empStatus)) {
            Toast.makeText(AddEmployeeActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(AddEmployeeActivity.this, "Invalid phone number format. Please enter only numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!TextUtils.isDigitsOnly(basicPay)) {
            Toast.makeText(AddEmployeeActivity.this, "Invalid basic pay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void navigateToAddEmployeePicture(String fullName, String email, String phoneNumber, String department, String basicPay, String empStatus) {
        Intent intent = new Intent(AddEmployeeActivity.this, AddEmployeePicture.class);
        intent.putExtra("FullName", fullName);
        intent.putExtra("Email", email);
        intent.putExtra("PhoneNumber", phoneNumber);
        intent.putExtra("Department", department);
        intent.putExtra("BasicPay", basicPay);
        intent.putExtra("Status", empStatus);
        startActivity(intent);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9()-]+$");
    }
}
