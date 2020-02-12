package com.example.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Update_Product extends Base_Activity {
    private static LinearLayout lin_lay;
    private static AnimationDrawable ani_draw;
    private static EditText barcode_edit, name_edit, model_edit, num_edit, buy_edit, sell_edit;
    private static ImageButton imageButton;
    private static Boolean back_bool;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__product);

        db = FirebaseDatabase.getInstance();

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ürün Bilgisi");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        barcode_edit = (EditText)findViewById(R.id.barcode_edit);
        name_edit = (EditText)findViewById(R.id.product_name_edit);
        model_edit = (EditText)findViewById(R.id.model_edit);
        num_edit = (EditText)findViewById(R.id.number_edit);
        buy_edit = (EditText)findViewById(R.id.buy_edit);
        sell_edit = (EditText)findViewById(R.id.sell_edit);

        imageButton = (ImageButton)findViewById(R.id.exclamation_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(Update_Product.this,"Selling price should be higher than buying price."
                        ,Toast.LENGTH_LONG);
                View view1 = toast.getView();
                view1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                TextView textView = (TextView)view1.findViewById(android.R.id.message);
                textView.setTextColor(Color.WHITE);



                toast.show();
            }
        });
        back_bool = true;
        getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        if(back_bool){
            back_bool = false;
            Intent intent = new Intent(Update_Product.this, Stock_info.class);
            startActivity(intent);
            timer_dis();
            finish();

        }
    }

    private void timer_dis(){
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        back_bool = true;
                    }
                });
            }
        }, 2000);
    }

    private void getExtras(){
        Bundle bundle = getIntent().getExtras();

        barcode_edit.setText(bundle.getString("barcode"));
        name_edit.setText(bundle.getString("name"));
        model_edit.setText(bundle.getString("model"));
        num_edit.setText(bundle.getString("num"));
        buy_edit.setText(bundle.getString("buy"));
        sell_edit.setText(bundle.getString("sell"));
    }

    public void update_product(View view) {
        Boolean empty = checkEmpty();

        if (empty == false){
            updateProduct();
        }
    }

    private boolean checkEmpty(){
        boolean empty = false;

        if (name_edit.getText().toString().equals("")){
            name_edit.setHint("Product name;");
            name_edit.setHintTextColor(Color.RED);
            empty = true;
        }

        if (model_edit.getText().toString().equals("")){
            model_edit.setHint("Model");
            model_edit.setHintTextColor(Color.RED);
            empty = true;
        }

        if (num_edit.getText().toString().equals("") || Integer.parseInt(num_edit.getText().toString()) == 0){
            num_edit.setText("");
            num_edit.setHint("Number");
            num_edit.setHintTextColor(Color.RED);
            empty = true;
        }

        if (buy_edit.getText().toString().equals("") || Double.parseDouble(buy_edit.getText().toString()) == 0){
            buy_edit.setText("");
            buy_edit.setHint("Buying Price");
            buy_edit.setHintTextColor(Color.RED);
            empty = true;
        }

        if (sell_edit.getText().toString().equals("") || Double.parseDouble(sell_edit.getText().toString()) == 0){
            sell_edit.setText("");
            sell_edit.setHint("Selling Price");
            sell_edit.setHintTextColor(Color.RED);
            empty = true;
        }

        if (sell_edit.getText().toString().equals("") == false && sell_edit.getText().toString().equals("") == false &&
                Double.parseDouble(buy_edit.getText().toString()) > Double.parseDouble(sell_edit.getText().toString())){
            imageButton.setVisibility(View.VISIBLE);
            empty = true;
        }
        else{
            imageButton.setVisibility(View.GONE);
        }


        return empty;
    }

    private void updateProduct(){
        DatabaseReference dbref = db.getReference(getUsername() + "/Products/" +
                barcode_edit.getText().toString() + "/Name");
        dbref.setValue(name_edit.getText().toString());


        dbref = db.getReference(getUsername() + "/Products/" +
                barcode_edit.getText().toString() + "/Model");
        dbref.setValue(model_edit.getText().toString());


        dbref = db.getReference(getUsername() + "/Products/" +
                barcode_edit.getText().toString() + "/Number of products");
        dbref.setValue(num_edit.getText().toString());


        dbref = db.getReference(getUsername() + "/Products/" +
                barcode_edit.getText().toString() + "/Buy price");
        dbref.setValue(String.format("%.2f", Double.parseDouble(buy_edit.getText().toString())).replace(",","."));


        dbref = db.getReference(getUsername() + "/Products/" +
                barcode_edit.getText().toString() + "/Sell price");
        dbref.setValue(String.format("%.2f", Double.parseDouble(sell_edit.getText().toString())).replace(",","."));

        checkUpdateProduct(barcode_edit.getText().toString());
    }

    private void checkUpdateProduct(String barcode){
        final DatabaseReference dbref = db.getReference(getUsername() + "/Products/" + barcode );

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String s;
                    DataSnapshot snapshot = dataSnapshot.child("Name");
                    s = snapshot.getValue().toString();

                    snapshot = dataSnapshot.child("Model");
                    s = snapshot.getValue().toString();

                    snapshot = dataSnapshot.child("Number of products");
                    s = snapshot.getValue().toString();

                    snapshot = dataSnapshot.child("Buy price");
                    s = snapshot.getValue().toString();

                    snapshot = dataSnapshot.child("Sell price");
                    s = snapshot.getValue().toString();


                    toast_message("Product has been updated.", Color.GREEN);

                }
                catch (Exception e){
                    dbref.setValue(null);

                    toast_message("Try again.", Color.RED);
                    updateProduct();
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
