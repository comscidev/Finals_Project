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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultKt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class AddEmployeePicture extends AppCompatActivity {
    StorageReference storageReference;
    ImageView EmployeeProfPic;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_employee_picture);
        TextView BackToEmployeeList = findViewById(R.id.textBack);
        TextView UploadEmpPic = findViewById(R.id.Upload_emp_pic);
        Button DoneUploadPic = findViewById(R.id.ButtonToEmpList);
        EmployeeProfPic = findViewById(R.id.EmployeePicture);
        storageReference = FirebaseStorage.getInstance().getReference();


        DoneUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GotoEmployeelistPage = new Intent(AddEmployeePicture.this, EmployeeList.class);
                startActivity(GotoEmployeelistPage);
            }
        });

        BackToEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToEmployeeListPage = new Intent(AddEmployeePicture.this, EmployeeList.class);
                startActivity(BackToEmployeeListPage);
            }
        });
        UploadEmpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Opengallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Opengallery, 1000);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                EmployeeProfPic.setImageURI(imageUri);
            }
        }
    }
}