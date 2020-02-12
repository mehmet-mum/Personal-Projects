package com.example.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Sale_info extends Base_Activity implements Date_Dialog.DateDialogListener {

    private LinearLayout lin_lay,scrollview_layout;
    private AnimationDrawable ani_draw;
    private Boolean back_bool;
    private EditText date_edit;
    private int views;
    private double total_sell, total_buy;
    private Button profit_button;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_info);

        db = FirebaseDatabase.getInstance();
        date_edit = (EditText)findViewById(R.id.date_edit);
        scrollview_layout = (LinearLayout)findViewById(R.id.scrollview_layout);
        profit_button = (Button)findViewById(R.id.profit_button);

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Satış Bilgisi");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        back_bool = true;
        views = 0;

        get_sales();


    }

    @Override
    public void onBackPressed() {
        if ( back_bool ){
            Intent intent = new Intent(Sale_info.this,Main_Menu.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void apply_filter(int day, int month, int year) {
        if( day == 0 && month == 0 && year == 2009 ){
            date_edit.setText("");
            filter("no_filter", day, month, year);
        }
        else if( day == 0 && month == 0 && year != 2009 ){
            date_edit.setText(Integer.toString(year));
            filter("year_filter", day, month, year);
        }
        else{
            Calendar cal=Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH,month - 1);
            String month_name = month_date.format(cal.getTime());

            if( day == 0 && month != 0 && year != 2009 ){
                date_edit.setText(month_name + "/" + year);
                filter("moon_filter", day, month, year);
            }
            else {
                date_edit.setText(String.format("%02d",day) + "/" + month_name + "/" + year);
                filter("date_filter", day, month, year);
            }
        }

    }

    private void get_sales(){
        DatabaseReference dbref = db.getReference(getUsername() + "/Sales");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for( DataSnapshot key : keys){
                    DataSnapshot snapshot = key.child("Date");
                    String date = snapshot.getValue().toString();

                    snapshot = key.child("Total Price");
                    String price = snapshot.getValue().toString();

                    snapshot = key.child("Total Buy");
                    String buy = snapshot.getValue().toString();

                    inflater(key.getKey().toString(), date, price, buy);
                }
                filter("no_filter",0,0,0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inflater(final String key, String date, String price, String buy){
        View view = getLayoutInflater().inflate(R.layout.sale_info_relative, scrollview_layout,false);
        view.setId(R.id.view_id + views);
        views++;

        final TextView sale_date = (TextView)view.findViewById(R.id.sale_date);
        final TextView sale_price = (TextView)view.findViewById(R.id.sale_price);
        final TextView sale_key = (TextView)view.findViewById(R.id.key_textview);
        TextView sale_buy = (TextView)view.findViewById(R.id.total_buy);

        sale_key.setText(key);
        sale_buy.setText(buy);
        sale_date.setText("Tarih: " + date.replace(";","/"));
        sale_price.setText("Fiyat: " + price);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( back_bool ){
                    Intent intent = new Intent(Sale_info.this, Sale_Detail.class);
                    intent.putExtra("Key", sale_key.getText().toString());
                    intent.putExtra("Date", sale_date.getText().toString());
                    intent.putExtra("Price", sale_price.getText().toString());
                    startActivity(intent);
                    timer_dis();
                    finish();
                }
            }
        });


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                vibrate();
                final AlertDialog alertDialog = new AlertDialog.Builder(Sale_info.this).create();
                alertDialog.setTitle(sale_price.getText().toString());
                alertDialog.setMessage("Satışı iptal etmek istiyor musunuz?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        products_back(key);
                        DatabaseReference dbref = db.getReference(getUsername() + "/Sales/" + key);
                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try{
                                    Log.d("Data", dataSnapshot.getValue().toString());
                                }
                                catch (Exception e){
                                    scrollview_layout.removeView(view);
                                    toast_message("Satış iptal edildi.", Color.BLACK);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                    }
                });

                alertDialog.show();

                return false;
            }
        });

        scrollview_layout.addView(view);
    }

    private void products_back(String sale_key){
        final DatabaseReference dbref = db.getReference(getUsername() + "/Sales/" + sale_key);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for(DataSnapshot key : keys){
                    String the_key = key.getKey().toString();

                    if(the_key.equals("Date") || the_key.equals("Total Buy") || the_key.equals("Total Price")){
                        // pass
                    }
                    else{
                        DataSnapshot snapshot = dataSnapshot.child(the_key + "/Number of product");
                        final int num = Integer.parseInt(snapshot.getValue().toString());
                        final DatabaseReference dbref = db.getReference(getUsername() + "/Products/" + the_key + "/Number of products");
                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dbref.setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + num);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                dbref.setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter(String condition,int day, int month, int year){
        String the_date;
        TextView date,buy,sell;
        total_buy = 0;
        total_sell = 0;
        switch (condition){
            case "year_filter":

                for ( int i = 0; i < views; i++){
                    try{
                        View view = findViewById(R.id.view_id + i);
                        date = view.findViewById(R.id.sale_date);
                        buy = view.findViewById(R.id.total_buy);
                        sell = view.findViewById(R.id.sale_price);
                        the_date = date.getText().toString();
                        if ( Integer.parseInt(the_date.substring(13,17)) == year){
                            view.setVisibility(View.VISIBLE);
                            total_buy = total_buy + Double.parseDouble(buy.getText().toString());
                            total_sell = total_sell + Double.parseDouble(sell.getText().toString().substring(7));
                        }
                        else{
                            view.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception e){

                    }
                }

                break;
            case "moon_filter":
                for ( int i = 0; i < views; i++){
                    try{
                        View view = findViewById(R.id.view_id + i);
                        date = view.findViewById(R.id.sale_date);
                        buy = view.findViewById(R.id.total_buy);
                        sell = view.findViewById(R.id.sale_price);
                        the_date = date.getText().toString();
                        if ( Integer.parseInt(the_date.substring(13,17)) == year &&
                                Integer.parseInt(the_date.substring(10,12)) == month){
                            view.setVisibility(View.VISIBLE);
                            total_buy = total_buy + Double.parseDouble(buy.getText().toString());
                            total_sell = total_sell + Double.parseDouble(sell.getText().toString().substring(7));
                        }
                        else{
                            view.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception e){

                    }
                }
                break;
            case "date_filter":
                for ( int i = 0; i < views; i++){
                    try{
                        View view = findViewById(R.id.view_id + i);
                        date = view.findViewById(R.id.sale_date);
                        buy = view.findViewById(R.id.total_buy);
                        sell = view.findViewById(R.id.sale_price);
                        the_date = date.getText().toString();
                        if ( Integer.parseInt(the_date.substring(13,17)) == year &&
                                Integer.parseInt(the_date.substring(10,12)) == month &&
                                Integer.parseInt(the_date.substring(7,9)) == day){
                            view.setVisibility(View.VISIBLE);
                            total_buy = total_buy + Double.parseDouble(buy.getText().toString());
                            total_sell = total_sell + Double.parseDouble(sell.getText().toString().substring(7));
                        }
                        else{
                            view.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception e){

                    }
                }
                break;
            default:
                for(int i = 0; i < views; i++){
                    try{
                        View view = findViewById(R.id.view_id + i);
                        buy = view.findViewById(R.id.total_buy);
                        sell = view.findViewById(R.id.sale_price);
                        view.setVisibility(View.VISIBLE);
                        total_buy = total_buy + Double.parseDouble(buy.getText().toString());
                        total_sell = total_sell + Double.parseDouble(sell.getText().toString().substring(7));
                    }
                    catch (Exception e){

                    }
                }
                break;
        }
        profit_button.setText("Kâr: " + String.format("%.2f", total_sell - total_buy).replace(",","."));

    }

    @Override
    public void wrong_date() {
        toast_message("Invalid date",Color.RED);
    }

    public void search_click(View view) {
        Date_Dialog date_dialog = new Date_Dialog();
        date_dialog.show(getSupportFragmentManager(),null);
    }

    public void profit_click(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(Sale_info.this).create();

        dialog.setTitle("Profit: " + String.format("%.2f", total_sell - total_buy).replace(",","."));
        dialog.setMessage("Total buying price: " + String.format("%.2f", total_buy).replace(",",".")
                + "\nTotal selling price: " + String.format("%.2f", total_sell).replace(",","."));

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
            }
        });
        dialog.show();
    }
}
