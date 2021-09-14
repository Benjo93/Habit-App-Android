package com.example.habitapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Adapters.CategoryAdapter;
import com.example.habitapp.Adapters.ListAdapter;
import com.example.habitapp.Adapters.ScrollSelectorAdapter;
import com.example.habitapp.Utils.Category;
import com.example.habitapp.R;
import com.example.habitapp.Utils.SnapHelperOneByOne;
import com.example.habitapp.Utils.ToDoItem;

import java.util.ArrayList;

import static com.example.habitapp.Managers.ToDoManager.*;

public class ListFragment extends Fragment implements
        ListInteraction,
        SnapListener,
        OnScrollSelectorListener {

    private MainListener mainListener;
    private ScrollSelectorAdapter scrollSelectorInteraction;

    private ArrayList<Category> categoriesList;

    private RecyclerView scrollingRecycler;
    private RecyclerView categoryRecycler;

    private CategoryAdapter categoryAdapter;

    public static ListFragment newInstance(ArrayList<Category> categoriesList) {
        ListFragment listFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("categoriesList", categoriesList);
        listFragment.setArguments(args);
        return listFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { mainListener = (MainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToDoListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoriesList = getArguments().getParcelableArrayList("categoriesList");
            categoriesList.remove(categoriesList.size() - 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.category_bar).setVisibility(View.GONE);

        // Category scroller.

        scrollingRecycler = view.findViewById(R.id.list_category_scroller);

        final SnapHelperOneByOne snapHelper = new SnapHelperOneByOne(this);
        snapHelper.attachToRecyclerView(scrollingRecycler);
        scrollingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ScrollSelectorAdapter scrollingAdapter = new ScrollSelectorAdapter(mainListener.getCategoryNames(), this, 140);
        scrollingRecycler.setAdapter(scrollingAdapter);
        scrollSelectorInteraction = scrollingAdapter;

        // Categories.

        categoryRecycler = view.findViewById(R.id.category_recycler);

        final SnapHelperOneByOne snapHelperOneByOne = new SnapHelperOneByOne(this);
        snapHelperOneByOne.attachToRecyclerView(categoryRecycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoriesList, mainListener);
        categoryRecycler.setAdapter(categoryAdapter);

        // TODO figure out how to check if the item is ready to be scrolled.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goToPosition(mainListener.getActiveCategory());
            }
        }, 1);
    }

    @Override
    public void onItemSnapped(int position) {
        scrollToPosition(position);
    }

    public void scrollItemSelected(View view) {
        int position = scrollingRecycler.getChildLayoutPosition(view);
        scrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {

        scrollingRecycler.smoothScrollToPosition(position);

        categoryRecycler.smoothScrollToPosition(position);

        scrollSelectorInteraction.selectItem(position);

        mainListener.setActiveCategory(position);

/*        categoryRecycler.setAlpha(0f);
        categoryRecycler.animate().alpha(1f).setDuration(500).start();*/

    }

    private void goToPosition(int position) {

        scrollingRecycler.scrollToPosition(position);

        categoryRecycler.scrollToPosition(position);

        scrollSelectorInteraction.selectItem(position);

        mainListener.setActiveCategory(position);

    }

    @Override
    public void addItemToCategory(ToDoItem item, int category) {

        categoriesList.get(category).items.add(item);
        mainListener.setActiveCategory(category);

    }

    @Override
    public void removeItemFromCategory(ToDoItem toDoItem, int category) {
        categoriesList.get(category).items.remove(toDoItem);
        mainListener.setActiveCategory(category);
    }

    @Override
    public void refresh() {
        categoryAdapter.notifyDataSetChanged();
    }
}
