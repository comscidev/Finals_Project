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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
            EditText fname = findViewById(R.id.add_fname);
            EditText eemail = findViewById(R.id.add_email);
            EditText phone = findViewById(R.id.add_phone);
            EditText Designation = findViewById(R.id.add_designation);
            EditText basicpay = findViewById(R.id.add_basicpay);
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
                    String fullname = fname.getText().toString();
                    String email = eemail.getText().toString();
                    String Phone_num = phone.getText().toString();
                    String Desig = Designation.getText().toString();
                    String BasicPay = basicpay.getText().toString();

                    if (fullname.isEmpty() || email.isEmpty() || Desig.isEmpty() || BasicPay.isEmpty() || Phone_num.isEmpty()) {
                        Toast.makeText(AddEmployeeActivity.this, "Please Fill all the Fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!TextUtils.isDigitsOnly(Phone_num)) {
                        Toast.makeText(AddEmployeeActivity.this, "Invalid Basicpay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isDigitsOnly(BasicPay)){
                        Toast.makeText(AddEmployeeActivity.this, "Invalid Basicpay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, Object> user = new HashMap<>();
                    user.put("Fullname", fullname);
                    user.put("Email", email);
                    user.put("Phone number", Phone_num);
                    user.put("Designation", Desig);
                    user.put("Basic Pay", BasicPay);

                    db.collection("employee")
                            .add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddEmployeeActivity.this, "Employee Success added", Toast.LENGTH_SHORT).show();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddEmployeeActivity.this, "Failed Registration", Toast.LENGTH_SHORT).show();
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