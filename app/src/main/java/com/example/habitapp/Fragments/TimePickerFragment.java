package com.example.habitapp.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

import com.example.habitapp.Managers.ToDoManager;

import java.util.Calendar;

import static com.example.habitapp.Managers.ToDoManager.*;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditInteraction editInteraction;

    public TimePickerFragment(EditInteraction editInteraction) {
        this.editInteraction = editInteraction;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        editInteraction.setTime(hourOfDay, minute);
    }
}