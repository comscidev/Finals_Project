package com.example.mobilepayroll;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirestoreRecyclerAdapter<UserModel, UserAdapter.Holder> {

    private Context context;

    public UserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull UserModel userModel) {
        holder.bind(userModel);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_data, parent, false);
        return new Holder(view);
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView fullName, department;
        ImageView employee_pic;
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.textViewName);
            department = itemView.findViewById(R.id.textJobRole);
            employee_pic = itemView.findViewById(R.id.emp_pic);
            cardView = itemView.findViewById(R.id.Card_View);
        }

        public void bind(UserModel userModel) {
            fullName.setText("Name: " + userModel.fullName);
            department.setText("Department: " + userModel.department);
            Picasso.get().load(userModel.imageUrl).into(employee_pic);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EmployeeDisplay.class);
                    intent.putExtra("fullName", userModel.fullName);
                    intent.putExtra("department", userModel.department);
                    intent.putExtra("imageUrl", userModel.imageUrl);
                    intent.putExtra("email", userModel.email);
                    intent.putExtra("status", userModel.status);
                    intent.putExtra("phoneNumber", userModel.phoneNumber);
                    intent.putExtra("basicPay", userModel.basicPay);
                    context.startActivity(intent);
                    Toast.makeText(context, "You are viewing " + userModel.fullName + "'s profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
