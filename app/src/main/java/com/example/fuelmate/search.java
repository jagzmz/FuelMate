package com.example.fuelmate;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

public class search extends Fragment {

    private RecyclerView mUsersList;
    private DatabaseReference mDbRef;
    public static String pref, colg, dep;
    private SharedPreferences se1;
    private Query qry;
    private ProgressBar pb;
    private FirebaseRecyclerAdapter<users, UserViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG, "parseSna: ");


        Log.d(TAG, "parseSna: end");


        //dep = ((TextView) getActivity().findViewById(R.id.nav_dep)).getText().toString();
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pb = getView().findViewById(R.id.sload);

        pb.setVisibility(View.VISIBLE);

        se1 = getActivity().getSharedPreferences("localdata", Context.MODE_PRIVATE);
        final String locality = se1.getString("locality", "null");
        pref = se1.getString("preferences", "null");
        mUsersList = (RecyclerView) getView().findViewById(R.id.user_view);
//        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));


        mDbRef = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());


        colg = ((TextView) getActivity().findViewById(R.id.nav_college)).getText().toString();

        FirebaseDatabase.getInstance().getReference().child("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/department").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dep = dataSnapshot.getValue().toString();
                Log.d(TAG, "parseSna data : ");

                if (!pref.equals("Department")) {
                    qry = FirebaseDatabase.getInstance().getReference().child("Preferences/" + pref + "/" + colg + "/" + locality);
                } else {
                    qry = FirebaseDatabase.getInstance().getReference().child("Preferences/" + pref + "/" + colg + "/" + dep + "/" + locality);


                }

//                Toast.makeText(getContext(), qry.toString(), Toast.LENGTH_LONG).show();

                Log.d(TAG, "parseSna: " + qry.toString());
                FirebaseRecyclerOptions<users> options = null;

                options = new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(qry, new SnapshotParser<users>() {
                            @NonNull
                            @Override
                            public users parseSnapshot(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.getKey().equals(mUser.getUid())) {
                                    Log.d("search", "parseSnapshot: loaded");
                                    return new users(snapshot.getKey(), snapshot.child("name").getValue().toString(),
                                            snapshot.child("college").getValue().toString(), snapshot.child("phone").getValue().toString());
                                } else {
                                    return new users();
                                }
                            }
                        })
                        .build();

                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, UserViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull final users model) {


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

                        mDbRef = FirebaseDatabase.getInstance().getReference();

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View v1 = v.findViewById(R.id.accept);
                                View v2 = v.findViewById(R.id.information);


                                v1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final HashMap<String, String> d = new HashMap<>();

                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                                        d.put("name", ((TextView) getActivity().findViewById(R.id.nav_username)).getText().toString());
                                        d.put("college", ((TextView) getActivity().findViewById(R.id.nav_college)).getText().toString());
                                        d.put("phone", model.getCell());

                                        mDbRef.child("Friend-Req").child(model.getName()).child(mAuth.getUid()).setValue(d);

                                    }
                                });


                                if (v1.getVisibility() == View.GONE) {

                                    v1.setVisibility(View.VISIBLE);
                                    v2.setVisibility(View.VISIBLE);
                                } else {
                                    v1.setVisibility(View.GONE);
                                    v2.setVisibility(View.GONE);
                                }

                                v2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showinfo si = new showinfo();
                                        Bundle b = new Bundle();
                                        b.putString("uid", model.getUid());
                                        si.setArguments(b);

                                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, si).commit();
                                    }
                                });
                            }


                        });


                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(ViewGroup parent, int i) {


                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.single_user_view, parent, false);

                        pb.setVisibility(View.GONE);


                        return new UserViewHolder(view);
                    }
                };

                firebaseRecyclerAdapter.notifyDataSetChanged();

                Log.d(TAG, "parseSna: 1");
                mUsersList.setAdapter(firebaseRecyclerAdapter);
                Log.d(TAG, "parseSna: 2");
                firebaseRecyclerAdapter.startListening();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
