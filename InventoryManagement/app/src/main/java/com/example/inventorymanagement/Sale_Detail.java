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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Sale_Detail extends Base_Activity {
    private LinearLayout linlay,scrollview_layout;
    private AnimationDrawable ani_draw;
    private Button price_button;
    private String key,date,get_price;
    private Boolean back_bool,ready_sale;
    private double price;
    private int views;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale__detail);

        db = FirebaseDatabase.getInstance();

        linlay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)linlay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        scrollview_layout = (LinearLayout)findViewById(R.id.scrollview_layout);
        price_button = (Button)findViewById(R.id.price_button);

        getExtras();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(date);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        back_bool = true;
        ready_sale = true;
        price = 0;
        views = 0;

        getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        if ( back_bool ){
            Intent intent = new Intent(Sale_Detail.this, Sale_info.class);
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

    private void getExtras(){
        Bundle bundle = getIntent().getExtras();

        key = bundle.getString("Key");
        date = bundle.getString("Date");
        get_price = bundle.getString("Price");
    }

    private void getData(){
        DatabaseReference dbref = db.getReference(getUsername() + "/Sales/" + key);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for( DataSnapshot key: keys){
                    if(key.getKey().toString().equals("Date")){
                        // empty
                    }
                    else if (key.getKey().toString().equals("Total Price")){
                        price = Double.parseDouble(key.getValue().toString());
                    }
                    else if (key.getKey().toString().equals("Total Buy")){
                        //empty
                    }
                    else{
                        DataSnapshot snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Name");
                        String name = snapshot.getValue().toString();

                        snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Model");
                        String model = snapshot.getValue().toString();

                        snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Sell Price");
                        String sell_price = snapshot.getValue().toString();

                        snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Buy Price");
                        String buy_price = snapshot.getValue().toString();

                        snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Number of product");
                        String num = snapshot.getValue().toString();

                        snapshot = dataSnapshot.child(key.getKey().toString() + "/" + "Total Price");
                        String tot_sell = snapshot.getValue().toString();


                        addInflater(key.getKey().toString(),name,model,num,buy_price,sell_price,tot_sell);

                    }
                }
                price_button.setText(get_price);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addInflater(String barcode, String name, String model, final String total_num, String buy, String sell, String tot_sell){
        View view = getLayoutInflater().inflate(R.layout.product_relative, scrollview_layout,false);
        view.setId(R.id.view_id + views);
        views++;


        EditText barcode_edit = (EditText)view.findViewById(R.id.barcode_edit);
        EditText name_edit = (EditText)view.findViewById(R.id.product_name_edit);
        EditText model_edit = (EditText)view.findViewById(R.id.model_edit);
        final EditText number_of_products_edit = (EditText)view.findViewById(R.id.number_edit);
        final EditText price_edit = (EditText)view.findViewById(R.id.price_edit);
        final EditText buy_edit = (EditText)view.findViewById(R.id.buy_edit);
        final EditText sell_edit = (EditText)view.findViewById(R.id.sell_edit);

        final LinearLayout gone_linlay1 = (LinearLayout)view.findViewById(R.id.gone_linlay);
        final LinearLayout gone_linlay2 = (LinearLayout)view.findViewById(R.id.gone_linlay2);

        TextView sale_detail_num = (TextView)view.findViewById(R.id.sale_detail_num);

        final ImageButton down_button = (ImageButton)view.findViewById(R.id.down_button);
        final ImageButton up_button = (ImageButton)view.findViewById(R.id.up_button);
        final ImageButton exclamation_button1 = (ImageButton)view.findViewById(R.id.exclamation_button);
        final ImageButton exclamation_button2 = (ImageButton)view.findViewById(R.id.exclamation_button2);



        barcode_edit.setText(barcode);
        name_edit.setText(name);
        model_edit.setText(model);
        number_of_products_edit.setText(total_num);
        price_edit.setText(tot_sell);
        buy_edit.setText(buy);
        sell_edit.setText(sell);
        sale_detail_num.setText(total_num);




        view.setLongClickable(true);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                vibrate();
                final AlertDialog alertDialog = new AlertDialog.Builder(Sale_Detail.this).create();
                final double current_price = Double.parseDouble(price_edit.getText().toString());
                alertDialog.setTitle("Fiyat \n" + current_price);
                alertDialog.setMessage("Ürünü çıkarmak istiyor musunuz?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        view.setVisibility(View.GONE);
                        toast_message("Ürün çıkarıldı.", Color.BLACK);
                        if(current_price != 0){
                            price = price - current_price;
                            String s = String.format("%.2f", price);
                            s = s.replace(",",".");
                            price = Double.parseDouble(s);
                            price_button.setText("Fiyat: " + s);
                        }
                    }
                });

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                    }
                });

                alertDialog.show();


                return true;
            }
        });

        down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gone_linlay1.setVisibility(View.VISIBLE);
                gone_linlay2.setVisibility(View.VISIBLE);
                up_button.setVisibility(View.VISIBLE);
                down_button.setVisibility(View.GONE);
            }
        });

        up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gone_linlay1.setVisibility(View.GONE);
                gone_linlay2.setVisibility(View.GONE);
                up_button.setVisibility(View.GONE);
                down_button.setVisibility(View.VISIBLE);
            }
        });

        exclamation_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Satış fiyatı alış fiyatından düşük olamaz.";
                toast_message(message,Color.RED);
            }
        });

        exclamation_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Ürün miktarını sadece azaltabilirsiniz. ( " + total_num + " )";
                toast_message(message,Color.RED);
            }
        });
        number_of_products_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( charSequence.toString().equals("") == false && sell_edit.getText().toString().equals("") == false ){
                    int num = Integer.parseInt(charSequence.toString());
                    double selling = Double.parseDouble(sell_edit.getText().toString());
                    if( num != 0 && selling != 0 ){
                        String s = String.format("%.2f", num*selling);
                        s = s.replace(",",".");
                        price_edit.setText(s);

                        price = price - num * selling;
                        s = String.format("%.2f", price);
                        s = s.replace(",",".");
                        price = Double.parseDouble(s);
                        price_button.setText("Fiyat: " + s);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                number_of_products_edit.setTextColor(Color.WHITE);
                price_edit.setTextColor(Color.WHITE);
                ready_sale = true;
                if(charSequence.toString().equals("") == false && sell_edit.getText().toString().equals("") == false){
                    int num = Integer.parseInt(charSequence.toString());
                    double selling = Double.parseDouble(sell_edit.getText().toString());
                    if( num == 0 || selling == 0) {
                        price_edit.setText("0.00");
                        price_edit.setTextColor(Color.RED);
                        ready_sale = false;
                        if( num == 0)
                            number_of_products_edit.setTextColor(Color.RED);

                    }
                    else{
                        if ( num > Integer.parseInt(total_num) ){
                            number_of_products_edit.setTextColor(Color.RED);
                            price_edit.setTextColor(Color.RED);
                            ready_sale = false;
                            exclamation_button1.setVisibility(View.VISIBLE);
                        }
                        else{
                            exclamation_button1.setVisibility(View.GONE);
                        }


                        String s = String.format("%.2f", num*selling);
                        s = s.replace(",",".");
                        price_edit.setText(s);

                        price = price + num * selling;
                        s = String.format("%.2f", price);
                        s = s.replace(",",".");
                        price = Double.parseDouble(s);
                        price_button.setText("Fiyat: " + s);
                    }
                }
                else if( charSequence.toString().equals("") == false ){
                    int num = Integer.parseInt(charSequence.toString());
                    if ( num > Integer.parseInt(total_num) ){
                        number_of_products_edit.setTextColor(Color.RED);
                        price_edit.setTextColor(Color.RED);
                        ready_sale = false;
                        exclamation_button1.setVisibility(View.VISIBLE);
                    }
                    else{
                        exclamation_button1.setVisibility(View.GONE);
                    }
                    price_edit.setText("0.00");
                    price_edit.setTextColor(Color.RED);
                    ready_sale = false;
                }
                else{
                    exclamation_button1.setVisibility(View.GONE);
                    price_edit.setText("0.00");
                    price_edit.setTextColor(Color.RED);
                    ready_sale = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sell_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( charSequence.toString().equals("") == false && number_of_products_edit.getText().toString().equals("") == false ){
                    int num = Integer.parseInt(number_of_products_edit.getText().toString());
                    double selling = Double.parseDouble(charSequence.toString());
                    if( num != 0 && selling != 0 ){
                        price = price - num * selling;
                        String s = String.format("%.2f", price);
                        s = s.replace(",",".");
                        price = Double.parseDouble(s);
                        price_button.setText("Fiyat: " + s);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sell_edit.setTextColor(Color.WHITE);
                price_edit.setTextColor(Color.WHITE);
                ready_sale = true;
                if(charSequence.toString().equals("") == false && number_of_products_edit.getText().toString().equals("") == false){
                    int num = Integer.parseInt(number_of_products_edit.getText().toString());
                    double selling = Double.parseDouble(charSequence.toString());
                    if( num == 0 || selling == 0) {
                        price_edit.setText("0.00");
                        price_edit.setTextColor(Color.RED);
                        ready_sale = false;
                        if( selling == 0 )
                            sell_edit.setTextColor(Color.RED);

                    }
                    else{
                        if(Double.parseDouble(buy_edit.getText().toString()) > Double.parseDouble(sell_edit.getText().toString())){
                            price_edit.setTextColor(Color.RED);
                            ready_sale = false;
                            sell_edit.setTextColor(Color.RED);
                            exclamation_button2.setVisibility(View.VISIBLE);
                        }
                        else{
                            exclamation_button2.setVisibility(View.GONE);
                        }

                        String s = String.format("%.2f", num*selling);
                        s = s.replace(",",".");
                        price_edit.setText(s);

                        price = price + num * selling;
                        s = String.format("%.2f", price);
                        s = s.replace(",",".");
                        price = Double.parseDouble(s);
                        price_button.setText("Fiyat: " + s);
                    }
                }
                else if( charSequence.toString().equals("") == false ){
                    if(Double.parseDouble(buy_edit.getText().toString()) > Double.parseDouble(sell_edit.getText().toString())){
                        price_edit.setTextColor(Color.RED);
                        ready_sale = false;
                        sell_edit.setTextColor(Color.RED);
                        exclamation_button2.setVisibility(View.VISIBLE);
                    }
                    else{
                        exclamation_button2.setVisibility(View.GONE);
                    }

                    price_edit.setText("0.00");
                    price_edit.setTextColor(Color.RED);
                    ready_sale = false;
                }
                else{
                    exclamation_button2.setVisibility(View.GONE);
                    price_edit.setText("0.00");
                    price_edit.setTextColor(Color.RED);
                    ready_sale = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        scrollview_layout.addView(view);
    }

    public void price_click(View view) {
        if (price != 0 && ready_sale){
            timer_dis();
            final AlertDialog dialog = new AlertDialog.Builder(Sale_Detail.this).create();

            dialog.setTitle(price_button.getText().toString());
            dialog.setMessage("Satışı güncellemek istiyor musunuz?");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sales();
                }
            });

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                }
            });

            dialog.show();
        }
        else{
            String message = "Tamamlanmamış ürünler var.";
            toast_message(message,Color.RED);
        }
    }
    private void sales(){
        double total_buy_price = 0;
        View view;

        DatabaseReference dbref;
        EditText barcode, name, model, num, buy,sell,total_price;
        TextView sale_detail_num;
        Date currentTime = Calendar.getInstance().getTime();

        String strDateFormat = "dd/MM/yyyy HH:mm:ss";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        String formattedDate= dateFormat.format(currentTime);


        String barcode_text;


        dbref = db.getReference(getUsername() + "/Sales/" + key + "/Date");
        dbref.setValue(formattedDate);

        dbref = db.getReference(getUsername() + "/Sales/" + key + "/Total Price");
        dbref.setValue(String.format("%.2f",price).replace(",","."));

        for (int i = 0; i < views; i++){
            view = findViewById(R.id.view_id + i);

            if ( view.getVisibility() == View.GONE ){
                barcode = (EditText)view.findViewById(R.id.barcode_edit);
                sale_detail_num = (TextView)view.findViewById(R.id.sale_detail_num);
                barcode_text = barcode.getText().toString();

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text);
                dbref.setValue(null);

                dbref = db.getReference(getUsername() + "/Products/" + barcode_text + "/Number of products");
                final String finalBarcode_text = barcode_text;
                final int num_product = Integer.parseInt(sale_detail_num.getText().toString());
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int number = Integer.parseInt(dataSnapshot.getValue().toString());
                        number = number + num_product;

                        DatabaseReference dbref_new = db.getReference(getUsername() + "/Products/" +
                                finalBarcode_text + "/Number of products");

                        dbref_new.setValue(number);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if( view != null ){
                barcode = (EditText)view.findViewById(R.id.barcode_edit);
                name = (EditText)view.findViewById(R.id.product_name_edit);
                model = (EditText)view.findViewById(R.id.model_edit);
                num = (EditText)view.findViewById(R.id.number_edit);
                buy = (EditText)view.findViewById(R.id.buy_edit);
                sell = (EditText)view.findViewById(R.id.sell_edit);
                total_price = (EditText)view.findViewById(R.id.price_edit);
                sale_detail_num = (TextView)view.findViewById(R.id.sale_detail_num);

                barcode_text = barcode.getText().toString();

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Name");
                dbref.setValue(name.getText().toString());

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Model");
                dbref.setValue(model.getText().toString());

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Number of product");
                dbref.setValue(Integer.parseInt(num.getText().toString()));

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Buy Price");
                dbref.setValue(buy.getText().toString());

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Sell Price");
                dbref.setValue(String.format("%.2f", Double.parseDouble(sell.getText().toString()))
                        .replace(",","."));

                dbref = db.getReference(getUsername() + "/Sales/" + key + "/" + barcode_text + "/Total Price");
                dbref.setValue(total_price.getText().toString());

                total_buy_price = total_buy_price + Integer.parseInt(num.getText().toString()) * Double.parseDouble(buy.getText().toString());

                if(Integer.parseInt(sale_detail_num.getText().toString()) != Integer.parseInt(num.getText().toString())){

                    dbref = db.getReference(getUsername() + "/Products/" + barcode_text + "/Number of products");
                    final String finalBarcode_text = barcode_text;
                    final int num_product = Integer.parseInt(sale_detail_num.getText().toString()) - Integer.parseInt(num.getText().toString());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int number = Integer.parseInt(dataSnapshot.getValue().toString());
                            number = number + num_product;

                            DatabaseReference dbref_new = db.getReference(getUsername() + "/Products/" +
                                    finalBarcode_text + "/Number of products");

                            dbref_new.setValue(number);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            dbref = db.getReference(getUsername() + "/Sales/" + key + "/Total Buy");
            dbref.setValue(String.format("%.2f",total_buy_price).replace(",","."));



        }
    }
}
