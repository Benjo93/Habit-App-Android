package com.example.habitapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.habitapp.Utils.Category;
import com.example.habitapp.R;

import static com.example.habitapp.Managers.ToDoManager.*;

public class CategoryEditFragment extends Fragment implements
        EditInteraction {

    private MainListener mainListener;
    private Category category;

    private EditText categoryEditText;

    private boolean editing;
    private int position;

    public CategoryEditFragment() { }

    public static CategoryEditFragment newInstance(Category category, int position, MainListener mainListener) {
        CategoryEditFragment categoryEditFragment = new CategoryEditFragment();
        categoryEditFragment.mainListener = mainListener;
        Bundle args = new Bundle();
        args.putParcelable("category", category);
        args.putInt("position", position);
        categoryEditFragment.setArguments(args);
        return categoryEditFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getParcelable("category");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editing = category.isActive();

        categoryEditText = view.findViewById(R.id.category_edit_text);
        categoryEditText.setText(category.name);
        categoryEditText.requestFocus();

    }

    @Override
    public void clearFocus() {

    }

    @Override
    public void saveCurrentItemFields() {

        category.name = categoryEditText.getText().toString();

        if (editing) { // Category is being edited.

            mainListener.updateCategory(category, position);

        } else { // Category is being added.

            mainListener.addCategory(category);

        }
    }

    @Override
    public void deleteCurrentItem() {
        if (editing) mainListener.removeCategory(position);
    }

    @Override
    public void setTime(int hourOfDay, int minute) { }

    @Override
    public void setRepeat(int repeatType) { }
}
