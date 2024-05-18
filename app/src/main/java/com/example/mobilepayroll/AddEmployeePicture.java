package com.example.mobilepayroll;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AddEmployeePicture extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1000;

    private ImageView employeeProfPic;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_picture);

        initializeViews();
        setUpUploadClickListener();
        setUpDoneClickListener();
        setUpBackClickListener();
    }

    private void initializeViews() {
        TextView backToEmployeeList = findViewById(R.id.textBack);
        TextView uploadEmpPic = findViewById(R.id.Upload_emp_pic);
        Button doneUploadPic = findViewById(R.id.ButtonToEmpList);
        employeeProfPic = findViewById(R.id.EmployeePicture);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setUpUploadClickListener() {
        findViewById(R.id.Upload_emp_pic).setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
    }

    private void setUpDoneClickListener() {
        findViewById(R.id.ButtonToEmpList).setOnClickListener(v -> handleDoneClick());
    }

    private void handleDoneClick() {
        if (imageUri != null) {
            String documentId = getIntent().getStringExtra("Email");
            if (documentId != null) {
                uploadImage(documentId);
            } else {
                showToast("Error: Document ID not found");
            }
        } else {
            showToast("No image selected");
        }
    }

    private void setUpBackClickListener() {
        findViewById(R.id.textBack).setOnClickListener(v -> navigateBackToEmployeeList());
    }

    private void navigateBackToEmployeeList() {
        Intent backIntent = new Intent(AddEmployeePicture.this, EmployeeList.class);
        startActivity(backIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(employeeProfPic);
        }
    }

    private void uploadImage(String email) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("employees/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Image Uploaded");
                            getDownloadUrl(ref);
                        } else {
                            showToast("Upload Failed: " + task.getException().getMessage());
                        }
                    });
        } else {
            showToast("No image selected");
        }
    }

    private void getDownloadUrl(StorageReference ref) {
        ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
            if (uriTask.isSuccessful()) {
                String imageUrl = uriTask.getResult().toString();
                saveEmployeeToFirestore(imageUrl);
            } else {
                showToast("Failed to get image URL");
            }
        });
    }

    private void saveEmployeeToFirestore(String imageUrl) {
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("FullName");
        String email = intent.getStringExtra("Email");
        String phoneNumber = intent.getStringExtra("PhoneNumber");
        String department = intent.getStringExtra("Department");
        String basicPay = intent.getStringExtra("BasicPay");
        String status = intent.getStringExtra("Status");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Employee employee = new Employee(fullName, email, phoneNumber, department, basicPay, imageUrl, status);

        db.collection("employees").document(fullName)
                .set(employee)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Employee Added Successfully");
                        navigateToEmployeeList();
                    } else {
                        showToast("Failed to Add Employee");
                    }
                });
    }

    private void navigateToEmployeeList() {
        Intent GotoEmployeeList = new Intent(AddEmployeePicture.this, EmployeeList.class);
        startActivity(GotoEmployeeList);
    }

    private void showToast(String message) {
        Toast.makeText(AddEmployeePicture.this, message, Toast.LENGTH_SHORT).show();
    }
}
