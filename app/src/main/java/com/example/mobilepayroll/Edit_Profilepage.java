package com.example.mobilepayroll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Edit_Profilepage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userID;
    private ImageView adminProfileImage;
    private DocumentSnapshot documentSnapshot;
    private StorageReference storageReference;
    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes, deleteUserButton;

    private EditText profileName, changeAdminEmail, changeAdminPassword, changeProfilePosition;
    private ImageButton addProfileImage;
    private Button saveEditButton;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profilepage);
        initializeViews();
        setupProfileImage();
        setupProfileSnapshotListener();
        setupSaveEditButtonListener();
        setupBackButtonListener();
        setupDeleteUserButtonListener();
        setupAddProfileImageButtonListener();
    }

    private void initializeViews() {
        profileName = findViewById(R.id.edit_profile_Name);
        changeAdminEmail = findViewById(R.id.edit_profile_Email);
        changeAdminPassword = findViewById(R.id.edit_profile_Password);
        changeProfilePosition = findViewById(R.id.edit_profile_Job);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        adminProfileImage = findViewById(R.id.profile_image);
        addProfileImage = findViewById(R.id.floatingCameraIcon);
        saveEditButton = findViewById(R.id.save_btn);
        backButton = findViewById(R.id.edit_profile_back_btn);
        deleteUserButton = findViewById(R.id.delete_btn);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setupProfileImage() {
        StorageReference profileRef = storageReference.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(adminProfileImage));
    }

    private void setupProfileSnapshotListener() {
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (snapshot, error) -> {
            if (snapshot != null) {
                documentSnapshot = snapshot;
                profileName.setText(snapshot.getString("fullname"));
                changeAdminEmail.setText(snapshot.getString("email"));
                changeProfilePosition.setText(snapshot.getString("position"));
            }
        });
    }

    private void setupSaveEditButtonListener() {
        saveEditButton.setOnClickListener(v -> {
            String newEmail = changeAdminEmail.getText().toString();
            String newPassword = changeAdminPassword.getText().toString();
            String newPosition = changeProfilePosition.getText().toString();

            if (isEmailChanged(newEmail)) {
                updateEmail(newEmail);
            }

            if (isPasswordValid(newPassword)) {
                updatePassword(newPassword);
            }

            if (isPositionChanged(newPosition)) {
                updatePosition(newPosition);
            }

            navigateToProfilePageFunction();
        });
    }

    private boolean isEmailChanged(String newEmail) {
        return !newEmail.equals(documentSnapshot.getString("email"));
    }

    private void updateEmail(String newEmail) {
        if (isValidEmail(newEmail)) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.updateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Email Updated Successfully");
                        updateFirestoreEmail(newEmail);
                    } else {
                        showToast("Failed to Update Email");
                    }
                });
            }
        } else {
            changeAdminEmail.setError("Invalid email format.");
        }
    }

    private void updateFirestoreEmail(String newEmail) {
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.update("email", newEmail);
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Password changed successfully!");
                } else {
                    showToast("Failed to change password: " + task.getException().getMessage());
                }
            });
        }
    }

    private boolean isPositionChanged(String newPosition) {
        return !newPosition.equals(documentSnapshot.getString("position"));
    }

    private void updatePosition(String newPosition) {
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.update("position", newPosition).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Position Updated");
            } else {
                showToast("Position Failed to Update");
            }
        });
    }

    private void navigateToProfilePageFunction() {
        Intent intent = new Intent(Edit_Profilepage.this, Profilepage_function.class);
        startActivity(intent);
    }

    private void setupBackButtonListener() {
        backButton.setOnClickListener(v -> showDialog());
    }

    private void setupDeleteUserButtonListener() {
        deleteUserButton.setOnClickListener(v -> showDeleteDialog());
    }

    private void setupAddProfileImageButtonListener() {
        addProfileImage.setOnClickListener(v -> openGallery());
    }

    private void showDeleteDialog() {
        dialog = createDialog(R.layout.delete_dialog);
        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        btnDialogYes.setOnClickListener(v -> {
            deleteUser();
            dialog.dismiss();
        });

        btnDialogNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private Dialog createDialog(int layoutId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(layoutId);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);
        return dialog;
    }

    private void deleteUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    deleteUserFromFirestore();
                } else {
                    showToast("Failed to Delete");
                }
            });
        }
    }

    private void deleteUserFromFirestore() {
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToLoginPage();
                showToast("Account Deleted");
            } else {
                showToast("Failed to Delete");
            }
        });
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(Edit_Profilepage.this, MainActivity.class);
        startActivity(intent);
    }

    private void showDialog() {
        dialog = createDialog(R.layout.cancel_dialog);
        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        btnDialogYes.setOnClickListener(v -> {
            navigateToProfilePageFunction();
            dialog.dismiss();
        });

        btnDialogNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebaseStorage(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/" + userID + "/profile.jpg");
        fileRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getDownloadUrlAndSave(fileRef);
            } else {
                showToast("Upload Failed: " + task.getException().getMessage());
            }
        });
    }

    private void getDownloadUrlAndSave(StorageReference fileRef) {
        fileRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
            if (uriTask.isSuccessful()) {
                Uri uri = uriTask.getResult();
                Picasso.get().load(uri).into(adminProfileImage);
                saveImageUrlToFirestore(uri.toString());
            } else {
                showToast("Failed to get download URL");
            }
        });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        DocumentReference userRef = db.collection("users").document(userID);
        userRef.update("profileImageUrl", imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Image URL saved");
            } else {
                showToast("Failed to save Image URL");
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String message) {
        Toast.makeText(Edit_Profilepage.this, message, Toast.LENGTH_SHORT).show();
    }
}
