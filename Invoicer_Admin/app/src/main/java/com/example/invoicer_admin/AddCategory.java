package com.example.invoicer_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddCategory extends AppCompatActivity {
    private static LinearLayout lin_lay;
    private static AnimationDrawable ani_draw;
    private static EditText editText;
    private static String username,category;
    private static Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        button = findViewById(R.id.add_button);
        editText = (EditText)findViewById(R.id.ac_id);
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
        if( bundle.getString("Edit") != null){
            category = bundle.getString("Category");
            edit();
        }
    }

    public void cancel_button(View view) {
        Intent intent = new Intent(AddCategory.this,ItemActivity.class);
        intent.putExtra("Event", "No event");
        intent.putExtra("Username",username);

        startActivity(intent);
        finish();
    }

    public void add_button(View view) {
        Intent intent = new Intent(AddCategory.this,ItemActivity.class);
        intent.putExtra("Username",username);

        if (button.getText().toString().equalsIgnoreCase("update")){
            intent.putExtra("Event", "Update_Category");
            intent.putExtra("Old_Category", category);
            if ( editText.getText().toString().equals(""))
                intent.putExtra("Category",editText.getHint().toString());
            else
                intent.putExtra("Category",editText.getText().toString());
            startActivity(intent);
            finish();
        }
        else{
            if ( editText.getText().toString().equals("") != true){

                intent.putExtra("Category",editText.getText().toString());
                intent.putExtra("Event", "Add_Category");
                startActivity(intent);
                finish();
            }
            else{
                editText.setHintTextColor(Color.RED);
            }
        }






    }

    @Override
    public void onBackPressed() {
    }

    public void edit(){
        editText.setHint(category);
        button.setText("Güncelle");
    }


}
