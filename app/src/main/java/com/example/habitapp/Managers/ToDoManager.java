package com.example.habitapp.Managers;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.habitapp.Utils.Category;
import com.example.habitapp.Utils.ToDoItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoManager {

    public interface MainListener {

        int getCurrentDay();
        Calendar getCurrentDate();
        long getMillisecondFromDay(int dayOfYear);


        void startFragment (View container, Fragment fragment, String fragmentName);

        void addToDoItem(ToDoItem toDoItem);
        void deleteToDoItem(ToDoItem toDoItem);
        void updateTodoItems();
        List<ToDoItem> getTodoItems();

        void updateTopBar();

        void startSpecificList(int module, int category);
        ArrayList<Category> filterCategoriesByModule(int module);
        ArrayList<Category> filterCategoriesByDate(int year, int day);

        int getModuleColor(int module);
        int getActiveModule();
        void setActiveModule(int module);
        void itemModuleMoved(ToDoItem toDoItem, int oldModule, int newModule);

        void setActiveCategory(int position);
        int getActiveCategory();
        ArrayList<String> getCategoryNames();

        void openModulePage();
        void openListPage();
        void openEditBlock(ToDoItem toDoItem);
        void openCategoryEditBlock(Category category, int position);

        void closeRepeatPickerDialog();
        void closeDatePickerDialog();
        void closeTimePickerDialog();

        void addCategory(Category category);
        void updateCategory(Category category, int position);
        void removeCategory(int position);

        List<ToDoItem> getToDoData();
        void saveToDoData(List<ToDoItem> results);
        void saveToDoData();
        void clearToDoData();

        void setActionsBarMode(int mode);

        void toggleKeyboard();
        void hideKeyboard();

        void vibratePhone(int milliseconds);

    }

    public interface OnModuleListener {
        void entireModuleSelected(int module);
        void categoryInModuleSelected(int category);
    }

    public interface OnCategoryListener {

        void onCategorySelected(int position);
        void onCategoryScrolled (int position);

    }

    public interface OnListItemListener {

        void onToDoCheck(ToDoItem toDoItem, boolean checked);
        void onToDoClick(ToDoItem toDoItem);

    }

    public interface OnScrollSelectorListener {
        void scrollItemSelected(View view);
        void scrollToPosition(int position);
    }

    public interface SnapListener {
        void onItemSnapped(int position);
    }

    public interface EditInteraction {
        void clearFocus();
        void saveCurrentItemFields();
        void deleteCurrentItem();
        void setTime(int hourOfDay, int minute);
        void setRepeat(int repeatType);
    }

    public interface ListInteraction {
        void addItemToCategory(ToDoItem item, int category);
        void removeItemFromCategory(ToDoItem toDoItem, int category);
        void refresh();
    }

    public interface ListItemInteraction {
        void refresh();
    }

    public interface ModuleInteraction {
        void updateCategory(Category category);
        void refresh();
        void changeDate(int year, int month, int day);
    }

    public interface ModuleItemInteraction {

        void setDate(long date);
        void refresh();
    }

    public interface CategoryInteraction {
        void refresh();
    }

    public interface ScrollSelectorInteraction {
        void selectItem(int position);
    }

    public interface DateScrollerInteraction {
        void setInterval(int start, int end);
        void setModule(int module);
    }
}
