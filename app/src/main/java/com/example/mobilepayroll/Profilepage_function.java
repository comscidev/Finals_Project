package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        TextView password = findViewById(R.id.showpass);
        TextView email = findViewById(R.id.showemail);
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
                    password.setText(documentSnapshot.getString("password"));
                }
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
