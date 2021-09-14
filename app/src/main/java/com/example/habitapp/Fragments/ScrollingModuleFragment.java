package com.example.habitapp.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Adapters.ScrollSelectorAdapter;
import com.example.habitapp.Managers.ToDoManager;
import com.example.habitapp.Managers.TopBarManager;
import com.example.habitapp.R;

import java.util.ArrayList;

public class ScrollingModuleFragment extends Fragment
        implements View.OnClickListener,
        ToDoManager.OnScrollSelectorListener,
        TopBarManager.TopBarInteraction {

    private ToDoManager.MainListener toDoListener;
    private ToDoManager.EditInteraction editInteraction;

    // Date Selector.
    private RecyclerView scrollingRecycler;
    private ScrollSelectorAdapter scrollingAdapter;
    private LinearSnapHelper snapHelper;

    private ArrayList<String> moduleNames;

    public ScrollingModuleFragment() { }

    static ScrollingModuleFragment newInstance(ArrayList<String> moduleNames) {
        ScrollingModuleFragment fragment = new ScrollingModuleFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("module_names", moduleNames);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            toDoListener = (ToDoManager.MainListener) context;
            //toDoInteraction = ((ToDoManager.OnToDoListener) context).getCurrentToDoInteraction();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToDoListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleNames = getArguments().getStringArrayList("module_names");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scrolling_module, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollingRecycler = view.findViewById(R.id.scrolling_recycler);
        snapHelper = new LinearSnapHelper();

        snapHelper.attachToRecyclerView(scrollingRecycler);
        scrollingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
                if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = snapHelper.findSnapView(this);
                    scrollItemSelected(view);
                }
            }
        });

        // Setting the padding, linear snap helper and setting the adapter.
        int padding = (Resources.getSystem().getDisplayMetrics().widthPixels / 2) -
                51 * getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;

        scrollingRecycler.setPadding(padding, 0, padding, 0);
        scrollingAdapter = new ScrollSelectorAdapter(moduleNames, this, 100);
        scrollingRecycler.setAdapter(scrollingAdapter);
    }

    @Override
    public void scrollItemSelected(View view) {
        int position = scrollingRecycler.getChildLayoutPosition(view);
        scrollingRecycler.smoothScrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {

    }

    @Override
    public void onClick(View view){
        scrollItemSelected(view);
    }

    @Override
    public void refresh() {
        scrollingAdapter.notifyDataSetChanged();
    }
}
