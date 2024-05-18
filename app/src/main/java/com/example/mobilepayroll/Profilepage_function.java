package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profilepage_function extends AppCompatActivity {

    private Dialog dialog;
    private Button btnDialogCancel, btnDialogLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage_function);
        setupViews();
        setupFirestoreAndAuth();
        setupProfileImage();
        setupButtons();
    }

    private void setupViews() {
        dialog = new Dialog(Profilepage_function.this);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);

        btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        btnDialogLogout = dialog.findViewById(R.id.btnDialogLogout);
    }

    private void setupFirestoreAndAuth() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView displayName = findViewById(R.id.adminName);
        TextView displayPosition = findViewById(R.id.profileDepartment);
        TextView displayEmail = findViewById(R.id.profileEmail);
        ImageView adminProfileImage = findViewById(R.id.admin_profpic);

        String userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                displayName.setText(documentSnapshot.getString("fullName"));
                displayPosition.setText(documentSnapshot.getString("position"));
                displayEmail.setText(documentSnapshot.getString("email"));

                String imageUrl = documentSnapshot.getString("profileImageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    loadProfileImage(imageUrl, adminProfileImage);
                } else {
                    adminProfileImage.setImageResource(R.drawable.default_image);
                }
            }
        });
    }

    private void loadProfileImage(String imageUrl, ImageView adminProfileImage) {
        Picasso.get()
                .load(Uri.parse(imageUrl))
                .networkPolicy(NetworkPolicy.OFFLINE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(adminProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(imageUrl)).into(adminProfileImage);
                    }
                });
    }

    private void setupButtons() {
        Button editProfile = findViewById(R.id.EditProfile);
        Button logout = findViewById(R.id.logout_btn);
        TextView BackToEmployeeList = findViewById(R.id.titleProfile);

        editProfile.setOnClickListener(v -> startActivity(new Intent(Profilepage_function.this, Edit_Profilepage.class)));

        btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        btnDialogLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Intent goToMainActivity = new Intent(Profilepage_function.this, MainActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToMainActivity);
                finish();
            } else {
                Toast.makeText(Profilepage_function.this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        logout.setOnClickListener(v -> dialog.show());

        BackToEmployeeList.setOnClickListener(v -> startActivity(new Intent(Profilepage_function.this, EmployeeList.class)));
    }

    private void setupProfileImage() {
        ImageView adminProfileImage = findViewById(R.id.admin_profpic);
        adminProfileImage.setImageResource(R.drawable.default_image);
    }
}
