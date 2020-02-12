package com.example.invoicer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class Price_Dialog extends AppCompatDialogFragment {

    private static ArrayAdapter<String> adapter;
    private static double price;
    private ListView lv;
    private TextView tv;
    private PriceDialogListener listener;

    protected Price_Dialog(ArrayAdapter<String> adp,double price){

        this.adapter = adp;
        this.price = price;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.price_dialog,null);
        tv = view.findViewById(R.id.text_view);
        lv = view.findViewById(R.id.list_view);

        builder.setView(view);
        tv.setText("Price: " + Double.toString(price));
        lv.setAdapter(adapter);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.apply_sell();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete(adapterView.getItemAtPosition(i).toString());

            }
        });
        return builder.create();
    }

    private void delete(final String order){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Delete product");
        dialog.setMessage("Do you want the delete this product?\n" + order);

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.remove(order);
                String[] split = order.split("    ");
                double cost = Double.parseDouble(split[2].substring(2));

                price = price - cost;
                tv.setText("Price: " + Double.toString(price));

                listener.apply_change(price,adapter);
            }
        });
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (PriceDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ProductDialogListener");
        }

    }
    public interface PriceDialogListener{
        void apply_change(double price,ArrayAdapter<String> adapter);
        void apply_sell();
    }
}
