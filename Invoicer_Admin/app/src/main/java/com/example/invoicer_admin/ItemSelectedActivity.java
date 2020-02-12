package com.example.invoicer_admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectedActivity extends AppCompatActivity {

    private static LinearLayout lin_lay;
    private static AnimationDrawable ani_draw;
    private static Toolbar toolbar;
    static private ListView listView;
    private static String category;
    private static String username;
    static private ArrayAdapter<String> arrayAdapter;
    static private List<String> dataList;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selected);
        db = FirebaseDatabase.getInstance();
        listView = (ListView)findViewById(R.id.list_view);
        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemSelectedActivity.this,ItemActivity.class);
                intent.putExtra("Event", "Back");
                intent.putExtra("Username", username);
                intent.putExtra("Category", category);
                startActivity(intent);
                finish();
            }
        });
        event();
        this.arrayAdapterListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.item_selectedmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {


        if(item.getItemId() == R.id.add_item){
            menu_selected("add_item");
        }
        else if(item.getItemId() == R.id.delete_opt){
            final AlertDialog alertDialog = new AlertDialog.Builder(ItemSelectedActivity.this).create();
            alertDialog.setTitle(category);
            alertDialog.setMessage("Do you want to delete this category?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    menu_selected("delete_item");
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                }
            });
            alertDialog.show();

        }
        else if (item.getItemId() == R.id.edit_opt){
            menu_selected("edit_item");
        }



        return super.onOptionsItemSelected(item);
    }

    private void menu_selected(String str){
        Intent intent = new Intent(this,ItemActivity.class);
        if( str.equals("add_item")){
            intent = new Intent(ItemSelectedActivity.this,AddItem.class);
        }
        else if ( str.equals("edit_item")){
            intent = new Intent(ItemSelectedActivity.this, AddCategory.class);
            intent.putExtra("Edit", "Edit");

        }
        else if ( str.equals("delete_item")){
            intent = new Intent(ItemSelectedActivity.this,ItemActivity.class);
            intent.putExtra("Event", "Delete");
        }
        intent.putExtra("Username", username);
        intent.putExtra("Category", category);

        startActivity(intent);
        finish();
    }

    private void event(){
        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("Category");
        toolbar.setTitle(category);
        username = bundle.getString("Username");
        String event = bundle.getString("Event");
        String item = bundle.getString("Item");

        if ( event.equals("Add_Item")){
            add_item(item,bundle.getDouble("Price"));
        }
        else if ( event.equals("Delete_Item")){
            delete_item(item);
        }
        else if ( event.equals("Update_Item")){
            update_item(bundle.getString("Old_Item"),item,bundle.getDouble("Price"));
        }
    }

    private void update_item(String old_item, String item, Double price){
        DatabaseReference old_dbref = db.getReference(username + "/" + category + "/" + old_item);
        old_dbref.setValue(null);

        DatabaseReference dbref = db.getReference(username + "/" + category + "/" + item);
        dbref.setValue(price);



    }
    private void add_item(String item,Double price){
        DatabaseReference newRef = db.getReference(username+"/"+category+"/"+item);
        newRef.setValue(price);

    }
    private void delete_item(final String item){
        DatabaseReference dbref = db.getReference(username + "/" + category + "/" + item);
        dbref.setValue(null);
    }


    // This method use an ArrayAdapter to add data in ListView.
    private void arrayAdapterListView()
    {

        dataList = new ArrayList<String>();

        DatabaseReference dbref = db.getReference(username + "/" + category);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                arrayAdapter.clear();
                for (DataSnapshot key: keys){
                    String str = key.getKey().toString() + "   \n₺" + key.getValue().toString();
                    if(arrayAdapter.getPosition(str) == -1)
                        arrayAdapter.add(str);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView = (ListView)findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList){
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


        };

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String str[] = adapterView.getAdapter().getItem(index).toString().split("   \n₺");
                Intent intent = new Intent(ItemSelectedActivity.this,AddItem.class);
                String name = str[0];

                double price = Double.parseDouble(str[1]);

                intent.putExtra("Item",name );
                intent.putExtra("Price", price);
                intent.putExtra("Update", "Update");
                intent.putExtra("Username",username);
                intent.putExtra("Category", category);
                startActivity(intent);
                finish();
            }
        });


    }
    @Override
    public void onBackPressed() {
    }
}
