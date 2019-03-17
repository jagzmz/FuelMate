package com.example.fuelmate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showinfo extends Fragment {


    String name, email, address, department, college, preferences, uid;

    ProgressBar pb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        uid = getArguments().getString("uid");
        return inflater.inflate(R.layout.showinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pb = view.findViewById(R.id.infoProgress);


        FirebaseDatabase.getInstance().getReference().child("Users/" + uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        pb.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.infoName)).setText(dataSnapshot.child("name").getValue().toString());
                        ((TextView) view.findViewById(R.id.infoEmail)).setText("Email: " + dataSnapshot.child("email").getValue().toString());
                        ((TextView) view.findViewById(R.id.infoAddress)).setText("Address: " + dataSnapshot.child("address").getValue().toString());
                        ((TextView) view.findViewById(R.id.infoCollege)).setText("College: " + dataSnapshot.child("college").getValue().toString());
                        ((TextView) view.findViewById(R.id.infoDepartment)).setText("Department: " + dataSnapshot.child("department").getValue().toString());
                        ((TextView) view.findViewById(R.id.infoPref)).setText("Preferences: " + dataSnapshot.child("preferences").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


    }
}
