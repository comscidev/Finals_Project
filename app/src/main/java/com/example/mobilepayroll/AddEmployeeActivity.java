package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.firestore.DocumentReference;
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
        EditText Emp_FullName = findViewById(R.id.add_fname);
        EditText Emp_Email = findViewById(R.id.add_email);
        EditText Emp_phone = findViewById(R.id.add_phone);
        EditText Emp_Department = findViewById(R.id.add_designation);
        EditText Emp_BasicPay = findViewById(R.id.add_basicpay);
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
                String GetFullName = Emp_FullName.getText().toString();
                String GetEmail = Emp_Email.getText().toString();
                String GetPhoneNumber = Emp_phone.getText().toString();
                String GetDepartment =Emp_Department.getText().toString();
                String GetBasicPay = Emp_BasicPay.getText().toString();

                if (GetFullName.isEmpty() || GetEmail.isEmpty() || GetDepartment.isEmpty() || GetBasicPay.isEmpty() || GetPhoneNumber.isEmpty()) {
                    Toast.makeText(AddEmployeeActivity.this, "Please Fill all the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isDigitsOnly(GetPhoneNumber)) {
                    Toast.makeText(AddEmployeeActivity.this, "Invalid phone number format. Please enter only numbers", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (!TextUtils.isDigitsOnly(GetBasicPay)){
                    Toast.makeText(AddEmployeeActivity.this, "Invalid Basic pay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> user = new HashMap<>();
                user.put("Fullname", GetFullName);
                user.put("Email", GetEmail);
                user.put("Phone number", GetPhoneNumber);
                user.put("Designation", GetDepartment);
                user.put("Basic Pay", GetBasicPay);

                db.collection("employee").document(GetEmail)
                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AddEmployeeActivity.this, "New Employee Added", Toast.LENGTH_SHORT).show();
                                    Intent GotoAddEmployeePicture = new Intent(AddEmployeeActivity.this, AddEmployeePicture.class);
                                    startActivity(GotoAddEmployeePicture);
                                }else{
                                    Toast.makeText(AddEmployeeActivity.this, "Adding Employee Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            };


        });

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_status, items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }

        });



    }
}