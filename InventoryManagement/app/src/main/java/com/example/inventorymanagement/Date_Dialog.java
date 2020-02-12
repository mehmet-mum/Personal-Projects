package com.example.inventorymanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Date_Dialog extends AppCompatDialogFragment {
    DateDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Date");


        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.date_dialog,null);

        final NumberPicker day_picker = (NumberPicker) view.findViewById(R.id.day_picker);
        final NumberPicker month_picker = (NumberPicker) view.findViewById(R.id.month_picker);
        final NumberPicker year_picker = (NumberPicker) view.findViewById(R.id.year_picker);

        year_picker.setMinValue(2009);
        year_picker.setMaxValue(2022);
        year_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if ( i == 2009 )
                    return " ";

                return Integer.toString(i);
            }
        });

        month_picker.setMinValue(0);
        month_picker.setMaxValue(12);
        month_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if ( i == 0)
                    return " ";

                return String.format("%02d",i);
            }
        });

        day_picker.setMinValue(0);
        day_picker.setMaxValue(31);
        day_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if( i == 0 )
                    return " ";

                return String.format("%02d",i);
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Boolean valid_date = check_valid(day_picker.getValue(),month_picker.getValue(),year_picker.getValue());
                int day = day_picker.getValue();
                int month = month_picker.getValue();
                int year = year_picker.getValue();
                if (valid_date || (day == 0 && month == 0 && year == 2009) || (day == 0 && month == 0 && year != 2009)
                        || (day == 0 && month != 0 && year != 0) )
                    listener.apply_filter(day,month,year);
                else
                    listener.wrong_date();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            listener = (DateDialogListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ProductDialogListener");
        }
    }

    private boolean check_valid(int day, int month, int year){
        try {
            String dateString = String.format("%02d",day) + String.format("%02d",month) + year;
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
            format.setLenient(false);
            format.parse(dateString);

            return true;

        } catch (ParseException e) {
            return false;
        }
    }


    public interface DateDialogListener{
        void apply_filter(int day, int month, int year);
        void wrong_date();
    }
}
