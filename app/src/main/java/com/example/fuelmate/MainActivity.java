package com.example.fuelmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static com.example.fuelmate.R.id.nav_req;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView nav_view;
    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    private FirebaseUser mUser;
    private TextView nav_username;
    private TextView nav_college, nav_pref, nav_dep;
    public static String name,phone1 = null;
Button req;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Firebase
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        mDbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        mDbRef.keepSynced(true);
        mDb=FirebaseDatabase.getInstance();




        //Toolbar
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Nav VIew
        nav_view=findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        //TextView
        nav_username=(TextView)nav_view.getHeaderView(0).findViewById(R.id.nav_username);
        name = nav_username.getText().toString();
        nav_college=(TextView)nav_view.getHeaderView(0).findViewById(R.id.nav_college);
        nav_pref=(TextView)nav_view.getHeaderView(0).findViewById(R.id.nav_pref);
        nav_dep = (TextView) nav_view.getHeaderView(0).findViewById(R.id.nav_dep);


        drawer=findViewById(R.id.drawer_layout);

        //ActionBarToggle
        ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(MainActivity.this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();


        if(savedInstanceState==null) {


            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new requests()).commit();
                nav_view.setCheckedItem(R.id.nav_home);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(isInternetAvailable()==false)
                {
                    AlertDialog a=new AlertDialog.Builder(MainActivity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setMessage("Please check your internet connection and try again").setCancelable(false).create();
                    a.show();

                }
            }
        });


        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                nav_username.setText(dataSnapshot.child("name").getValue().toString());
                name = nav_username.getText().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               // req=dataSnapshot.getRef ().child ("phone").toString ();
                phone1=dataSnapshot.child("phone").getValue ().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                nav_dep.setText(dataSnapshot.child("department").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                nav_college.setText(dataSnapshot.child("college").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                nav_pref.setText(dataSnapshot.child("preferences").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(nav_pref.getText().toString().equals(getResources().getString(R.string.default_preference))||nav_pref.getText().toString().equals("null"))
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new preferences()).commit();
                    nav_view.setCheckedItem(R.id.nav_pref);
                }
            }
        },4000);


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new home()).commit();
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new search()).commit();
                break;
            case R.id.nav_pref:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new preferences()).commit();
                break;
            case R.id.nav_changePass:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new changePass()).commit();
                break;
            case R.id.nav_logOut:
                AlertDialog ad= new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to quit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                ad.setCanceledOnTouchOutside(false);
                ad.show();

                break;
        }

        drawer.closeDrawer(Gravity.START);

        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent i=new Intent(MainActivity.this,initial_activity.class);
        i.getBooleanExtra("called",true);
        startActivity(i);
    }

    public boolean isInternetAvailable() {

            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            }
            catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }

            return false;

    }
}
