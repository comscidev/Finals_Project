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
    private TextView displayName;
    private EditText displayEmail, displayPhone, displayRole, displayStatus;
    private ImageView displayImage;
    private Button btnSave;
    private StorageReference storageReference;
    private Uri imageUri;
    private String fullName, department, email, imageUrl, phoneNumber, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        initializeViews();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent getData = getIntent();
        if (getData != null) {
            populateEmployeeData(getData);
        }

        btnSave.setOnClickListener(v -> saveEmployeeDetails());
        displayImage.setOnClickListener(v -> openGallery());
    }

    private void initializeViews() {
        displayName = findViewById(R.id.Payslip_Name);
        displayEmail = findViewById(R.id.display_email);
        displayPhone = findViewById(R.id.display_earnings);
        displayRole = findViewById(R.id.displayRole);
        displayImage = findViewById(R.id.displayphoto);
        displayStatus = findViewById(R.id.display_status);
        btnSave = findViewById(R.id.delete_btnn);
    }

    private void populateEmployeeData(Intent getData) {
        fullName = getData.getStringExtra("fullName");
        email = getData.getStringExtra("email");
        phoneNumber = getData.getStringExtra("phoneNumber");
        department = getData.getStringExtra("department");
        imageUrl = getData.getStringExtra("imageUrl");
        status = getData.getStringExtra("status");

        displayName.setText(fullName);
        displayRole.setText(department);
        displayStatus.setText(status);
        displayEmail.setText(email);
        displayPhone.setText(phoneNumber);
        Picasso.get().load(imageUrl).into(displayImage);
    }

    private void saveEmployeeDetails() {
        String updatedName = displayName.getText().toString();
        String updatedDepartment = displayRole.getText().toString();
        String updatedStatus = displayStatus.getText().toString();
        String updatedEmail = displayEmail.getText().toString();
        String updatedPhone = displayPhone.getText().toString();

        updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone, imageUrl);
    }

    private void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, 1000);
    }

    private void updateEmployeeDocument(String updatedName, String updatedDepartment, String updatedStatus,
                                        String updatedEmail, String updatedPhone, String updatedImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference employeeRef = db.collection("employees").document(fullName);
        employeeRef.update("fullName", updatedName, "department", updatedDepartment, "status", updatedStatus,
                        "email", updatedEmail, "phoneNumber", updatedPhone, "imageUrl", updatedImage)
                .addOnSuccessListener(aVoid -> onEmployeeUpdateSuccess(updatedName, updatedEmail, updatedDepartment, updatedStatus, updatedPhone))
                .addOnFailureListener(e -> onEmployeeUpdateFailure(e));
    }

    private void onEmployeeUpdateSuccess(String updatedName, String updatedEmail, String updatedDepartment,
                                         String updatedStatus, String updatedPhone) {
        Toast.makeText(EditEmployee.this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(EditEmployee.this, EmployeeDisplay.class);
        intent.putExtra("fullName", updatedName);
        intent.putExtra("email", updatedEmail);
        intent.putExtra("department", updatedDepartment);
        intent.putExtra("status", updatedStatus);
        intent.putExtra("phoneNumber", updatedPhone);
        intent.putExtra("imageUrl", imageUrl);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onEmployeeUpdateFailure(Exception e) {
        Toast.makeText(EditEmployee.this, "Failed to update employee details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(displayImage);
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("employees/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    imageUrl = uriTask.getResult().toString();
                                    saveEmployeeDetails();
                                } else {
                                    showToast("Failed to get image URL");
                                }
                            });
                        } else {
                            showToast("Upload Failed: " + task.getException().getMessage());
                        }
                    });
        } else {
            showToast("No image selected");
        }
    }

    private void showToast(String message) {
        Toast.makeText(EditEmployee.this, message, Toast.LENGTH_SHORT).show();
    }
}
