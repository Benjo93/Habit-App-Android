package com.example.habitapp.Adapters;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Utils.Category;
import com.example.habitapp.R;
import com.example.habitapp.Utils.ToDoItem;

import java.util.ArrayList;

import static com.example.habitapp.Managers.ToDoManager.*;

public class CategoryAdapter extends RecyclerView.Adapter implements
        OnListItemListener,
        CategoryInteraction {

    private MainListener mainListener;

    private ArrayList<Category> categoriesList;

    public CategoryAdapter(ArrayList<Category> categoriesList, MainListener mainListener) {
        this.categoriesList = categoriesList;
        this.mainListener = mainListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new ItemViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        RecyclerView listRecycler = itemViewHolder.listRecycler;
        listRecycler.setLayoutManager(new LinearLayoutManager(itemViewHolder.itemView.getContext()));
        final ListAdapter listAdapter = new ListAdapter(categoriesList.get(position).items, mainListener, this);
        listRecycler.setAdapter(listAdapter);

        itemViewHolder.listRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainListener.vibratePhone(10);

                sortCategoriesByCompletion(position, listAdapter);

                itemViewHolder.listRefreshButton.setRotation(0);
                itemViewHolder.listRefreshButton.setAlpha(0f);
                itemViewHolder.listRefreshButton.animate().rotation(360).setDuration(550).start();
                itemViewHolder.listRefreshButton.animate().alpha(1f).setDuration(250).start();

            }
        });

        if (categoriesList.get(position).items.size() <= 0)
            itemViewHolder.emptyListPrompt.setVisibility(View.VISIBLE);
         else itemViewHolder.emptyListPrompt.setVisibility(View.GONE);

    }

    private void sortCategoriesByCompletion(int position, ListAdapter listAdapter) {

        ArrayList<ToDoItem> completedItems = new ArrayList<>();
        ArrayList<ToDoItem> allItems = new ArrayList<>();

        for (ToDoItem item : mainListener.filterCategoriesByModule(mainListener.getActiveModule()).get(position).items)

            if (item.isCompleted()) completedItems.add(item);
            else allItems.add(item);

        allItems.addAll(completedItems);
        categoriesList.get(position).items.clear();
        categoriesList.get(position).items.addAll(allItems);

        listAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    @Override
    public void onToDoCheck(ToDoItem toDoItem, boolean checked) {
        //listAdapter.notifyDataSetChanged();
        mainListener.updateTopBar();
        mainListener.saveToDoData();
    }

    @Override
    public void onToDoClick(ToDoItem toDoItem) {
        //listAdapter.notifyDataSetChanged();
        mainListener.openEditBlock(toDoItem);
    }

    @Override
    public void refresh() {
        //listAdapter.notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout categoryContainer;
        TextView categoryName;
        RecyclerView listRecycler;

        ImageView listSortButton;
        ImageView listRefreshButton;

        TextView emptyListPrompt;

        ItemViewHolder(@NonNull ConstraintLayout container) {
            super(container);

            categoryContainer = container.findViewById(R.id.category_container);
            categoryName = container.findViewById(R.id.category_name);
            listRecycler = container.findViewById(R.id.list_recycler);

            listSortButton = container.findViewById(R.id.list_sort_button);
            listRefreshButton = container.findViewById(R.id.list_refresh_button);

            emptyListPrompt = container.findViewById(R.id.empty_list_prompt);
        }
    }
}
