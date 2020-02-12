package com.example.inventorymanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDialogFragment;


public class Search_Product extends AppCompatDialogFragment {
    private ArrayAdapter<String> adapter;
    private Boolean empty;
    private SearchProductListener listener;



    protected Search_Product(ArrayAdapter<String> adapter,Boolean empty){
        this.adapter = adapter;
        this.empty = empty;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.search_dialog,null);


        ListView lv = (ListView)view.findViewById(R.id.s_list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                selected.replace("'\n'", " ");
                String splt[] = selected.split("\n");
                splt = splt[0].split(" ");

                listener.apply(splt[1]);
                dismiss();

            }
        });

        if(empty)
            builder.setTitle("Product not found");
        else
            builder.setTitle("Products");

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SearchProductListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ProductDialogListener");
        }
    }

    public interface SearchProductListener{
        void apply(String barcode);
    }



}
