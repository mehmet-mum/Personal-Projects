package com.example.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Sell_Product extends Base_Activity implements Search_Product.SearchProductListener {
    private LinearLayout scrollview_layout,lin_lay;
    private Button price_button,clear_button;
    private ImageButton scan_button,search_button;
    private double price;
    private int views;
    private boolean serial_scan,adapter_empty,ready_sale,back_bool;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private static AnimationDrawable ani_draw;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell__product);

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Satış Yap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        db = FirebaseDatabase.getInstance();
        price_button = (Button)findViewById(R.id.price_button);
        clear_button = (Button)findViewById(R.id.clear_button);
        search_button = (ImageButton)findViewById(R.id.search_button);
        scan_button = (ImageButton)findViewById(R.id.scan_button);
        scrollview_layout = (LinearLayout)findViewById(R.id.scrollview_layout);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serial_scan = false;
                scan_barcode();
            }
        });
        scan_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate();
                serial_scan = true;
                scan_barcode();
                return false;
            }
        });
        price = 0;
        views = 0;
        serial_scan = false;
        ready_sale = true;
        back_bool = true;
        sell();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        if(back_bool){
            Intent intent = new Intent(Sell_Product.this,Main_Menu.class);
            startActivity(intent);
            timer_dis();
            finish();
        }

    }

    private void timer_dis(){
        back_bool = false;
        scan_button.setClickable(false);
        search_button.setClickable(false);
        price_button.setClickable(false);
        clear_button.setClickable(false);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        scan_button.setClickable(true);
                        back_bool = true;
                        scan_button.setClickable(true);
                        search_button.setClickable(true);
                        price_button.setClickable(true);
                        clear_button.setClickable(true);
                    }
                });
            }
        }, 2000);
    }

    public void scan_click(View view) {
        scan_barcode();
    }

    public void search_click(View view) {
        timer_dis();
        adapter_empty = true;
        adapter.clear();
        EditText editText = (EditText)findViewById(R.id.barcode_text);
        getData(editText.getText().toString().toLowerCase());
    }

    private void getData(final String s_str){
        final DatabaseReference dbref = db.getReference(getUsername() + "/Products");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys){
                    final String barcode = key.getKey().toString();
                    DataSnapshot name_snap = key.child("/Name");
                    String name = name_snap.getValue().toString();
                    if(barcode.contains(s_str) || name.toLowerCase().contains(s_str)){

                        adapter.add("Barkod: " + barcode + "\n" + "Ürün adı: " + name);
                        adapter_empty = false;

                    }
                }
                Search_Product search_product = new Search_Product(adapter,adapter_empty);
                search_product.show(getSupportFragmentManager(),null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void apply(String barcode) {
        getValuesFromDatabase(barcode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {

            if (intentResult.getContents() == null)
                Toast.makeText(Sell_Product.this,"Barkod okunamadı!",Toast.LENGTH_SHORT).show();
            else {
                checkBarcodeFromDatabase(intentResult.getContents());
                if (serial_scan)
                    scan_barcode();
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void checkBarcodeFromDatabase(final String barcode){
        DatabaseReference dbref = db.getReference(getUsername() + "/Products");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for(DataSnapshot key: keys){
                    if (key.getKey().toString().equals(barcode)){
                        getValuesFromDatabase(barcode);
                        return;
                    }
                }

                Toast.makeText(Sell_Product.this, barcode + "\nborkodlu ürün bulunamadı!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void scan_barcode(){
        final Activity activity = Sell_Product.this;

        IntentIntegrator integrator =  new IntentIntegrator(activity);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setCameraId(0);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        timer_dis();
        integrator.initiateScan();
    }

    private void getValuesFromDatabase(final String barcode){
        DatabaseReference dbref = db.getReference(getUsername() + "/Products/" + barcode);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snap = dataSnapshot.child("Name");
                String name = snap.getValue().toString();

                snap = dataSnapshot.child("Model");
                String model = snap.getValue().toString();

                snap = dataSnapshot.child("Number of products");
                String num = snap.getValue().toString();

                snap = dataSnapshot.child("Buy price");
                String buy = snap.getValue().toString();

                snap = dataSnapshot.child("Sell price");
                String sell = snap.getValue().toString();


                for(int i = 0; i < views; i++){
                    View view = findViewById(R.id.view_id + i);
                    if(view != null){
                        EditText bar = (EditText)view.findViewById(R.id.barcode_edit);

                        if(bar.getText().toString().equals(barcode)){
                            EditText num_edt = view.findViewById(R.id.number_edit);
                            EditText price_edt = view.findViewById(R.id.price_edit);
                            if ( num_edt.getText().toString().equals("") || price_edt.getText().toString().equals("")){
                                String message = "Tamamlanmamış ürünler var.";
                                toast_message(message,Color.RED);
                            }
                            else{
                                int num2 = Integer.parseInt(num_edt.getText().toString());
                                num_edt.setText(Integer.toString(num2 + 1));
                            }

                            return;
                        }
                    }
                }

                addInflater(barcode,name,model,num,buy,sell);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addInflater(String barcode, String name, String model, final String total_num, String buy, String sell){
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

        final ImageButton down_button = (ImageButton)view.findViewById(R.id.down_button);
        final ImageButton up_button = (ImageButton)view.findViewById(R.id.up_button);
        final ImageButton exclamation_button1 = (ImageButton)view.findViewById(R.id.exclamation_button);
        final ImageButton exclamation_button2 = (ImageButton)view.findViewById(R.id.exclamation_button2);

        barcode_edit.setText(barcode);
        name_edit.setText(name);
        model_edit.setText(model);
        number_of_products_edit.setText("1");
        price_edit.setText(sell);
        buy_edit.setText(buy);
        sell_edit.setText(sell);

        price = price + Double.parseDouble(sell);

        String s = String.format("%.2f", price);
        s = s.replace(",",".");
        price = Double.parseDouble(s);
        price_button.setText("Fiyat: " + s);

        view.setLongClickable(true);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                vibrate();
                final AlertDialog alertDialog = new AlertDialog.Builder(Sell_Product.this).create();
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
                        scrollview_layout.removeView(view);
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
                String message = "Stoktaki ürün miktarı " + total_num + " üründür.";
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
        number_of_products_edit.setText("1");
        scrollview_layout.addView(view);
    }



    private void sell(){
        list = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#000000"));


                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.NORMAL);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

                // return the view
                return item;
            }
        };
    }

    public void clear_click(View view) {
        views = 0;
        price_button.setText("Fiyat: 0.00");
        price = 0;
        scrollview_layout.removeAllViews();
    }

    public void price_click(View view) {
        if (price != 0 && ready_sale){
            timer_dis();
            final AlertDialog dialog = new AlertDialog.Builder(Sell_Product.this).create();

            dialog.setTitle(price_button.getText().toString());
            dialog.setMessage("Do you want to complete the sale?");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sales();
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                }
            });

            dialog.show();
        }
        else{
            String message = "There are incomplete products.";
            toast_message(message,Color.RED);
        }
    }

    private void sales(){
        double total_buy_price = 0;
        View view;
        DatabaseReference key_dbref = db.getReference(getUsername() + "/Sales");
        String key = key_dbref.push().getKey();
        DatabaseReference dbref;
        EditText barcode, name, model, num, buy,sell,total_price;
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

            if( view != null ){
                barcode = (EditText)view.findViewById(R.id.barcode_edit);
                name = (EditText)view.findViewById(R.id.product_name_edit);
                model = (EditText)view.findViewById(R.id.model_edit);
                num = (EditText)view.findViewById(R.id.number_edit);
                buy = (EditText)view.findViewById(R.id.buy_edit);
                sell = (EditText)view.findViewById(R.id.sell_edit);
                total_price = (EditText)view.findViewById(R.id.price_edit);

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

                dbref = db.getReference(getUsername() + "/Products/" + barcode_text + "/Number of products");
                final String finalBarcode_text = barcode_text;
                final int num_product = Integer.parseInt(num.getText().toString());
                total_buy_price = total_buy_price + num_product * Double.parseDouble(buy.getText().toString());
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int number = Integer.parseInt(dataSnapshot.getValue().toString());
                        number = number - num_product;

                        DatabaseReference dbref_new = db.getReference(getUsername() + "/Products/" +
                                finalBarcode_text + "/Number of products");

                        dbref_new.setValue(number);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            dbref = db.getReference(getUsername() + "/Sales/" + key + "/Total Buy");
            dbref.setValue(String.format("%.2f",total_buy_price).replace(",","."));



        }
        views = 0;
        price_button.setText("Price: 0.00");
        price = 0;
        scrollview_layout.removeAllViews();
    }
}
