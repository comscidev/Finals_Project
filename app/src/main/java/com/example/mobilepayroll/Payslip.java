package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Payslip extends AppCompatActivity {

    TextView nameTextView, designationTextView, displayStatus, displayEmail, displayEarnings,
            displayDeductions, displayNet, payslipTitle;
    Button payButton, deleteButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payslip);

        initializeViews();
        db = FirebaseFirestore.getInstance();
        setIntentData();
    }

    private void initializeViews() {
        nameTextView = findViewById(R.id.payslip_name);
        designationTextView = findViewById(R.id.payslip_dept);
        displayStatus = findViewById(R.id.display_status);
        displayEmail = findViewById(R.id.display_email);
        displayEarnings = findViewById(R.id.display_earnings);
        displayDeductions = findViewById(R.id.display_deduction);
        displayNet = findViewById(R.id.display_net);
        payslipTitle = findViewById(R.id.payslip_title);

        payButton = findViewById(R.id.pay_btn);
        deleteButton = findViewById(R.id.delete_btnn);
    }

    private void setIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("FullName");
            String designation = intent.getStringExtra("Department");
            String email = intent.getStringExtra("Email");
            String status = intent.getStringExtra("Status");
            String payrollTitle = intent.getStringExtra("PayrollTitle");
            String earnings = intent.getStringExtra("TotalEarnings");
            String deductions = intent.getStringExtra("TotalDeduction");
            String netPay = intent.getStringExtra("NetPay");

            payslipTitle.setText(payrollTitle);
            nameTextView.setText(name);
            designationTextView.setText(designation);
            displayStatus.setText(status);
            displayEmail.setText(email);
            displayEarnings.setText(earnings);
            displayDeductions.setText(deductions);
            displayNet.setText(netPay);

            payButton.setOnClickListener(v -> saveToFirestoreAndSendEmail(email, payrollTitle, name, designation, earnings, deductions, netPay));
        }
    }

    private void saveToFirestoreAndSendEmail(String email, String payrollTitle, String name, String designation, String earnings, String deductions, String netPay) {
        Map<String, Object> payslipData = createPayslipData(name, designation, email, earnings, deductions, netPay);

        DocumentReference payrollRef = db.collection("payroll").document(name).collection("payrollTitles").document(payrollTitle);
        payrollRef.set(payslipData)
                .addOnSuccessListener(documentReference -> sendEmail(email, payrollTitle, name, designation, earnings, deductions, netPay))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save payslip data", Toast.LENGTH_SHORT).show());
    }

    private Map<String, Object> createPayslipData(String name, String designation, String email, String earnings, String deductions, String netPay) {
        Map<String, Object> payslipData = new HashMap<>();
        payslipData.put("FullName", name);
        payslipData.put("Department", designation);
        payslipData.put("Email", email);
        payslipData.put("Status", displayStatus.getText().toString());
        payslipData.put("TotalEarnings", earnings);
        payslipData.put("TotalDeduction", deductions);
        payslipData.put("NetPay", netPay);
        return payslipData;
    }

    private void sendEmail(String email, String payrollTitle, String name, String designation, String earnings, String deductions, String netPay) {
        String subject = "Payslip: " + payrollTitle;
        String body = "Hello,\n\nAttached is the payslip for " + name + " in the " + designation + " department.\n\nEarnings: " + earnings + "\nDeductions: " + deductions + "\nNet Pay: " + netPay + "\n\nRegards,\nMobile Payroll App";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
}
