package com.example.habitapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.habitapp.Managers.ToDoManager;
import com.example.habitapp.Managers.TopBarManager;
import com.example.habitapp.R;

public class TopBarHomeFragment extends Fragment implements
        TopBarManager.TopBarInteraction,
        View.OnClickListener {

    private ToDoManager.MainListener toDoListener;

    public static TopBarHomeFragment newInstance() {
        TopBarHomeFragment topBarHomeFragment = new TopBarHomeFragment();
        Bundle args = new Bundle();
        topBarHomeFragment.setArguments(args);
        return topBarHomeFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { toDoListener = (ToDoManager.MainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToDoListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_bar_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refresh() {

    }
}
