package com.example.mobilepayroll;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditEmployee extends AppCompatActivity {
    private TextView Display_Name;
    private EditText Display_Email, Display_Phone, Display_Role, Display_Status;
    private ImageView Display_Image;
    private String fullName, department, email, imageUrl, phoneNumber, status, basicPay;
    private Button btnSave;
    private StorageReference storageReference;
    private Uri imageUri;

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

        storageReference = FirebaseStorage.getInstance().getReference();

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
                updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone, imageUrl);
            }
        });

        Display_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
    }

    private void updateEmployeeDocument(String updatedName, String updatedDepartment, String updatedStatus,
                                        String updatedEmail, String updatedPhone, String updatedImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference employeeRef = db.collection("employees").document(fullName); // Update with actual document ID
        employeeRef.update("fullName", updatedName,
                        "department", updatedDepartment,
                        "status", updatedStatus, "email", updatedEmail, "phoneNumber", updatedPhone, "imageUrl", updatedImage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEmployee.this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();
                        fullName = updatedName;
                        department = updatedDepartment;
                        status = updatedStatus;
                        email = updatedEmail;
                        phoneNumber = updatedPhone;
                        imageUrl = updatedImage;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEmployee.this, "Failed to update employee details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(Display_Image);

            if (imageUri != null) {
                String documentId = getIntent().getStringExtra("email");
                if (documentId != null) {
                    uploadImage(documentId);
                } else {
                    Toast.makeText(EditEmployee.this, "Error: Document ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditEmployee.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(String email) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("employees/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    imageUrl = uriTask.getResult().toString();
                                    String updatedName = Display_Name.getText().toString();
                                    String updatedDepartment = Display_Role.getText().toString();
                                    String updatedStatus = Display_Status.getText().toString();
                                    String updatedEmail = Display_Email.getText().toString();
                                    String updatedPhone = Display_Phone.getText().toString();
                                    updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone, imageUrl);
                                    // Navigate to another page after the update button is clicked
                                    Intent intent = new Intent(EditEmployee.this, EmployeeDisplay.class);
                                    intent.putExtra("fullName", updatedName);
                                    intent.putExtra("email", updatedEmail);
                                    intent.putExtra("department", updatedDepartment);
                                    intent.putExtra("status", updatedStatus);
                                    intent.putExtra("phoneNumber", updatedPhone);
                                    intent.putExtra("imageUrl", imageUrl);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(EditEmployee.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditEmployee.this, "Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditEmployee.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}
