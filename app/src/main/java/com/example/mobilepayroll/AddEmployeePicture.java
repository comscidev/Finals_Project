package com.example.mobilepayroll;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddEmployeePicture extends AppCompatActivity {
    ImageView employeeProfPic;
    StorageReference storageReference;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_picture);
        TextView backToEmployeeList = findViewById(R.id.textBack);
        TextView uploadEmpPic = findViewById(R.id.Upload_emp_pic);
        Button doneUploadPic = findViewById(R.id.ButtonToEmpList);
        employeeProfPic = findViewById(R.id.EmployeePicture);
        storageReference = FirebaseStorage.getInstance().getReference();

        uploadEmpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });

        doneUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    String documentId = getIntent().getStringExtra("Email");
                    if (documentId != null) {
                        uploadImage(documentId);
                    } else {
                        Toast.makeText(AddEmployeePicture.this, "Error: Document ID not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEmployeePicture.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        backToEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(AddEmployeePicture.this, EmployeeList.class);
                startActivity(backIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(employeeProfPic);
        }
    }

    private void uploadImage(String email) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("images/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddEmployeePicture.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    saveEmployeeToFirestore(imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEmployeePicture.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AddEmployeePicture.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEmployeeToFirestore(String imageUrl) {
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("FullName");
        String email = intent.getStringExtra("Email");
        String phoneNumber = intent.getStringExtra("PhoneNumber");
        String department = intent.getStringExtra("Department");
        String basicPay = intent.getStringExtra("BasicPay");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Employee employee = new Employee(fullName, email, phoneNumber, department, basicPay, imageUrl);

        db.collection("employees").document(email)
                .set(employee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddEmployeePicture.this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddEmployeePicture.this, EmployeeList.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEmployeePicture.this, "Failed to add Employee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
