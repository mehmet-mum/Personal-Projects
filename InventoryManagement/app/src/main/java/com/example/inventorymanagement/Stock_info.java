package com.example.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Stock_info extends Base_Activity {
    private static LinearLayout lin_lay,scrollview_layout;
    private static AnimationDrawable ani_draw;
    private static Boolean back_bool;
    FirebaseDatabase db;
    private int views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        scrollview_layout = (LinearLayout)findViewById(R.id.scrollview_layout);
        db = FirebaseDatabase.getInstance();

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stock information");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        views = 0;
        back_bool = true;

        filter_listener();

        getStock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        if(back_bool){
            Intent intent = new Intent(Stock_info.this,Main_Menu.class);
            startActivity(intent);
            timer_dis();
            finish();
        }

    }

    private void timer_dis(){
        back_bool = false;
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

    private void getStock(){
        DatabaseReference dbref = db.getReference(getUsername() + "/Products/");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scrollview_layout.removeAllViews();
                views = 0;
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                String barcode,name,model,num,buy,sell;
                for ( DataSnapshot key: keys ){
                    try{
                        barcode = key.getKey().toString();

                        DataSnapshot snap = key.child("Name");
                        name = snap.getValue().toString();

                        snap = key.child("Model");
                        model = snap.getValue().toString();

                        snap = key.child("Number of products");
                        num = snap.getValue().toString();

                        snap = key.child("Buy price");
                        buy = snap.getValue().toString();

                        snap = key.child("Sell price");
                        sell = snap.getValue().toString();

                        inflateProduct(barcode, name, model, num, buy, sell);
                    }
                    catch (Exception e){
                        Log.d("Exeption", e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inflateProduct(final String barcode, final String name, final String model, final String num, final String buy, final String sell){
        View view = getLayoutInflater().inflate(R.layout.stock_layout, scrollview_layout, false);
        view.setId(R.id.view_id + views);
        views++;

        EditText barcode_edit = (EditText)view.findViewById(R.id.barcode_edit);
        EditText name_edit = (EditText)view.findViewById(R.id.product_name_edit);
        EditText model_edit = (EditText)view.findViewById(R.id.model_edit);
        EditText num_edit_ = (EditText)view.findViewById(R.id.number_edit);
        EditText buy_edit = (EditText)view.findViewById(R.id.buy_edit);
        EditText sell_edit = (EditText)view.findViewById(R.id.sell_edit);

        barcode_edit.setText(barcode);
        name_edit.setText(name);
        model_edit.setText(model);
        num_edit_.setText(num);
        buy_edit.setText(buy);
        sell_edit.setText(sell);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( back_bool ){
                    timer_dis();
                    Intent intent = new Intent(Stock_info.this,Update_Product.class);

                    intent.putExtra("barcode", barcode);
                    intent.putExtra("name", name);
                    intent.putExtra("model", model);
                    intent.putExtra("num", num);
                    intent.putExtra("buy", buy);
                    intent.putExtra("sell", sell);

                    startActivity(intent);
                    finish();
                }

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                vibrate();
                final AlertDialog dialog = new AlertDialog.Builder(Stock_info.this).create();
                dialog.setMessage("Do you want to remove this product?");

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference dbref = db.getReference(getUsername() + "/Products/" + barcode);
                        dbref.setValue(null);

                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try{
                                    Log.d("Data",dataSnapshot.getValue().toString());
                                }
                                catch (Exception e){
                                    scrollview_layout.removeView(view);
                                    toast_message("The product has been removed.",Color.BLACK);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                });

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                    }
                });

                dialog.show();
                return false;
            }
        });

        scrollview_layout.addView(view);


    }

    private void filter_listener(){
        EditText barcode_text = (EditText)findViewById(R.id.barcode_text);
        barcode_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void filter(String barcode_text){
        for(int i = 0; i < views; i++){
            View view = findViewById(R.id.view_id + i);
            EditText bar_view = (EditText)view.findViewById(R.id.barcode_edit);
            EditText name_view = (EditText)view.findViewById(R.id.product_name_edit);

            if (bar_view.getText().toString().toLowerCase().contains(barcode_text) ||
                    name_view.getText().toString().toLowerCase().contains(barcode_text)){
                view.setVisibility(View.VISIBLE);
            }
            else{
                view.setVisibility(View.GONE);
            }

        }
    }
}
