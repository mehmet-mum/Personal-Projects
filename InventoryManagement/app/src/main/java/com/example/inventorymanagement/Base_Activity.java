package com.example.inventorymanagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Base_Activity extends AppCompatActivity {

    private static String username = new String();

    private static Vibrator v;


    public static void setUsername(String usr){
        username = usr;
    }

    public static String getUsername(){
        return username;
    }

    public void toast_message(String message, int color){
        Toast toast = Toast.makeText(Base_Activity.this,message,Toast.LENGTH_LONG);
        View view1 = toast.getView();
        view1.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        TextView textView = (TextView)view1.findViewById(android.R.id.message);
        textView.setTextColor(Color.WHITE);

        toast.show();
    }

    public void set_vibrate(){
        v = (Vibrator) Base_Activity.this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(){
        v.vibrate(30); // 1000 = 1 sec
    }



}
