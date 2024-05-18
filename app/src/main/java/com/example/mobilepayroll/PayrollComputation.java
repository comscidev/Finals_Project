package com.example.mobilepayroll;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class PayrollComputation extends AppCompatActivity {

    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes;

    private TextView empRate, empName, empDesignation, displayTotalEarnings, displayTotalDeduction,
            displayNetPay, cancelPayroll, empOvertimeRate, empOverTimePay, empBasicPay, empEmail, empStatus;

    private EditText empTotalDays, empTotalWeeks, empAdditionalPayment, empSpecialAllowance,
            payrollTitle, empTax, empSSS, empPHealth, empPagIbig, empCashAdvance, empMealAllowance, empShop;

    private Button saveBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_computation);

        initViews();
        setupDialog();
        populateFieldsFromIntent();
        setupListeners();
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();
        empName = findViewById(R.id.Payroll_EmpName);
        empDesignation = findViewById(R.id.Emp_Designation);
        empRate = findViewById(R.id.EmployeeRate);
        empTotalDays = findViewById(R.id.EmployeeTotalDays);
        empTotalWeeks = findViewById(R.id.EmployeeWeeklyHrs);
        empBasicPay = findViewById(R.id.EmployeeBasicPay);
        empOvertimeRate = findViewById(R.id.EmployeeOverTime);
        empOverTimePay = findViewById(R.id.EmployeeOverTimePay);
        empEmail = findViewById(R.id.EmpEmail);
        empStatus = findViewById(R.id.EmpStatus);
        empAdditionalPayment = findViewById(R.id.EmployeeAdditionalPay);
        empSpecialAllowance = findViewById(R.id.EmployeeSpecialAllowance);
        payrollTitle = findViewById(R.id.Payslip_Title);
        displayTotalEarnings = findViewById(R.id.DisplayTotalEarnings);
        displayTotalDeduction = findViewById(R.id.DisplayTotalDeductions);
        empTax = findViewById(R.id.EmployeeTax);
        empSSS = findViewById(R.id.EmployeeSSS);
        empPHealth = findViewById(R.id.EmployeePhilHealth);
        empPagIbig = findViewById(R.id.EmployeePagIbig);
        empCashAdvance = findViewById(R.id.EmployeeCashAdvance);
        empMealAllowance = findViewById(R.id.EmployeeMealAllowance);
        empShop = findViewById(R.id.EmployeeShop);
        displayNetPay = findViewById(R.id.DisplayNetPay);
        cancelPayroll = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.saveComputationBtn);
    }

    private void setupDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);

        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);
    }

    private void populateFieldsFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String fullName = intent.getStringExtra("fullName");
            String department = intent.getStringExtra("department");
            String basicPay = intent.getStringExtra("basicPay");
            String email = intent.getStringExtra("email");
            String status = intent.getStringExtra("status");

            empStatus.setText(status);
            empName.setText(fullName);
            empDesignation.setText(department);
            empRate.setText(basicPay);
            empEmail.setText(email);
        }
    }

    private void setupListeners() {
        saveBtn.setOnClickListener(v -> savePayrollData());

        btnDialogNo.setOnClickListener(v -> dialog.dismiss());

        btnDialogYes.setOnClickListener(v -> {
            Intent cancelPayrollIntent = new Intent(PayrollComputation.this, EmployeeList.class);
            startActivity(cancelPayrollIntent);
            dialog.dismiss();
        });

        cancelPayroll.setOnClickListener(v -> dialog.show());

        empName.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String fullName = s.toString().trim();
                loadUserData(fullName);
            }
        });

        TextWatcher earningsWatcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalEarnings();
            }
        };

        empRate.addTextChangedListener(earningsWatcher);
        empTotalDays.addTextChangedListener(earningsWatcher);
        empTotalWeeks.addTextChangedListener(earningsWatcher);
        empAdditionalPayment.addTextChangedListener(earningsWatcher);
        empSpecialAllowance.addTextChangedListener(earningsWatcher);

        TextWatcher deductionWatcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalDeduction();
            }
        };

        empTax.addTextChangedListener(deductionWatcher);
        empSSS.addTextChangedListener(deductionWatcher);
        empPHealth.addTextChangedListener(deductionWatcher);
        empPagIbig.addTextChangedListener(deductionWatcher);
        empCashAdvance.addTextChangedListener(deductionWatcher);
        empMealAllowance.addTextChangedListener(deductionWatcher);
        empShop.addTextChangedListener(deductionWatcher);

        TextWatcher netPayWatcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalNetPay();
            }
        };

        displayTotalEarnings.addTextChangedListener(netPayWatcher);
        displayTotalDeduction.addTextChangedListener(netPayWatcher);
    }

    private void loadUserData(String fullName) {
        CollectionReference employeesRef = db.collection("employees");
        Query query = employeesRef.whereEqualTo("fullName", fullName).limit(1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String basicPay = document.getString("basicPay");
                    empRate.setText(basicPay);
                    String designation = document.getString("department");
                    empDesignation.setText(designation);
                    String email = document.getString("email");
                    empEmail.setText(email);
                    String status = document.getString("status");
                    empStatus.setText(status);
                }
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void calculateTotalEarnings() {
        double empRateValue = parseDouble(empRate.getText().toString());
        double totalDaysOfWork = parseDouble(empTotalDays.getText().toString());
        double empBasicPayValue = empRateValue * totalDaysOfWork;
        double overtimeRate = empRateValue / 8 * 1.25;
        double totalOverTimePay = parseDouble(empTotalWeeks.getText().toString()) * overtimeRate;
        double additionalPayment = parseDouble(empAdditionalPayment.getText().toString());
        double specialAllowance = parseDouble(empSpecialAllowance.getText().toString());
        double totalEarnings = empBasicPayValue + totalOverTimePay + specialAllowance + additionalPayment;

        empOvertimeRate.setText(String.valueOf(overtimeRate));
        empOverTimePay.setText(String.valueOf(totalOverTimePay));
        empBasicPay.setText(String.valueOf(empBasicPayValue));
        displayTotalEarnings.setText(String.format(Locale.getDefault(), "₱%.2f", totalEarnings));
    }

    private void calculateTotalDeduction() {
        double empTaxValue = parseDouble(empTax.getText().toString());
        double empSSSValue = parseDouble(empSSS.getText().toString());
        double empPHealthValue = parseDouble(empPHealth.getText().toString());
        double empPagIbigValue = parseDouble(empPagIbig.getText().toString());
        double empCashAdvanceValue = parseDouble(empCashAdvance.getText().toString());
        double empMealAllowanceValue = parseDouble(empMealAllowance.getText().toString());
        double empShopValue = parseDouble(empShop.getText().toString());
        double totalDeduction = empTaxValue + empSSSValue + empPHealthValue + empPagIbigValue +
                empCashAdvanceValue + empMealAllowanceValue + empShopValue;

        displayTotalDeduction.setText(String.format(Locale.getDefault(), "₱%.2f", totalDeduction));
        calculateTotalNetPay();
    }

    private void calculateTotalNetPay() {
        double totalEarnings = parseDouble(displayTotalEarnings.getText().toString().replace("₱", ""));
        double totalDeduction = parseDouble(displayTotalDeduction.getText().toString().replace("₱", ""));
        double netPay = totalEarnings - totalDeduction;

        displayNetPay.setText(String.format(Locale.getDefault(), "₱%.2f", netPay));
    }

    private void savePayrollData() {
        String fullName = empName.getText().toString().trim();
        String department = empDesignation.getText().toString();
        String payrollTitleValue = payrollTitle.getText().toString();
        String email = empEmail.getText().toString();
        String status = empStatus.getText().toString();
        double totalEarnings = parseDouble(displayTotalEarnings.getText().toString().replace("₱", ""));
        double totalDeduction = parseDouble(displayTotalDeduction.getText().toString().replace("₱", ""));
        double netPay = parseDouble(displayNetPay.getText().toString().replace("₱", ""));

        Intent payrollDataIntent = new Intent(PayrollComputation.this, Payslip.class);
        payrollDataIntent.putExtra("FullName", fullName);
        payrollDataIntent.putExtra("Department", department);
        payrollDataIntent.putExtra("Email", email);
        payrollDataIntent.putExtra("Status", status);
        payrollDataIntent.putExtra("PayrollTitle", payrollTitleValue);
        payrollDataIntent.putExtra("TotalEarnings", String.format(Locale.getDefault(), "₱%.2f", totalEarnings));
        payrollDataIntent.putExtra("TotalDeduction", String.format(Locale.getDefault(), "₱%.2f", totalDeduction));
        payrollDataIntent.putExtra("NetPay", String.format(Locale.getDefault(), "₱%.2f", netPay));
        startActivity(payrollDataIntent);
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private abstract class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
