package com.aparnyuk.rsn.fragment.dialog;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.aparnyuk.rsn.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RemindDialog extends DialogFragment {
    private EditText nameView, dateView, timeView;
    ;
    private Spinner repeatRemindSpinner, quantityRemindSpinner;
    private int day, month, year, hour, minute;
    private static TimePickerDialog timePicker;
    private static DatePickerDialog datePicker;
    private static Button okButton, cancelButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.remind_dialog, null);

        nameView = (EditText) view.findViewById(R.id.remind_dialog_text);
        nameView.addTextChangedListener(titleWatcher);

        dateView = (EditText) view.findViewById(R.id.remind_date);
        timeView = (EditText) view.findViewById(R.id.remind_time);

        Date date = new Date();
        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getOffset(date.getTime()) / (60 * 60 * 1000);

        DateTime dateTime = new DateTime(date);
        LocalDateTime localDateTime = dateTime.toLocalDateTime();

        day = localDateTime.getDayOfMonth();
        month = localDateTime.getMonthOfYear();
        year = localDateTime.getYear();
        hour = localDateTime.getHourOfDay() + offset;
        minute = localDateTime.getMinuteOfHour();

        datePicker = new DatePickerDialog(getActivity(), myDateListener, year, month, day);
        timePicker = new TimePickerDialog(getActivity(), myTimeListener, hour, minute, true);
        showDate(year, month, day);
        showTime(hour, minute);


        //Set OnClickListener on Date and Time
        dateView.setOnClickListener(new View.OnClickListener() {
            public void setDatePicker(DatePickerDialog mDatePicker) {
                final Calendar calendar = Calendar.getInstance();
                mDatePicker = datePicker;
                mDatePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.show();
            }

            @Override
            public void onClick(View v) {
                setDatePicker(datePicker);
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            public void setTimePicker(TimePickerDialog mTimePicker) {
                mTimePicker = timePicker;
                mTimePicker.show();
            }

            @Override
            public void onClick(View v) {
                setTimePicker(timePicker);
            }
        });

        //Show Spinners
        repeatRemindSpinner = (Spinner) view.findViewById(R.id.repeatRemindSpinner);
        ArrayAdapter<CharSequence> arrayRepeatAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.repeat, android.R.layout.simple_spinner_dropdown_item);
        arrayRepeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatRemindSpinner.setAdapter(arrayRepeatAdapter);

        quantityRemindSpinner = (Spinner) view.findViewById(R.id.quantityRemindSpinner);
        ArrayAdapter<CharSequence> arrayQuantityAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.quantity, android.R.layout.simple_spinner_dropdown_item);
        arrayRepeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityRemindSpinner.setAdapter(arrayQuantityAdapter);


        builder.setTitle(R.string.dialog_create_remind)
                .setView(view)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_cancel, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (nameView.getText().toString().isEmpty()) {
                    okButton.setEnabled(false);
                }
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        sendCreateItemEvent();
                        dialog.dismiss();
                    }
                });
                cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    private final TextWatcher titleWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 3) {
                if (okButton.isEnabled()) {
                    okButton.setEnabled(false);
                }
            } else {
                if (!okButton.isEnabled()) {
                    okButton.setEnabled(true);
                }
                nameView.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int day, int month, int year) {
            showDate(day, month, year);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            showTime(hour, minute);
        }
    };

    private void showDate(int day, int month, int year) {
        dateView.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }

    public void showTime(int hour, int minute) {
        String mHour = "00";
        if (hour < 10) {
            mHour = "0" + hour;
        } else {
            mHour = String.valueOf(hour);
        }

        String mMinute = "00";
        if (minute < 10) {
            mMinute = "0" + minute;
        } else {
            mMinute = String.valueOf(minute);
        }
        timeView.setText(new StringBuilder().append(mHour)
                .append(":").append(mMinute));
    }
//
//    private boolean dateHasPassed(EditText dateView) {
//        String[] strings = dateView.getText().toString().split("/");
//        Integer[] ints = new Integer[3];
//        ints[0] = Integer.parseInt(strings[0]);
//        ints[1] = Integer.parseInt(strings[1]);
//        ints[2] = Integer.parseInt(strings[2]);
//        Date date = new Date();
//        DateTime curDateTime = new DateTime(date);
//        DateTime eventDateTime = new DateTime(ints[2], ints[1], ints[0], 0, 0, 0);
//        LocalDate currentDateLocal = curDateTime.toLocalDate();
//        LocalDate eventDateLocal = eventDateTime.toLocalDate();
//        if (eventDateLocal.isBefore(currentDateLocal)) {
//            return true;
//        } else return false;
//    }
}
