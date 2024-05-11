package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditEmployee extends AppCompatActivity {
    TextView Display_Name;
    EditText Display_Email, Display_Phone, Display_Role, Display_Status;
    ImageView Display_Image;
    String fullName, department, email, imageUrl, phoneNumber, status, basicPay;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        Display_Name = findViewById(R.id.displayName);
        Display_Email = findViewById(R.id.displayEmail);
        Display_Phone = findViewById(R.id.displayPhone);
        Display_Role = findViewById(R.id.displayRole);
        Display_Image = findViewById(R.id.displayphoto);
        Display_Status = findViewById(R.id.displayStatus);
        btnSave = findViewById(R.id.Update_btn);

        Intent getdata = getIntent();
        if (getdata != null) {
            fullName = getdata.getStringExtra("fullName");
            email = getdata.getStringExtra("email");
            phoneNumber = getdata.getStringExtra("phoneNumber");
            department = getdata.getStringExtra("department");
            imageUrl = getdata.getStringExtra("imageUrl");
            status = getdata.getStringExtra("status");

            Display_Name.setText(fullName);
            Display_Role.setText(department);
            Display_Status.setText(status);
            Display_Email.setText(email);
            Display_Phone.setText(phoneNumber);
            Picasso.get().load(imageUrl).into(Display_Image);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = Display_Name.getText().toString();
                String updatedDepartment = Display_Role.getText().toString();
                String updatedStatus = Display_Status.getText().toString();
                String updatedEmail = Display_Email.getText().toString();
                String updatedPhone = Display_Phone.getText().toString();
                updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone);
            }
        });
    }

    private void updateEmployeeDocument(String updatedName, String updatedDepartment, String updatedStatus,
                                        String updatedEmail, String updatedPhone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference employeeRef = db.collection("employees").document(fullName); // Update with actual document ID
        employeeRef.update("fullName", updatedName,
                        "department", updatedDepartment,
                        "status", updatedStatus, "email", updatedEmail, "phoneNumber", updatedPhone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEmployee.this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();
                        fullName = updatedName;
                        department = updatedDepartment;
                        status = updatedStatus;
                        email = updatedEmail;
                        phoneNumber = updatedPhone;
                        Intent intent = new Intent(EditEmployee.this,EmployeeDisplay.class);
                        intent.putExtra("fullName", updatedName);
                        intent.putExtra("email", updatedEmail);
                        intent.putExtra("department", updatedDepartment);
                        intent.putExtra("status", updatedStatus);
                        intent.putExtra("phoneNumber", updatedPhone);
                        intent.putExtra("imageUrl", imageUrl);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEmployee.this, "Failed to update employee details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
