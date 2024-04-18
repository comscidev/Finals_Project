package com.example.mobilepayroll;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profilepage_function extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profilepage_function);

        TextView fname = findViewById(R.id.textFullName);
        TextView username = findViewById(R.id.textusername);
        TextView email = findViewById(R.id.showemail);
        TextView change_pass = findViewById(R.id.showpass);
        ImageView back = findViewById(R.id.backIcon);
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        String userID = Auth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    fname.setText(documentSnapshot.getString("email"));
                    username.setText(documentSnapshot.getString("email"));
                    email.setText(documentSnapshot.getString("email"));
                }
            }
        });
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reset_password = new EditText(v.getContext());
                AlertDialog.Builder password_reset = new AlertDialog.Builder(v.getContext());
                password_reset.setTitle("Reset Password");
                password_reset.setTitle("Enter email to reset password");
                password_reset.setView(reset_password);
                password_reset.setPositiveButton("Yes", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = reset_password.getText().toString();

                            }
                        });
                password_reset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                password_reset.create().show();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profilepage_function.this,
                        emlist_function.class);
                startActivity(intent);
            }
        });

    }
}
