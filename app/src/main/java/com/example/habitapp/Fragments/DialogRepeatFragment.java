package com.example.habitapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.habitapp.R;
import com.example.habitapp.Utils.Habit;

import static com.example.habitapp.Managers.ToDoManager.*;

public class DialogRepeatFragment extends DialogFragment implements
        View.OnClickListener {

    private EditInteraction editInteraction;
    private MainListener mainListener;

    public DialogRepeatFragment() { }

    public static DialogRepeatFragment newInstance(EditInteraction editInteraction) {
        DialogRepeatFragment fragment = new DialogRepeatFragment();
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
        return inflater.inflate(R.layout.fragment_dialog_repeat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button repeatDaily = view.findViewById(R.id.repeat_daily);
        Button repeatWeekly = view.findViewById(R.id.repeat_weekly);
        Button repeatMonthly = view.findViewById(R.id.repeat_monthly);

        repeatDaily.setOnClickListener(this);
        repeatWeekly.setOnClickListener(this);
        repeatMonthly.setOnClickListener(this);
    }

    public void onTimeSelectedTimeChanged(int hourOfDay, int minute) {
        editInteraction.setTime(hourOfDay, minute);
        mainListener.closeTimePickerDialog();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.repeat_daily:
                editInteraction.setRepeat(Habit.REPEAT_DAILY);
                Toast.makeText(getContext(), "Repeat Daily", Toast.LENGTH_SHORT).show();
                break;

            case R.id.repeat_weekly:
                editInteraction.setRepeat(Habit.REPEAT_WEEKLY);
                Toast.makeText(getContext(), "Repeat Weekly", Toast.LENGTH_SHORT).show();
                break;

            case R.id.repeat_monthly:
                editInteraction.setRepeat(Habit.REPEAT_MONTHLY);
                Toast.makeText(getContext(), "Repeat Monthly", Toast.LENGTH_SHORT).show();
                break;
        }

        mainListener.closeRepeatPickerDialog();

    }
}
