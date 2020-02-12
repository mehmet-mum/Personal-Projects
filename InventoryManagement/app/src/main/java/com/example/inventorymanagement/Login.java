package com.example.inventorymanagement;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends Base_Activity {
    static private LinearLayout lin_lay;
    static private AnimationDrawable ani_draw;
    static private EditText usr_text,pswd_text;
    static private TextView err_text;
    static private Button login,signup;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usr_text = (EditText)findViewById(R.id.user_text);
        pswd_text = (EditText)findViewById(R.id.password_text);
        err_text = (TextView)findViewById(R.id.error_text);
        db = FirebaseDatabase.getInstance();

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable) lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        login = (Button)findViewById(R.id.login_button);
        signup = (Button)findViewById(R.id.signup_button);



    }
    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }
    public void login_click(View view) {
        DatabaseReference dbref = db.getReference("Users");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = usr_text.getText().toString();
                String password = pswd_text.getText().toString();
                if ( dataSnapshot.hasChild(username)){
                    Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                    for (DataSnapshot key: keys){
                        if (key.getKey().toString().equals(username)){
                            if (key.getValue().toString().equals(getSHA(password))){
                                Intent intent = new Intent(Login.this,Main_Menu.class);
                                setUsername(username);
                                set_vibrate();
                                startActivity(intent);

                                login.setClickable(false);
                                signup.setClickable(false);

                                Timer buttonTimer = new Timer();
                                buttonTimer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                login.setClickable(true);
                                                signup.setClickable(true);
                                            }
                                        });
                                    }
                                }, 2000);

                                finish();
                            }
                            else{
                                err_text.setText("Invalid username or password!");
                                usr_text.setText("");
                                pswd_text.setText("");
                            }
                        }
                    }
                }
                else {
                    err_text.setText("Invalid username or password!");
                    usr_text.setText("");
                    pswd_text.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void signup_click(View view) {
        String username = usr_text.getText().toString();
        String password = pswd_text.getText().toString();

        if( username.length() < 6 ){
            err_text.setText("Short username!");
            usr_text.setText("");
            pswd_text.setText("");
        }
        else if ( password.length() < 6 ){
            err_text.setText("Short password!");
            usr_text.setText("");
            pswd_text.setText("");
        }
        else{
            check_availabity(username,password);
        }
    }

    public void check_availabity(final String username,final String password){
        DatabaseReference dbref = db.getReference("Users");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean bool = true;
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key: keys){
                    if (key.getKey().toString().equalsIgnoreCase(username)){
                        bool = false;
                    }
                }

                if ( bool == true){
                    err_text.setText("");
                    DatabaseReference dbref = db.getReference("Users/" + username);
                    dbref.setValue(getSHA(password));
                    usr_text.setText("");
                    pswd_text.setText("");
                }
                else{
                    err_text.setText("Invalid username!");
                    usr_text.setText("");
                    pswd_text.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // get hash code of a text ( SHA-256 )
    public static String getSHA(String input)
    {

        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }
}