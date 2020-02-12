package com.example.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Timer;
import java.util.TimerTask;


public class Add_Product extends Base_Activity {
    private static EditText barcode_text,product_name,model_name,num_of_prod,buy_price,sell_price;
    private ImageButton imageButton;
    private static Boolean bool,back_bool;
    private static AnimationDrawable ani_draw;
    private static LinearLayout lin_lay;
    private static ImageButton scan_button;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__product);

        lin_lay = (LinearLayout)findViewById(R.id.linlay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Product");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db = FirebaseDatabase.getInstance();
        barcode_text = (EditText)findViewById(R.id.barcode_text);
        product_name = (EditText)findViewById(R.id.product_name);
        model_name = (EditText)findViewById(R.id.model_name);
        num_of_prod = (EditText)findViewById(R.id.num_of_prod);
        buy_price = (EditText)findViewById(R.id.buy_price);
        sell_price = (EditText)findViewById(R.id.sell_price);
        imageButton = (ImageButton)findViewById(R.id.exclamation_button);
        scan_button = (ImageButton)findViewById(R.id.scan_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(Add_Product.this,"Selling price should be higher than buying price."
                        ,Toast.LENGTH_LONG);
                View view1 = toast.getView();
                view1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                TextView textView = (TextView)view1.findViewById(android.R.id.message);
                textView.setTextColor(Color.WHITE);

                toast.show();
            }
        });

        barcodeTextListener();

        back_bool = true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        if (back_bool){
            Intent intent = new Intent(Add_Product.this,Main_Menu.class);
            startActivity(intent);
            back_bool = false;
            timer_dis();
            finish();

        }
    }

    public void scan_click(View view) {
        final Activity activity = Add_Product.this;

        IntentIntegrator integrator =  new IntentIntegrator(activity);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setCameraId(0);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        scan_button.setClickable(false);
        timer_dis();
        integrator.initiateScan();
    }

    private void timer_dis(){
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        scan_button.setClickable(true);
                        back_bool = true;
                    }
                });
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {

            if (intentResult.getContents() == null)
                Toast.makeText(Add_Product.this,"Invalid barcode!",Toast.LENGTH_SHORT).show();
            else {
                barcode_text.setText(intentResult.getContents());
                checkExistence(intentResult.getContents());            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void barcodeTextListener(){
        barcode_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( charSequence.toString().equals("") ){
                    product_name.setText("");
                    model_name.setText("");
                    num_of_prod.setText("");
                    buy_price.setText("");
                    sell_price.setText("");

                    barcode_text.setHintTextColor(Color.WHITE);
                    product_name.setHintTextColor(Color.WHITE);
                    model_name.setHintTextColor(Color.WHITE);
                    buy_price.setHintTextColor(Color.WHITE);
                    buy_price.setHintTextColor(Color.WHITE);
                    sell_price.setHintTextColor(Color.WHITE);
                }
                else {

                    clearEdittext();
                    checkExistence(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void clearEdittext(){
        bool = true;

        product_name.setEnabled(true);
        product_name.setText("");
        product_name.setHintTextColor(Color.WHITE);

        model_name.setEnabled(true);
        model_name.setText("");
        model_name.setHintTextColor(Color.WHITE);


        num_of_prod.setText("");
        num_of_prod.setHintTextColor(Color.WHITE);

        buy_price.setEnabled(true);
        buy_price.setText("");
        buy_price.setHintTextColor(Color.WHITE);

        sell_price.setEnabled(true);
        sell_price.setText("");
        sell_price.setHintTextColor(Color.WHITE);

    }
    private void checkExistence(final String barcode){
        DatabaseReference dbref = db.getReference(getUsername() + "/Products");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for (DataSnapshot key: keys){
                    if (key.getKey().equals(barcode)){
                        updateEdittexts(barcode);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateEdittexts(String barcode){
        DatabaseReference db_upd = db.getReference(getUsername() + "/Products/" + barcode);
        db_upd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bool = false;
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for(DataSnapshot key: keys){
                    if(key.getKey().toString().equals("Name")){
                        product_name.setText(key.getValue().toString());
                        product_name.setEnabled(false);
                    }
                    else if(key.getKey().toString().equals("Model")){
                        model_name.setText(key.getValue().toString());
                        model_name.setEnabled(false);
                    }
                    else if(key.getKey().toString().equals("Buy price")){
                        buy_price.setText(key.getValue().toString());
                        buy_price.setEnabled(false);
                    }
                    else if(key.getKey().toString().equals("Sell price")){
                        sell_price.setText(key.getValue().toString());
                        sell_price.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void add_product(View view) {
        Boolean empty = checkEmpty();

        if (empty == false){
            addProduct();
        }

    }
    private boolean checkEmpty(){
        boolean empty = false;
        if (barcode_text.getText().toString().equals("")){
            barcode_text.setHintTextColor(Color.RED);
            empty = true;
        }

        if (product_name.getText().toString().equals("")){
            product_name.setHintTextColor(Color.RED);
            empty = true;
        }

        if (model_name.getText().toString().equals("")){
            model_name.setHintTextColor(Color.RED);
            empty = true;
        }

        if (num_of_prod.getText().toString().equals("") || Integer.parseInt(num_of_prod.getText().toString()) == 0){
            num_of_prod.setText("");
            num_of_prod.setHintTextColor(Color.RED);
            empty = true;
        }

        if (buy_price.getText().toString().equals("") || Double.parseDouble(buy_price.getText().toString()) == 0){
            buy_price.setText("");
            buy_price.setHintTextColor(Color.RED);
            empty = true;
        }

        if (sell_price.getText().toString().equals("") || Double.parseDouble(sell_price.getText().toString()) == 0){
            sell_price.setText("");
            sell_price.setHintTextColor(Color.RED);
            empty = true;
        }

        if (sell_price.getText().toString().equals("") == false && buy_price.getText().toString().equals("") == false &&
                Double.parseDouble(buy_price.getText().toString()) > Double.parseDouble(sell_price.getText().toString())){
            imageButton.setVisibility(View.VISIBLE);
            empty = true;
        }
        else{
            imageButton.setVisibility(View.GONE);
        }


        return empty;
    }

    private void addProduct(){
        String username = getUsername();
        final String barcode = barcode_text.getText().toString();
        if(bool){

            DatabaseReference dbref = db.getReference(username + "/Products/" +
                    barcode + "/Name");
            dbref.setValue(product_name.getText().toString());


            dbref = db.getReference(username + "/Products/" +
                    barcode + "/Model");
            dbref.setValue(model_name.getText().toString());


            dbref = db.getReference(username + "/Products/" +
                    barcode + "/Number of products");
            dbref.setValue(num_of_prod.getText().toString());


            dbref = db.getReference(username + "/Products/" +
                    barcode + "/Buy price");
            dbref.setValue(String.format("%.2f", Double.parseDouble(buy_price.getText().toString())).replace(",","."));


            dbref = db.getReference(username + "/Products/" +
                    barcode + "/Sell price");
            dbref.setValue(String.format("%.2f", Double.parseDouble(sell_price.getText().toString())).replace(",","."));

            checkAddProduct(barcode);

        }
        else{
            final DatabaseReference dbref = db.getReference(username + "/Products/" +
                    barcode + "/Number of products");

            final int number = Integer.parseInt(num_of_prod.getText().toString());

            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try{


                        dbref.setValue(Integer.parseInt(dataSnapshot.getValue().toString())
                                + number);
                        checkAddProduct(barcode);

                    }
                    catch (Exception e){
                        toast_message("Number of available product is " +
                                (999999999 - Integer.parseInt(dataSnapshot.getValue().toString())), Color.RED);

                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if ( getUsername().equals("") ){
            setUsername(username);
        }

        barcode_text.setText("");

    }

    private void checkAddProduct(String barcode){
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


                    toast_message("Successful!", Color.GREEN);

                }
                catch (Exception e){
                    dbref.setValue(null);

                    toast_message("Unsuccessful!", Color.RED);
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
