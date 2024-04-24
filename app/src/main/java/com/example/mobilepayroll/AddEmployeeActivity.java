package com.example.mobilepayroll;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddEmployeeActivity extends AppCompatActivity {

        String[] items = {"Regular", "Probationary", "Part-time"};
        AutoCompleteTextView autoCompleteTxt;
        ArrayAdapter<String> adapterItems;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_employee);

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