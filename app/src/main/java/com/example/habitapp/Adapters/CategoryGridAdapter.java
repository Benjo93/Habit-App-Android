package com.example.habitapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Utils.Category;
import com.example.habitapp.R;

import java.util.ArrayList;

import static com.example.habitapp.Managers.ToDoManager.*;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.CategoryViewHolder> {

    private ArrayList<Category> categoriesList;
    private MainListener mainListener;
    private OnModuleListener moduleListener;

    public CategoryGridAdapter(ArrayList<Category> categoriesList, OnModuleListener moduleListener, MainListener mainListener) {
        this.categoriesList = categoriesList;
        this.mainListener = mainListener;
        this.moduleListener = moduleListener;
    }

    @NonNull
    @Override
    public CategoryGridAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_grid_item, parent, false);
        return new CategoryViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryGridAdapter.CategoryViewHolder holder, final int position) {

        holder.categoryName.setText(categoriesList.get(position).name);

        //if (categoriesList.get(position).items.size() == 0) holder.categoryName.setAlpha(0.4f);

        holder.categoryNameEdit.setText(categoriesList.get(position).name);

        holder.categoryItems.setText(String.format("%s items", categoriesList.get(position).items.size()));
        holder.categoryProgress.setProgress(categoriesList.get(position).getProgress(), true);

        holder.categoryItemShow.setVisibility(View.VISIBLE);
        holder.categoryItemEdit.setVisibility(View.INVISIBLE);
        holder.categoryItemAdd.setVisibility(View.INVISIBLE);

        if (position == categoriesList.size() - 1) {
            holder.categoryItemShow.setVisibility(View.INVISIBLE);
            holder.categoryItemAdd.setVisibility(View.VISIBLE);
        }

        holder.categoryShowClicker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainListener.openCategoryEditBlock(categoriesList.get(position), position);
                return true;
            }
        });
        
        holder.categoryAddClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainListener.openCategoryEditBlock(new Category("", categoriesList.size(), 11234), position);
            }
        });

        holder.categoryShowClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleListener.categoryInModuleSelected(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        TextView categoryNameEdit;
        TextView categoryItems;
        ProgressBar categoryProgress;

        ConstraintLayout categoryItemShow;
        ConstraintLayout categoryItemEdit;
        ConstraintLayout categoryItemAdd;

        RelativeLayout categoryShowClicker;
        RelativeLayout categoryEditClicker;
        RelativeLayout categoryAddClicker;

         CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            categoryNameEdit = itemView.findViewById(R.id.category_edit_text);
            categoryItems = itemView.findViewById(R.id.category_items);
            categoryProgress = itemView.findViewById(R.id.category_progress);

            categoryItemShow = itemView.findViewById(R.id.category_item_show);
            categoryItemEdit = itemView.findViewById(R.id.category_item_edit);
            categoryItemAdd = itemView.findViewById(R.id.category_item_add);

             categoryShowClicker = itemView.findViewById(R.id.category_show_clicker);
             categoryEditClicker = itemView.findViewById(R.id.category_edit_clicker);
             categoryAddClicker = itemView.findViewById(R.id.category_add_clicker);
        }
    }
}
