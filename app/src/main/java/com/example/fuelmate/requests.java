package com.example.fuelmate;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class requests extends Fragment {

    DatabaseReference mDbRef;
    private RecyclerView mUsersList;
    private String name;
    public Pair<String, String> dat = null;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter<users, requests.UserViewHolder> firebaseRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDbRef = FirebaseDatabase.getInstance().getReference();

        return inflater.inflate(R.layout.requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUsersList = (RecyclerView) getView().findViewById(R.id.requests_view);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseDatabase.getInstance().getReference().child("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ((TextView) getActivity().findViewById(R.id.nav_username)).setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

//        Toast.makeText(getView().getContext(),dat.second,Toast.LENGTH_LONG).show();

        final StringBuilder sb = new StringBuilder();

        sb.append("Friend-Req/" + MainActivity.name);

//        Toast.makeText(getContext(),sb.toString(),Toast.LENGTH_LONG).show();


        Query qry = FirebaseDatabase.getInstance().getReference().child("Friend-Req").child(MainActivity.name);


//        FirebaseDatabase.getInstance().getReference().child("Friend-Req/"+ ((TextView)getActivity().findViewById(R.id.nav_username)).getText()).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        Toast.makeText(getContext(),String.valueOf(dataSnapshot.getChildrenCount()),Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                }
//        );


        FirebaseRecyclerOptions<users> options = new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(qry, new SnapshotParser<users>() {
                    @NonNull
                    @Override
                    public users parseSnapshot(@NonNull DataSnapshot snapshot) {

//                        Toast.makeText(getContext(),snapshot.getKey(),Toast.LENGTH_LONG).show();

                        return new users(snapshot.child("name").getValue().toString(),
                                snapshot.child("college").getValue().toString(), snapshot.child("phone").getValue().toString());

                    }
                })
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, requests.UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull requests.UserViewHolder holder, final int position, @NonNull final users model) {


                if (model.getName() == null) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                } else {
                    holder.setname(model.getName());
                    holder.setColg(model.getColg());
                    holder.setcell(model.getCell());

                }

//                holder.root.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        v.findViewById(R.id.accept).setVisibility(View.GONE);
//                    }
//                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View v1 = v.findViewById(R.id.phone1);

                        if (v1.getVisibility() == View.GONE)
                            v1.setVisibility(View.VISIBLE);
                        else
                            v1.setVisibility(View.GONE);
                    }
                });

                mDbRef = FirebaseDatabase.getInstance().getReference();


            }

            @NonNull
            @Override
            public requests.UserViewHolder onCreateViewHolder(ViewGroup parent, int i) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_view, parent, false);

                NotificationCompat.Builder nb = new NotificationCompat.Builder(getContext());
                nb.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
                nb.setContentTitle("New Friend Request");
                nb.setContentText("You have a new Friend Request");

                ((NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(001, nb.build());


                return new requests.UserViewHolder(view);
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        LinearLayout root;
        String uname, name;

        public UserViewHolder(View itemV) {
            super(itemV);
            root = itemView.findViewById(R.id.userSingleLayout);
        }

        public void setname(String name) {

            TextView dname = root.findViewById(R.id.name);
            dname.setText(name);
            this.uname = name;

        }

        public void setColg(String name) {

            TextView cname = root.findViewById(R.id.college);
            cname.setText(name);
        }

        public void setcell(final String name) {
            root.findViewById(R.id.phone1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                    ab.setTitle("Calling " + uname + "...");
                    ab.setMessage("Are you sure?");
                    ab.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            Intent i = new Intent(Intent.ACTION_DIAL);
                            i.setData(Uri.parse("tel:" + name));
                            startActivity(i);
                        }
                    });

                    ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    ab.setCancelable(false);
                    AlertDialog a = ab.create();
                    a.show();


                }
            });
        }


    }
}
