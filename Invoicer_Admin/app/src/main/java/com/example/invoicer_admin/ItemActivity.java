package com.example.invoicer_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


public class ItemActivity extends AppCompatActivity {

    static private LinearLayout lin_lay;
    static private AnimationDrawable ani_draw;
    static private ListView listView;
    static private ArrayAdapter<String> arrayAdapter;
    static private List<String> dataList;
    static private String username;

    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        db = FirebaseDatabase.getInstance();

        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if ( getIntent().getExtras() != null ){
            event();
        }
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
        menuInflater.inflate(R.menu.item_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if(item.getItemId() == R.id.add_cate){
            Intent intent = new Intent(ItemActivity.this, AddCategory.class);
            intent.putExtra("Username",username);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.log_out){
            Intent intent = new Intent(ItemActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.quit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // This method use an ArrayAdapter to add data in ListView.
    private void arrayAdapterListView()
    {

        dataList = new ArrayList<String>();

        DatabaseReference dbref = db.getReference(username + "/Categories");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                arrayAdapter.clear();
                for (DataSnapshot key: keys){
                    if(arrayAdapter.getPosition(key.getValue().toString()) == -1)
                        arrayAdapter.add(key.getValue().toString());
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

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
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                Intent intent = new Intent(ItemActivity.this,ItemSelectedActivity.class);
                intent.putExtra("Category", clickItemObj.toString());
                intent.putExtra("Event", "Select");
                intent.putExtra("Username",username);
                startActivity(intent);
                finish();
            }
        });


    }

    private void event(){
        Bundle bundle = getIntent().getExtras();
        String event = bundle.getString("Event");
        String category = bundle.getString("Category");
        String old_category = bundle.getString("Old_Category");
        username = bundle.getString("Username");


        if ( event.equals("Add_Category")){
            add_category(category);
        }
        else if ( event.equals("Delete")){
            delete_category(category);
        }
        else if (event.equals("Update_Category")){
            if ( category.equalsIgnoreCase(old_category ) != true)
                update_category(old_category,category);
        }


    }

    private void add_category(String category){
        DatabaseReference dbref = db.getReference(username+"/Categories");
        String key = dbref.push().getKey();
        DatabaseReference newref = db.getReference(username+"/Categories/"+key);
        newref.setValue(category);
    }

    private void delete_category(final String category){
        DatabaseReference dbref = db.getReference(username + "/Categories");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for (DataSnapshot key: keys){

                    if ( key.getValue().toString().equals(category)){
                        DatabaseReference db_del = db.getReference(username+"/Categories/"+key.getKey().toString());
                        db_del.setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

        dbref = db.getReference(username + "/" + category);
        dbref.setValue(null);

    }
    private void update_category(final String category,final String category_new){
        DatabaseReference dbref = db.getReference(username + "/Categories");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();

                for (DataSnapshot key: keys){

                    if ( key.getValue().toString().equals(category)){
                        DatabaseReference db_del = db.getReference(username+"/Categories/"+key.getKey().toString());
                        db_del.setValue(category_new);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

        dbref = db.getReference(username + "/" + category);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                DatabaseReference new_dbref;

                String name;
                Double price;
                for( DataSnapshot key: keys ){
                    name = key.getKey().toString();
                    price = Double.parseDouble(String.valueOf(key.getValue()));
                    new_dbref = db.getReference(username + "/" + category_new + "/" + name);
                    new_dbref.setValue(price);
                }

                new_dbref = db.getReference(username + "/" + category);
                new_dbref.setValue(null);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {
    }
}
