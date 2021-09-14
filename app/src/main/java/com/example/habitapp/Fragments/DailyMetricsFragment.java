package com.example.habitapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.habitapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DailyMetricsFragment extends Fragment {

    public DailyMetricsFragment() { }

    public static DailyMetricsFragment newInstance() {
        DailyMetricsFragment fragment = new DailyMetricsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_metrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dailyMetricsDate = view.findViewById(R.id.daily_metrics_date);
        TextView dailyMetricsTime = view.findViewById(R.id.daily_metrics_time);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE, MMMM dd", Locale.US);
        String formattedDay = dayFormatter.format(Calendar.getInstance().getTime());

        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm", Locale.US);
        String formattedTime = timeFormatter.format(Calendar.getInstance().getTime());

        dailyMetricsDate.setText(formattedDay);
        dailyMetricsTime.setText(formattedTime);

    }
}
