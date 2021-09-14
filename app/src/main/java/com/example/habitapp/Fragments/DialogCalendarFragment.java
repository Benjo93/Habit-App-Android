package com.example.habitapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.habitapp.R;

import java.util.Objects;

import static com.example.habitapp.Managers.ToDoManager.*;

public class DialogCalendarFragment extends DialogFragment implements
        CalendarView.OnDateChangeListener {

    private ModuleInteraction moduleInteraction;
    private MainListener mainListener;

    public DialogCalendarFragment() { }

    public static DialogCalendarFragment newInstance(ModuleInteraction moduleInteraction) {
        DialogCalendarFragment fragment = new DialogCalendarFragment();
        fragment.moduleInteraction = moduleInteraction;
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
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_dialog_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarView calendarView = view.findViewById(R.id.dialog_calendar);

        calendarView.setDateTextAppearance(R.style.CalendarDateTextAppearance);
        calendarView.setWeekDayTextAppearance(R.style.CalendarWeekTextAppearance);

        calendarView.setDate(mainListener.getMillisecondFromDay(mainListener.getActiveModule()));
        calendarView.setOnDateChangeListener(this);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        moduleInteraction.changeDate(year, month, dayOfMonth);
        mainListener.closeDatePickerDialog();
    }
}
