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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    String[] items = {"Regular", "Probationary", "Part-time"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText empFullName = findViewById(R.id.add_fname);
        EditText empEmail = findViewById(R.id.add_email);
        EditText empPhone = findViewById(R.id.add_phone);
        EditText empDepartment = findViewById(R.id.add_designation);
        EditText empBasicPay = findViewById(R.id.add_basicpay);
        Button button = findViewById(R.id.next_btn);
        ImageButton cancel = findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEmployeeActivity.this, EmployeeList.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getFullName = empFullName.getText().toString();
                String getEmail = empEmail.getText().toString();
                String getPhoneNumber = empPhone.getText().toString();
                String getDepartment = empDepartment.getText().toString();
                String getBasicPay = empBasicPay.getText().toString();
                String getEmpStatus = autoCompleteTxt.getText().toString();

                if (TextUtils.isEmpty(getFullName) || TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getDepartment) || TextUtils.isEmpty(getBasicPay) || TextUtils.isEmpty(getPhoneNumber)) {
                    Toast.makeText(AddEmployeeActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPhoneNumber(getPhoneNumber)) {
                    Toast.makeText(AddEmployeeActivity.this, "Invalid phone number format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isDigitsOnly(getBasicPay)) {
                    Toast.makeText(AddEmployeeActivity.this, "Invalid basic pay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("Fullname", getFullName);
                user.put("Email", getEmail);
                user.put("Phone number", getPhoneNumber);
                user.put("Designation", getDepartment);
                user.put("Basic Pay", getBasicPay);
                user.put("Status", getEmpStatus);

                db.collection("employee").document(getEmail)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddEmployeeActivity.this, "New Employee Added", Toast.LENGTH_SHORT).show();
                                    Intent gotoAddEmployeePicture = new Intent(AddEmployeeActivity.this, AddEmployeePicture.class);
                                    startActivity(gotoAddEmployeePicture);
                                } else {
                                    Toast.makeText(AddEmployeeActivity.this, "Adding Employee Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_status, items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    public boolean isValidPhoneNumber(String phoneNumber) {

        return phoneNumber.matches("^[0-9()-]+$");
    }
}
