package com.example.habitapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.example.habitapp.R;
import static com.example.habitapp.Managers.ToDoManager.*;

public class DialogTimeFragment extends DialogFragment implements
        TimePicker.OnTimeChangedListener {

    private EditInteraction editInteraction;
    private MainListener mainListener;

    public DialogTimeFragment() { }

    public static DialogTimeFragment newInstance(EditInteraction editInteraction) {
        DialogTimeFragment fragment = new DialogTimeFragment();
        fragment.editInteraction = editInteraction;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mainListener = (MainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MainListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TimePicker timePicker = view.findViewById(R.id.dialog_time);
        timePicker.setOnTimeChangedListener(this);

    }

    public void onTimeSelectedTimeChanged(int hourOfDay, int minute) {
        editInteraction.setTime(hourOfDay, minute);
        mainListener.closeTimePickerDialog();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        editInteraction.setTime(hourOfDay, minute);
        mainListener.closeTimePickerDialog();
    }
}
