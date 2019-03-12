package com.example.fuelmate;

import android.app.usage.NetworkStats;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class search extends Fragment {

    private RecyclerView mUsersList;
    private DatabaseReference mDbRef;
    public static String pref, colg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUsersList = (RecyclerView) getView().findViewById(R.id.user_view);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));


        mDbRef = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

        pref = ((TextView) getActivity().findViewById(R.id.nav_pref)).getText().toString();
        colg = ((TextView) getActivity().findViewById(R.id.nav_college)).getText().toString();


        Query qry = FirebaseDatabase.getInstance().getReference().child("Preferences/" + pref + "/" + colg);


        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(qry, new SnapshotParser<users>() {
                            @NonNull
                            @Override
                            public users parseSnapshot(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.getKey().equals(mUser.getUid())) {
                                    return new users(snapshot.child("name").getValue().toString(),
                                            snapshot.child("college").getValue().toString());
                                } else {
                                    return new users();
                                }
                            }
                        })
                        .build();

        FirebaseRecyclerAdapter<users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull users model) {


                if (model.getName() == null) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                } else {
                    holder.setname(model.getName());
                    holder.setColg(model.getColg());

                }

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.findViewById(R.id.accept).setVisibility(View.GONE);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View v1 = v.findViewById(R.id.accept);
                        View v2 = v.findViewById(R.id.information);

                        if (v1.getVisibility() == View.GONE) {

                            v1.setVisibility(View.VISIBLE);
                            v2.setVisibility(View.VISIBLE);
                        } else {
                            v1.setVisibility(View.GONE);
                            v2.setVisibility(View.GONE);
                        }


                    }
                });


            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int i) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_view, parent, false);

                return new UserViewHolder(view);
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);


        firebaseRecyclerAdapter.startListening();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        LinearLayout root;

        public UserViewHolder(View itemV) {
            super(itemV);
            root = itemView.findViewById(R.id.userSingleLayout);
        }

        public void setname(String name) {

            TextView dname = root.findViewById(R.id.name);
            dname.setText(name);
        }

        public void setColg(String name) {

            TextView cname = root.findViewById(R.id.college);
            cname.setText(name);
        }
    }
}
