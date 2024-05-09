package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PayrollComputation extends AppCompatActivity {

    Dialog dialog;
    Button btnDialogNo, btnDialogYes;

    TextView Emp_Rate, Emp_Name, Emp_Designation, Emp_TotalEarnings, DisplayTotalEarnings, DisplayTotalDeduction,
            DisplayNetPay, CancelPayroll, Emp_OvertimeRate, Emp_OverTimePay, Emp_BasicPay;

    EditText  Emp_Total_Days, Emp_TotalWeeks, Emp_AdditionalPayment, Emp_SpecialAllowance,
            Payroll_Tittle,
            Emp_Tax, Emp_SSS, Emp_PHealth, Emp_PagIbig, Emp_CashAdvance, Emp_MealAllowance, Emp_Shop;

    Button Savebtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_computation);

        Emp_Name = findViewById(R.id.Payroll_EmpName);
        Emp_Designation = findViewById(R.id.Emp_Designation);
        Emp_Rate = findViewById(R.id.EmployeeRate);
        Emp_Total_Days = findViewById(R.id.EmployeeTotalDays);
        Emp_TotalWeeks = findViewById(R.id.EmployeeWeeklyHrs);
        Emp_BasicPay = findViewById(R.id.EmployeeBasicPay);
        Emp_OvertimeRate = findViewById(R.id.EmployeeOverTime);
        Emp_OverTimePay = findViewById(R.id.EmployeeOverTimePay);
        Emp_AdditionalPayment = findViewById(R.id.EmployeeAdditionalPay);
        Emp_SpecialAllowance = findViewById(R.id.EmployeeSpecialAllowance);
        Emp_TotalEarnings = findViewById(R.id.Total_Earnings);
        DisplayTotalEarnings = findViewById(R.id.DisplayTotalEarnings);
        DisplayTotalDeduction = findViewById(R.id.DisplayTotalDeductions);
        Emp_Tax = findViewById(R.id.EmployeeTax);
        Emp_SSS = findViewById(R.id.EmployeeSSS);
        Emp_PHealth = findViewById(R.id.EmployeePhilHealth);
        Emp_PagIbig = findViewById(R.id.EmployeePagIbig);
        Emp_CashAdvance = findViewById(R.id.EmployeeCashAdvance);
        Emp_MealAllowance = findViewById(R.id.EmployeeMealAllowance);
        Emp_Shop = findViewById(R.id.EmployeeShop);
        DisplayNetPay = findViewById(R.id.DisplayNetPay);
        CancelPayroll = findViewById(R.id.cancel_btn);
        Savebtn = findViewById(R.id.saveComputationBtn);

        // Initialize dialog and other components here...
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);

        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);

        // Automatically set employee's name and department from intent
        Intent intent = getIntent();
        if (intent != null) {
            String fullName = intent.getStringExtra("fullName");
            String department = intent.getStringExtra("department");
            String basicPay = intent.getStringExtra("basicPay");
            Emp_Name.setText(fullName);
            Emp_Designation.setText(department);
            Emp_Rate.setText(basicPay);
        }

        Savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    savePayrollData();
                    Intent GotoPayrollAgain = new Intent(PayrollComputation.this, PayrollComputation.class);
                    startActivity(GotoPayrollAgain);
            }
        });


        btnDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CancelPayroll = new Intent(PayrollComputation.this, EmployeeList.class);
                startActivity(CancelPayroll);
                dialog.dismiss();
            }
        });

        CancelPayroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        db = FirebaseFirestore.getInstance();

        Emp_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String fullName = s.toString().trim();
                loadUserData(fullName);
            }
        });

        Emp_Rate.addTextChangedListener(textWatcher);
        Emp_Total_Days.addTextChangedListener(textWatcher);
        Emp_TotalWeeks.addTextChangedListener(textWatcher);
        Emp_AdditionalPayment.addTextChangedListener(textWatcher);
        Emp_SpecialAllowance.addTextChangedListener(textWatcher);

        Emp_Tax.addTextChangedListener(deductionWatcher);
        Emp_SSS.addTextChangedListener(deductionWatcher);
        Emp_PHealth.addTextChangedListener(deductionWatcher);
        Emp_PagIbig.addTextChangedListener(deductionWatcher);
        Emp_CashAdvance.addTextChangedListener(deductionWatcher);
        Emp_MealAllowance.addTextChangedListener(deductionWatcher);
        Emp_Shop.addTextChangedListener(deductionWatcher);
    }

    private void loadUserData(String fullName) {
        CollectionReference employeesRef = db.collection("employees");
        Query query = employeesRef.whereEqualTo("fullName", fullName);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                String basicPay = documentSnapshot.getString("basicPay");
                Emp_Rate.setText(basicPay);
                String designation = documentSnapshot.getString("department");
                Emp_Designation.setText(designation);
                Emp_Name.setText(fullName);
            } else {
                Emp_Rate.setText("");
                Emp_Designation.setText("");
            }
        }).addOnFailureListener(e -> {
            Emp_Rate.setText("");
            Emp_Designation.setText("");
            Emp_Name.setText(""); // Clear the name field if data retrieval fails
            Log.e("Firestore", "Error retrieving user data", e);
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            calculateTotalEarnings();
        }
    };

    private final TextWatcher deductionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            calculateTotalDeduction();
        }
    };

    private void calculateTotalEarnings() {
        double EmpRate = parseDouble(Emp_Rate.getText().toString());
        double TotalDaysOfWork = parseDouble(Emp_Total_Days.getText().toString());
        double EmpBasicPay = EmpRate * TotalDaysOfWork;
        double OvertimeRate = EmpRate / 8 * 1.25;
        double TotalOverTimePay = parseDouble(Emp_TotalWeeks.getText().toString()) * OvertimeRate;
        double AdditionalPayment = parseDouble(Emp_AdditionalPayment.getText().toString());
        double SpecialAllowance = parseDouble(Emp_SpecialAllowance.getText().toString());
        double TotalEarnings = EmpBasicPay + TotalOverTimePay + SpecialAllowance + AdditionalPayment;

        Emp_OvertimeRate.setText(String.valueOf(OvertimeRate));
        Emp_OverTimePay.setText(String.valueOf(TotalOverTimePay));
        Emp_BasicPay.setText(String.valueOf(EmpBasicPay));
        DisplayTotalEarnings.setText(String.format(Locale.getDefault(), "₱%.2f", TotalEarnings));
    }


    private void calculateTotalDeduction() {
        double Emptax = parseDouble(Emp_Tax.getText().toString());
        double EmpSSS = parseDouble(Emp_SSS.getText().toString());
        double EMpPhealth = parseDouble(Emp_PHealth.getText().toString());
        double EMpPagIbig = parseDouble(Emp_PagIbig.getText().toString());
        double EMpCashAdvance = parseDouble(Emp_CashAdvance.getText().toString());
        double EmpMealAllowance = parseDouble(Emp_MealAllowance.getText().toString());
        double EmpShop = parseDouble(Emp_Shop.getText().toString());
        double TotalDeduction = Emptax + EmpSSS+ EMpPhealth + EMpPagIbig + EMpCashAdvance + EmpMealAllowance + EmpShop;

        DisplayTotalDeduction.setText(String.format(Locale.getDefault(), "₱%.2f", TotalDeduction));
        calculateTotalNetPay();
    }

    private void calculateTotalNetPay() {
        double TotalEarnings = parseDouble(DisplayTotalEarnings.getText().toString().replace("₱", ""));
        double TotalDeduction = parseDouble(DisplayTotalDeduction.getText().toString().replace("₱", ""));
        double NetPay = TotalEarnings - TotalDeduction;

        DisplayNetPay.setText(String.format(Locale.getDefault(), "₱%.2f", NetPay));
    }

    private void savePayrollData() {
        String FullName = Emp_Name.getText().toString().trim();
        String Designation = Emp_Designation.getText().toString();
        double Rate = parseDouble(Emp_Rate.getText().toString());
        double TotalEarnings = parseDouble(DisplayTotalEarnings.getText().toString().replace("₱", ""));
        double TotalDeduction = parseDouble(DisplayTotalDeduction.getText().toString().replace("₱", ""));
        double NetPay = parseDouble(DisplayNetPay.getText().toString().replace("₱", ""));

        Map<String, Object> payrollData = new HashMap<>();
        payrollData.put("fullName", FullName);
        payrollData.put("designation", Designation);
        payrollData.put("rate", Rate);
        payrollData.put("totalEarnings", TotalEarnings);
        payrollData.put("totalDeduction", TotalDeduction);
        payrollData.put("netPay", NetPay);

        db.collection("payrolls").document(FullName).set(payrollData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PayrollComputation.this, "Payslip stored in Database", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PayrollComputation.this, "Failed to update Database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
