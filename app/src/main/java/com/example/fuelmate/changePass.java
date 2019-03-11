package com.example.fuelmate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePass extends Fragment {

    private FirebaseUser mUser;
    private AuthCredential authCreds;
    private EditText email, oldPass, newPass;
    private Button changPassBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        return inflater.inflate(R.layout.changepass_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = (EditText) getView().findViewById(R.id.email);
        oldPass = (EditText) getView().findViewById(R.id.oldPass);
        newPass = (EditText) getView().findViewById(R.id.newPass);
        changPassBtn = (Button) getView().findViewById(R.id.changePass);


        changPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().equals("") || oldPass.getText().toString().equals("") || newPass.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();

                } else {


                    authCreds = EmailAuthProvider.getCredential(email.getText().toString(), oldPass.getText().toString());


                    mUser.reauthenticate(authCreds).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Password Updated Succesfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity().getApplicationContext(), "Error Updating Password", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {

                                Toast.makeText(getActivity().getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                }
            });

    }
}
