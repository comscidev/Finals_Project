package com.example.mobilepayroll;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class PayrollComputation extends AppCompatActivity {

    TextView Emp_Name, Emp_Designation, Emp_TotalEarnings, DisplayTotalEarnings, DisplayTotalDeduction,
            DisplayNetPay;

    EditText Emp_Rate, Emp_Total_Days, Emp_TotalWeeks, Emp_AdditionalPayment, Emp_SpecialAllowance,
            Payroll_Tittle, Emp_OvertimeRate, Emp_BasicPay, Emp_OverTimePay,
            Emp_Tax, Emp_SSS, Emp_PHealth, Emp_PagIbig, Emp_CashAdvance, Emp_MealAllowance, Emp_Shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payroll_computation);

        Emp_Name = findViewById(R.id.Payroll_EmpName);
        Emp_Designation = findViewById(R.id.Emp_Designation);
        Payroll_Tittle = findViewById(R.id.Payslip_Title);
        Emp_Rate = findViewById(R.id.EmployeeRate);
        Emp_OvertimeRate = findViewById(R.id.EmployeeOverTime);
        Emp_TotalWeeks = findViewById(R.id.EmployeeWeeklyHrs);
        Emp_Total_Days = findViewById(R.id.EmployeeTotalDays);
        Emp_BasicPay = findViewById(R.id.EmployeeBasicPay);
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
        double TotalOverTimePay = parseDouble(Emp_TotalWeeks.getText().toString());
        double OvertimeRate = (EmpRate / 8 * 1.25);
        double OvetimePayment = OvertimeRate * TotalOverTimePay;
        double AdditionalPayment = parseDouble(Emp_AdditionalPayment.getText().toString());
        double SpecialAllowance = parseDouble(Emp_SpecialAllowance.getText().toString());
        double TotalEarnings = EmpBasicPay + OvetimePayment + SpecialAllowance + AdditionalPayment;

        Emp_OvertimeRate.setText(String.valueOf(OvertimeRate));
        Emp_OverTimePay.setText(String.valueOf(OvetimePayment));
        Emp_BasicPay.setText(String.valueOf(EmpBasicPay));
        DisplayTotalEarnings.setText(String.format(Locale.getDefault(), "₱%.2f", TotalEarnings));

        calculateTotalNetPay();
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

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
