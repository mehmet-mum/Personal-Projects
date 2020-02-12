package com.example.invoicer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class InventoryActivity extends AppCompatActivity implements Product_Dialog.ProductDialogListener , Price_Dialog.PriceDialogListener {

    private static String username;
    private static LinearLayout linearLayout,inventory_layout;
    static private ArrayAdapter<String>[]  arrayAdapter;
    static private ArrayAdapter<String> sell_adapter;
    static private List<String> dataList, sell_list;
    static private AnimationDrawable ani_draw;
    static private Button p_button;
    static private double sell_price;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        sell();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        p_button = (Button) findViewById(R.id.price_button);
        linearLayout = (LinearLayout) findViewById(R.id.lin_lay);
        inventory_layout = (LinearLayout) findViewById(R.id.inventory_layout);
        ani_draw = (AnimationDrawable) inventory_layout.getBackground();
        ani_draw.setExitFadeDuration(3000);
        getExtra();

        db = FirebaseDatabase.getInstance();
        asd();
        sell_price = 0;


    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    private void asd(){
        DatabaseReference dbref = db.getReference(username + "/Categories");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                arrayAdapter = new ArrayAdapter[(int)dataSnapshot.getChildrenCount()];
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                int i = 0;
                for (DataSnapshot key: keys){
                    arrayAdapterListView(key.getValue().toString(),i);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void arrayAdapterListView(final String category,final int i)
    {
        dataList = new ArrayList<String>();

        arrayAdapter[i] = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#FFFFFF"));


                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

                // return the view
                return item;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                tv.setTypeface(tv.getTypeface(),Typeface.BOLD);
                if(position == 0){

                    tv.setTextColor(Color.MAGENTA);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                }
                else {
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                }

                return view;
            }


        };

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);


        Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(lparams);
        linearLayout.addView(spinner);


        DatabaseReference dbref = db.getReference(username + "/" + category);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayAdapter[i].clear();
                arrayAdapter[i].add(category);

                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key: keys){
                    arrayAdapter[i].add(key.getKey().toString() + "   ₺" + key.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        spinner.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        spinner.setAdapter(arrayAdapter[i]);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setSelection(0);
                if ( i != 0)
                    openDialog(adapterView.getItemAtPosition(i).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void getExtra(){
        Bundle bundle = getIntent().getExtras();

        username = bundle.getString("Username");
    }

    private void sell(){
        sell_list = new ArrayList<String>();

        sell_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sell_list){
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

    @Override
    public void apply(String name, double price, int num) {
        String splt[];

        sell_price = sell_price + price;
        p_button.setText("Fiyat: " + sell_price);
        for ( int i = 0; i < sell_adapter.getCount(); i++){
            splt = sell_adapter.getItem(i).split("    ");
            if(splt[0].equals(name)){
                sell_adapter.remove(sell_adapter.getItem(i));
                double new_price = Double.parseDouble(splt[2].substring(2));
                int new_num = Integer.parseInt(splt[1].substring(1));

                num = new_num + num;
                price = new_price + price;

                break;
            }
        }


        sell_adapter.add(name + "    x" + num + "    \n₺" + price);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.log_out){
            Intent intent = new Intent(InventoryActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.quit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog(String product_name){
        String[] sp = product_name.split("   ");
        Product_Dialog pd = new Product_Dialog(sp[0], sp[1]);
        pd.show(getSupportFragmentManager(),null);
    }

    public void price_click(View view) {
        Price_Dialog pd = new Price_Dialog(sell_adapter,sell_price);
        pd.show(getSupportFragmentManager(),null);
    }

    public void clear_click(View view) {
        sell_price = 0;
        sell_adapter.clear();
        p_button.setText("Price: 0");    }

    @Override
    public void apply_sell() {

        Date date = new Date();

        String strDateFormat = "dd:MM:yyyy hh:mm:ss:SS";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        String formattedDate= dateFormat.format(date);

        DatabaseReference dbref = db.getReference(username + "/sell/" + formattedDate.toString() + "/PRICE");
        dbref.setValue(sell_price);

        dbref = db.getReference(username + "/sell/" + formattedDate.toString() + "/ORDERS");
        String key;
        String order;
        DatabaseReference dbnew;
        for (int i = 0; i < sell_adapter.getCount();i++){
            key = dbref.push().getKey();
            dbnew = db.getReference(username + "/sell/" + formattedDate.toString() + "/ORDERS/" + key);
            order = sell_adapter.getItem(i);
            order = order.replace("\n", "");
            dbnew.setValue(order);
        }


        sell_price = 0;
        sell_adapter.clear();
        p_button.setText("Price: 0");
    }

    @Override
    public void apply_change(double price, ArrayAdapter<String> adapter) {
        sell_price = price;
        sell_adapter = adapter;
        p_button.setText("Price: " + sell_price);
    }
}
