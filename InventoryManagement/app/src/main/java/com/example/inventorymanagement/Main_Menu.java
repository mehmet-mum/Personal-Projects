package com.example.inventorymanagement;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

public class Main_Menu extends Base_Activity {
    private static LinearLayout lin_lay;
    private static Toolbar toolbar;
    private static AnimationDrawable ani_draw;
    private static Button add_pdct, sale, sale_info, stock_info, exit_butt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu);


        lin_lay = (LinearLayout)findViewById(R.id.lin_lay);
        ani_draw = (AnimationDrawable)lin_lay.getBackground();
        ani_draw.setExitFadeDuration(3000);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getUsername());

        add_pdct = (Button)findViewById(R.id.add_product_button);
        sale = (Button)findViewById(R.id.sell_product_button);
        sale_info = (Button)findViewById(R.id.sales_info_button);
        stock_info = (Button)findViewById(R.id.stock_info_button);
        exit_butt = (Button)findViewById(R.id.exit_button);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ani_draw.start();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }


    public void add_product(View view) {
        Intent intent = new Intent(Main_Menu.this,Add_Product.class);
        startActivity(intent);
        disableButtons();
        finish();
    }

    public void sell_product(View view) {
        Intent intent = new Intent(Main_Menu.this,Sell_Product.class);
        startActivity(intent);
        disableButtons();
        finish();
    }

    public void sales_info(View view) {
        Intent intent = new Intent(Main_Menu.this,Sale_info.class);
        startActivity(intent);
        disableButtons();
        finish();
    }

    public void stock_info(View view) {
        Intent intent = new Intent(Main_Menu.this,Stock_info.class);
        startActivity(intent);
        disableButtons();
        finish();
    }

    public void exit(View view) {
        finish();
    }

    private void disableButtons(){
        add_pdct.setClickable(false);
        sale.setClickable(false);
        sale_info.setClickable(false);
        stock_info.setClickable(false);
        exit_butt.setClickable(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        add_pdct.setClickable(true);
                        sale.setClickable(true);
                        sale_info.setClickable(true);
                        stock_info.setClickable(true);
                        exit_butt.setClickable(true);
                    }
                });
            }
        }, 2000);
    }
}
