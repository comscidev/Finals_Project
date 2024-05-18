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

        initializeViews();
        setUpAutoCompleteTextView();
        setUpButtonClickListener();
    }

    private void initializeViews() {
        empFullName = findViewById(R.id.add_fname);
        empEmail = findViewById(R.id.add_email);
        empPhone = findViewById(R.id.add_phone);
        empDepartment = findViewById(R.id.add_designation);
        empBasicPay = findViewById(R.id.add_basicpay);
        button = findViewById(R.id.next_btn);
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
    }

    private void setUpAutoCompleteTextView() {
        adapterItems = new ArrayAdapter<>(this, R.layout.list_status, items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               }
        });
    }

    private void setUpButtonClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick();
            }
        });
    }

    private void handleButtonClick() {
        String fullName = empFullName.getText().toString();
        String email = empEmail.getText().toString();
        String phoneNumber = empPhone.getText().toString();
        String department = empDepartment.getText().toString();
        String basicPay = empBasicPay.getText().toString();
        String empStatus = autoCompleteTxt.getText().toString();

        if (ValidFields(fullName, email, phoneNumber, department, basicPay, empStatus)) {
            proceedToNextActivity(fullName, email, phoneNumber, department, basicPay, empStatus);
        }
    }

    private boolean ValidFields(String fullName, String email, String phoneNumber, String department, String basicPay, String empStatus) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(department) ||
                TextUtils.isEmpty(basicPay) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(empStatus)) {
            showToast("Please fill all the fields");
            return false;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showToast("Invalid phone number format. Please enter only numbers");
            return false;
        }

        if (!TextUtils.isDigitsOnly(basicPay)) {
            showToast("Invalid basic pay format. Please enter only numbers");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(AddEmployeeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void proceedToNextActivity(String fullName, String email, String phoneNumber, String department, String basicPay, String empStatus) {
        Intent GoToAddEmployeePicture = new Intent(AddEmployeeActivity.this, AddEmployeePicture.class);
        GoToAddEmployeePicture.putExtra("FullName", fullName);
        GoToAddEmployeePicture.putExtra("Email", email);
        GoToAddEmployeePicture.putExtra("PhoneNumber", phoneNumber);
        GoToAddEmployeePicture.putExtra("Department", department);
        GoToAddEmployeePicture.putExtra("BasicPay", basicPay);
        GoToAddEmployeePicture.putExtra("Status", empStatus);
        startActivity(GoToAddEmployeePicture);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9()-]+$");
    }
}
