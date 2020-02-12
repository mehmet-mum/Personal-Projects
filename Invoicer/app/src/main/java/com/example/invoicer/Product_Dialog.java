package com.example.invoicer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Product_Dialog extends AppCompatDialogFragment {
    private TextView tv,pv;
    private EditText et;
    private String product_name,price;
    private ProductDialogListener listener;

    protected Product_Dialog(String product_name,String price){
        this.product_name = product_name;
        this.price = price;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.prod_dialog,null);

        tv = view.findViewById(R.id.text_view);
        et = view.findViewById(R.id.edit_text);
        pv = view.findViewById(R.id.price_view);

        builder.setView(view);
        tv.setText(product_name);
        pv.setText(price);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int num;
                if ( et.getText().toString().equals(""))
                    num = Integer.parseInt(et.getHint().toString());
                else
                    num = Integer.parseInt(et.getText().toString());

                if ( num > 0 ){
                    String product_name = tv.getText().toString();
                    String price_str = pv.getText().toString();

                    double price = Double.parseDouble(price_str.substring(1)) * num;


                    listener.apply(product_name,price,num);

                }
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (ProductDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ProductDialogListener");
        }

    }

    public interface ProductDialogListener{
        void apply(String name, double price, int num);
    }
}
