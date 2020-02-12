package com.example.invoicer_admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

public class AddItem extends AppCompatActivity {
    private static LinearLayout lin_lay;
    private static AnimationDrawable ani_draw;
    private static EditText name_edit,price_edit;
    private static String username,category;
    private static Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        add = (Button)findViewById(R.id.add);
        name_edit = (EditText)findViewById(R.id.name_edit);
        price_edit = (EditText)findViewById(R.id.price_edit);
        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("Username");
        category = bundle.getString("Category");
        if( bundle.getString("Update") != null ){
            Button del = (Button)findViewById(R.id.delete);

            add.setText("Update");
            del.setVisibility(View.VISIBLE);
            name_edit.setHint(bundle.getString("Item"));
            price_edit.setHint(bundle.get("Price").toString());
        }
    }

    public void cancel_button(View view) {
        Intent intent = new Intent(AddItem.this,ItemSelectedActivity.class);
        intent.putExtra("Event", "No event");
        intent.putExtra("Username",username);
        intent.putExtra("Category", category);

        startActivity(intent);
        finish();
    }

    public void add_button(View view) {
        Intent intent = new Intent(AddItem.this,ItemSelectedActivity.class);
        intent.putExtra("Category",category);
        intent.putExtra("Username", username);

        if( add.getText().toString().equalsIgnoreCase("update")) {
            intent.putExtra("Event", "Update_Item");

            if (name_edit.getText().toString().equals("")) {
                intent.putExtra("Item", name_edit.getHint().toString());
            }
            else
                intent.putExtra("Item", name_edit.getText().toString());

            intent.putExtra("Old_Item", name_edit.getHint().toString());

            if (price_edit.getText().toString().equals("")) {
                intent.putExtra("Price",Double.parseDouble(price_edit.getHint().toString()));
            }
            else
                intent.putExtra("Price",Double.parseDouble(price_edit.getText().toString()));

            startActivity(intent);
            finish();
        }
        else {
            if ( name_edit.getText().toString().equals("") != true && price_edit.getText().toString().equals("") != true){

                intent.putExtra("Event", "Add_Item");
                intent.putExtra("Item",name_edit.getText().toString());
                intent.putExtra("Price",Double.parseDouble(price_edit.getText().toString()));
                startActivity(intent);
                finish();

            }
            else{
                if (name_edit.getText().toString().equals(""))
                    name_edit.setHintTextColor(Color.RED);

                if (price_edit.getText().toString().equals(""))
                    price_edit.setHintTextColor(Color.RED);
            }
        }



    }
    public void delete_button(View view) {

        final AlertDialog alertDialog = new AlertDialog.Builder(AddItem.this).create();

        alertDialog.setTitle(name_edit.getHint().toString());
        alertDialog.setMessage("Do you want to delete this product?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(AddItem.this,ItemSelectedActivity.class);
                intent.putExtra("Event", "Delete_Item");
                intent.putExtra("Item", name_edit.getHint());
                intent.putExtra("Category", category);
                intent.putExtra("Username", username);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
            }
        });

        alertDialog.show();

    }
    @Override
    public void onBackPressed() {
    }
}