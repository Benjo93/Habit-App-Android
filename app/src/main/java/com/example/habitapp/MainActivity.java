package com.example.habitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.habitapp.Fragments.ActionsBarFragment;
import com.example.habitapp.Fragments.CategoryEditFragment;
import com.example.habitapp.Fragments.DialogCalendarFragment;
import com.example.habitapp.Fragments.DialogRepeatFragment;
import com.example.habitapp.Fragments.EditFragment;
import com.example.habitapp.Fragments.ListFragment;
import com.example.habitapp.Fragments.ModuleFragment;
import com.example.habitapp.Fragments.TimePickerFragment;
import com.example.habitapp.Fragments.TopBarHomeFragment;
import com.example.habitapp.Fragments.TopBarModuleFragment;
import com.example.habitapp.Managers.ActionsBarManager;
import com.example.habitapp.Utils.Category;
import com.example.habitapp.Utils.Habit;
import com.example.habitapp.Utils.Module;
import com.example.habitapp.Utils.ToDoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.habitapp.Managers.ActionsBarManager.*;
import static com.example.habitapp.Managers.ToDoManager.*;
import static com.example.habitapp.Managers.TopBarManager.*;

public class MainActivity extends AppCompatActivity implements
        MainListener,
        ActionsBarListener{

    private int currentDay;
    private int currentYear;
    private Calendar currentDate;

    private ModuleInteraction moduleInteraction;
    private ListInteraction listInteraction;
    private EditInteraction editInteraction;

    private ActionsBarInteraction actionsBarInteraction;
    private TopBarInteraction topBarInteraction;

    private static String TODO_DATA = "DATA_TODO";
    private static String CATEGORY_DATA = "CATEGORY_DATA";
    private int APP_STATE = Habit.LIST;

    public static int PRIMARY_COLOR;

    // Theme colors.
    public static int RED_COLOR;
    public static int GREEN_COLOR;
    public static int BLUE_COLOR;
    public static int PURPLE_COLOR;

    private ConstraintLayout mainContainer;
    private ConstraintLayout editContainer;
    private ConstraintLayout topContainer;

    private ArrayList<ToDoItem> allToDoItems;

    private ArrayList<Module> modulesMain = new ArrayList<>();
    private int activeModule;

    private ArrayList<Category> categoriesMain = new ArrayList<>();
    private ArrayList<String> categoryNames;
    private int activeCategory;

    private boolean isKeyboardShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PRIMARY_COLOR = ContextCompat.getColor(this, R.color.colorAccent);

        RED_COLOR = ContextCompat.getColor(this, R.color.PrimaryRed);
        GREEN_COLOR = ContextCompat.getColor(this, R.color.PrimaryGreen);
        BLUE_COLOR = ContextCompat.getColor(this, R.color.PrimaryBlue);
        PURPLE_COLOR = ContextCompat.getColor(this, R.color.PrimaryPurple);

        //clearToDoData();

        // Current Day.
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1;
        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Toast.makeText(this, "Year, " + currentYear, Toast.LENGTH_SHORT).show();

        currentDate = Calendar.getInstance();

        activeModule = getCurrentDay();

        // Fetch the to do list and set the list,
        allToDoItems = getToDoData();

        mainContainer = findViewById(R.id.main_container);
        editContainer = findViewById(R.id.edit_container);
        topContainer = findViewById(R.id.top_bar_container);

        //clearCategoryData();
        categoriesMain = getCategoryData();

        // Create the modules and categories.
        getDays(100);
        partitionCategories();

        // Open the module page by default.
        openModulePage();

        startKeyboardListener(findViewById(android.R.id.content));
        if (isKeyboardShowing) toggleKeyboard();

    }

    @Override
    public int getCurrentDay() {
        return currentDay;
    }

    @Override
    public Calendar getCurrentDate () { return currentDate; }

    public void setCurrentYear (int year) { this.currentYear = year; }

    public int getCurrentYear() { return currentYear; }

    @Override
    public int getModuleColor(int module) {
        switch (module) {
            case 0: return MainActivity.RED_COLOR;
            case 1: return MainActivity.BLUE_COLOR;
            case 2: return MainActivity.GREEN_COLOR;
            case 3: return MainActivity.PURPLE_COLOR;
            default: return MainActivity.PRIMARY_COLOR;
        }
    }

    @Override
    public void startFragment(View container, Fragment fragment, String fragmentName) {

        if (container != null && fragment != null)

        getSupportFragmentManager()
                .beginTransaction()
                .replace(container.getId(), fragment, fragmentName)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getActiveModule() {
        return activeModule;
    }

    @Override
    public void setActiveModule(int module) { activeModule = module; }

    @Override
    public void itemModuleMoved(ToDoItem toDoItem, int oldModule, int newModule) {
        modulesMain.get(oldModule).items.remove(toDoItem);
        modulesMain.get(newModule).items.add(toDoItem);
    }

    @Override
    public void setActiveCategory(int position) {
        activeCategory = position;
    }

    @Override
    public int getActiveCategory() {
        return activeCategory;
    }

    @Override
    public ArrayList<String> getCategoryNames () {
        return categoryNames;
    }

    public void partitionCategories () {

        // Initially separate the categories.
        ArrayList<Category> categoryGroups = new ArrayList<>();

        for (int currentCategory = 0; currentCategory < categoriesMain.size(); currentCategory++) { // Loop through all categories.

            categoryGroups.add(new Category(categoriesMain.get(currentCategory).name, currentCategory, 123678)); // Create a new empty category.

            for (ToDoItem item : allToDoItems)  // Loop through all main items.

                if (item.getCategory() == currentCategory)  // Check if the item belongs in the new category.

                    categoryGroups.get(currentCategory).items.add(item);
        }

        this.categoriesMain.clear();
        this.categoriesMain.addAll(categoryGroups);

        // Create a list of category names for the category scroller.
        categoryNames = new ArrayList<>();

        // Add category names to pass into the edit fragment.
        for (Category category : categoryGroups) {
            categoryNames.add(category.name);
        }
    }

    @Override
    public void startSpecificList(int module, int category) {

        // Set the module to filter the list by.
        activeModule = module;

        // Set the category to filter the list by.
        activeCategory = category;

        // Open the list page, which requires a filtered list of items.
        openListPage();

    }

    // When the module is selected, each category should be filtered as to only show the items
    // within the selected module. Therefore, a new list is created given that specific module.
    @Override
    public ArrayList<Category> filterCategoriesByModule(int module) {

        // Make a new list of categories filtered by the active module.
        ArrayList<Category> result = new ArrayList<>();

        int index = 0;

        for (Category category : categoriesMain) { // Loop through all main categories.

            result.add(new Category(category.name, category.category, category.categoryID));

            result.get(index).setActive(true);

            for (ToDoItem item : category.items) // Loop through all items in the current category.

                if (item.getModule() == module) // Check which items match the active module.

                    result.get(index).items.add(item);

            index++;
        }

        // Adding the last blank category.
        result.add(new Category("", result.size() - 1, 2223345));

        return result;
    }

    @Override
    public ArrayList<Category> filterCategoriesByDate (int year, int day) {
        // Make a new list of categories filtered by the active module.
        ArrayList<Category> result = new ArrayList<>();

        int index = 0;

        for (Category category : categoriesMain) { // Loop through all main categories.

            result.add(new Category(category.name, category.category, category.categoryID));

            result.get(index).setActive(true);

            for (ToDoItem item : category.items) // Loop through all items in the current category.

                if (item.getDayOfYearDue() == year && item.getDayOfYearDue() == day) // Check which items match the active module.

                    result.get(index).items.add(item);

                index++;
        }

        // Adding the last blank category.
        result.add(new Category("", result.size() - 1, 2223345));

        return result;
    }

    //
    //
    //
    //
    //
    //
    //

    public void openTopBarListMode() {

        TopBarModuleFragment topBarModuleFragment = TopBarModuleFragment.newInstance(modulesMain.get(getActiveModule()));
        topBarInteraction = topBarModuleFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_bar_container, topBarModuleFragment, "TOP_BAR_LIST_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    public void closeTopBarList() {

        TopBarModuleFragment topBarModuleFragment = (TopBarModuleFragment) getSupportFragmentManager().findFragmentByTag("TOP_BAR_LIST_FRAGMENT");

        if (topBarModuleFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(topBarModuleFragment)
                    .commit();
    }

    public void openTopBarHome() {

        TopBarHomeFragment topBarHomeFragment = TopBarHomeFragment.newInstance();
        topBarInteraction = topBarHomeFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_bar_container, topBarHomeFragment, "TOP_BAR_HOME_FRAGMENT")
                .addToBackStack(null)
                .commit();

        findViewById(R.id.top_bar_container).setVisibility(View.VISIBLE);

    }

    public void closeTopBarHome() {

        TopBarHomeFragment topBarHomeFragment = (TopBarHomeFragment) getSupportFragmentManager().findFragmentByTag("TOP_BAR_HOME_FRAGMENT");
        if (topBarHomeFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(topBarHomeFragment)
                    .commit();

        findViewById(R.id.top_bar_container).setVisibility(View.GONE);
    }

    // If the actions bar doesn't need to be set right away, it will be sent to default mode.
    public void openActionsBar() {

        ActionsBarFragment actionsBarFragment = ActionsBarFragment.newInstance();
        actionsBarInteraction = actionsBarFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actions_bar_container, actionsBarFragment, "ACTIONS_BAR_FRAGMENT")
                .addToBackStack(null)
                .commit();

        //findViewById(R.id.actions_bar_container).setVisibility(View.VISIBLE);
    }

    // Explicitly set the actions bar mode when starting the fragment.
    public void openActionsBar(int MODE) {

        ActionsBarFragment actionsBarFragment = ActionsBarFragment.newInstance(MODE);
        actionsBarInteraction = actionsBarFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actions_bar_container, actionsBarFragment, "ACTIONS_BAR_FRAGMENT")
                .addToBackStack(null)
                .commit();

    }

    public void closeActionsBar() {

        ActionsBarFragment actionsBarFragment = (ActionsBarFragment) getSupportFragmentManager().findFragmentByTag("ACTIONS_BAR_FRAGMENT");

        if (actionsBarFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(actionsBarFragment)
                    .commit();

        //findViewById(R.id.actions_bar_container).setVisibility(View.GONE);

    }

    @Override
    public void openModulePage() {
        APP_STATE = Habit.MODULE;

        // Animate layout change.
        if (!Habit.EDITING_ACTIVE) slideFromLeft(mainContainer);
        if (isKeyboardShowing) toggleKeyboard();

        ModuleFragment moduleFragment = ModuleFragment.newInstance(modulesMain, filterCategoriesByModule(getActiveModule()));
        moduleInteraction = moduleFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, moduleFragment, "MODULE_FRAGMENT")
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .addToBackStack("MODULE_FRAGMENT")
                .commit();

        closeActionsBar();
        closeTopBarList();

        ViewGroup.LayoutParams params = topContainer.getLayoutParams();
        params.height = 0;
        topContainer.setLayoutParams(params);
    }

    @Override
    public void openListPage() {
        APP_STATE = Habit.LIST;

        // Animate layout changes.
        if (!Habit.EDITING_ACTIVE) slideFromRight(mainContainer);
        else Habit.EDITING_ACTIVE = false;

        if (isKeyboardShowing) toggleKeyboard();

        ListFragment listFragment = ListFragment.newInstance(filterCategoriesByModule(getActiveModule()));
        listInteraction = listFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, listFragment, "LIST_FRAGMENT")
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .addToBackStack("LIST_FRAGMENT")
                .commit();

        openActionsBar(DEFAULT);
//        actionsBarInteraction.setActionsBarMode(ActionsBarManager.DEFAULT);

        openTopBarListMode();

        ViewGroup.LayoutParams params = topContainer.getLayoutParams();
        params.height = 188;
        topContainer.setLayoutParams(params);
    }

    @Override
    public void openEditBlock(ToDoItem toDoItem) {

        slideFromBottom(editContainer);

        if (toDoItem.isActive()) {
            Habit.EDITING_ACTIVE = true;
            Habit.EDITING_CONFIRM = true;
            actionsBarInteraction.setActionsBarMode(EDIT_CONFIRM);
        } else {
            if (!isKeyboardShowing) toggleKeyboard();
            Habit.EDITING_ACTIVE = true;
            Habit.EDITING_CONFIRM = false;
            actionsBarInteraction.setActionsBarMode(EDIT_ACTIVE);
        }

        EditFragment editFragment = EditFragment.newInstance(toDoItem);
        editInteraction = editFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_container, editFragment, "EDIT_FRAGMENT")
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right /*, R.anim.enter_from_right, R.anim.exit_to_left*/)
                //.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null)
                .commit();

        switch (APP_STATE) {
            case Habit.MODULE:
                closeTopBarHome();
                break;
            case Habit.LIST:
                closeTopBarList();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + APP_STATE);
        }

    }

    public void closeEditBlock() {

        Habit.EDITING_CONFIRM = false;

        if (isKeyboardShowing) toggleKeyboard();

        EditFragment editFragment = (EditFragment) getSupportFragmentManager().findFragmentByTag("EDIT_FRAGMENT");

        if (editFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(editFragment)
                    .commit();

        switch (APP_STATE) {

            case Habit.MODULE:

                assert moduleInteraction != null;
                openTopBarHome();
                moduleInteraction.refresh();
                break;

            case Habit.LIST:

                assert listInteraction != null;
                openTopBarListMode();
                listInteraction.refresh();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + APP_STATE);
        }

        // Actions bar should stay open, but the mode should be set back to default since it always goes to its list.
        actionsBarInteraction.setActionsBarMode(ActionsBarManager.DEFAULT);

    }

    @Override
    public void openCategoryEditBlock(Category category, int position) {

        // Both active and confirm are true so that a category change requires only one click.
        Habit.EDITING_ACTIVE = true;
        Habit.EDITING_CONFIRM = true;

        if (!isKeyboardShowing) toggleKeyboard();

        CategoryEditFragment categoryEditFragment = CategoryEditFragment.newInstance(category, position,this);
        editInteraction = categoryEditFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_container, categoryEditFragment, "CATEGORY_EDIT_FRAGMENT")
                .addToBackStack(null)
                .commit();

        // Actions bar needs to be open since this is only ever accessed from the module page.
        openActionsBar(EDIT_CONFIRM);
        actionsBarInteraction.setActionsBarMode(EDIT_CONFIRM);

    }

    public void closeCategoryEditBlock() {

        Habit.EDITING_ACTIVE = false;

        if (isKeyboardShowing) toggleKeyboard();

        CategoryEditFragment categoryEditFragment = (CategoryEditFragment) getSupportFragmentManager().findFragmentByTag("CATEGORY_EDIT_FRAGMENT");

        if (categoryEditFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(categoryEditFragment)
                    .commit();

        // Actions bar should be closed since this always leads back to the module page.
        closeActionsBar();

    }

    @Override
    public long getMillisecondFromDay(int dayOfYear) {

        Calendar calendar = Calendar.getInstance();

        int month = 0;
        calendar.set(Calendar.MONTH, month);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        while (dayOfYear > daysInMonth) { // If the day of year is not within the current month.

            dayOfYear -= daysInMonth; // Subtract the current month length.

            month++; // Go to the next month.

            calendar.set(Calendar.MONTH, month); // Set the calendar to the next month.

            daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Get the next length of days.
        }

        calendar.set(getCurrentDate().get(Calendar.YEAR), month, dayOfYear + 1);

        return calendar.getTimeInMillis();
    }

    private void getDays (int radius) {

        ArrayList<Module> dates = new ArrayList<>();

       for (int i = 0; i < Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR); i++) {
           dates.add(new Module("January 1", getModuleName(i), i));
           Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.DAY_OF_YEAR, i + 1);
           dates.get(i).dueDate = calendar.getTimeInMillis();
       }

        modulesMain.clear();
        modulesMain.addAll(dates);

        // Partition the items into their modules.
        for (ToDoItem item : allToDoItems)
            modulesMain.get(item.getModule()).items.add(item);
    }

    private String getModuleName(int day) {

        if (day >= getCurrentDay()) { // Present to future days.

            if (day == getCurrentDay()) return "Today";
            else if (day == getCurrentDay() +  1) return "Tomorrow";
            else if (day < getCurrentDay() + 7 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) return "This Week";
            else if (day < getCurrentDay() + Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
                    - Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) return "This Month";
            else if (day < getCurrentDay() + Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)
                    - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) return "This Year";

        } else { // Past days.

            if (day == getCurrentDay() -  1) return "Yesterday";
            else if (day > getCurrentDay() - 7 + Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) return "Last Week";
            else if (day > getCurrentDay() - Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
                    + Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) return "Last Month";
            else if (day > getCurrentDay() - Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)
                + Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) return "Last Year";

        }

        return "Module Name";
    }

    //
    //
    //
    //
    //
    //
    // Animations.

    private void slideFromLeft (View view) {

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        view.setTranslationX(-100);
        view.animate().translationX(0).setDuration(200).start();

    }

    private void slideFromRight (View view) {

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        view.setTranslationX(100);
        view.animate().translationX(0).setDuration(200).start();

    }

    private void slideFromBottom (View view) {

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        view.setTranslationY(100);
        view.animate().translationY(0).setDuration(300).start();

    }

    private void slideFromTop (View view) {

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        view.setTranslationY(-100);
        view.animate().translationY(0).setDuration(300).start();

    }

    //
    //
    //
    //
    //
    //
    // Manage To Do Items.

    @Override
    public void addToDoItem(ToDoItem toDoItem) {

        if (!toDoItem.getName().equals("")) { // If there is at least an item title.

            allToDoItems.add(toDoItem); // Save to the main list.

            modulesMain.get(toDoItem.getModule()).items.add(toDoItem); // Save to the modules list.

            categoriesMain.get(toDoItem.getCategory()).items.add(toDoItem); // Save to the main categories list.

            if (APP_STATE == Habit.LIST && toDoItem.getModule() == activeModule) { // If the list page is open behind the edit block.

                listInteraction.addItemToCategory(toDoItem, toDoItem.getCategory()); // Add to local categories list.
                listInteraction.refresh();
            }
            toDoItem.setActive(true);
        }
        saveToDoData(allToDoItems); // Save to do items.
    }

    @Override
    public void deleteToDoItem(ToDoItem toDoItem) {

        allToDoItems.remove(toDoItem); // Remove from main list.

        modulesMain.get(toDoItem.getModule()).items.remove(toDoItem); // Remove from modules list.

        categoriesMain.get(toDoItem.getCategory()).items.remove(toDoItem); // Remove from main categories list.

        if (APP_STATE == Habit.LIST && listInteraction != null) {

            listInteraction.removeItemFromCategory(toDoItem, toDoItem.getCategory()); // Remove from local categories list.
            listInteraction.refresh();
        }
        saveToDoData(allToDoItems);
    }

    @Override
    public void updateTodoItems () {

        if (listInteraction != null) {
            listInteraction.refresh(); // Updates the list fragments adapter.
        }
    }

    @Override
    public void updateTopBar () {

        if (topBarInteraction != null) {
            topBarInteraction.refresh();
        }
    }

    @Override
    public List<ToDoItem> getTodoItems() { return allToDoItems; }

    //
    //
    //
    //
    //
    //
    // Manage Categories.

    @Override
    public void addCategory(Category category) {

        if (!category.name.equals("")) {

            category.setActive(true);

            categoriesMain.add(category);

            categoryNames.add(category.name);

            moduleInteraction.refresh();

            saveCategoryData(categoriesMain);

        }
    }

    @Override
    public void updateCategory(Category category, int position) {

        categoryNames.remove(position);

        categoryNames.add(position, category.name);

        categoriesMain.get(position).name = category.name;

        moduleInteraction.refresh();

        saveCategoryData(categoriesMain);

    }

    public void removeCategory(int position) {

        categoryNames.remove(position);

        categoriesMain.remove(position);

        moduleInteraction.refresh();

        saveCategoryData(categoriesMain);
    }

    //
    //
    //
    //
    //
    //
    // Read and Write Items.

    @Override
    public ArrayList<ToDoItem> getToDoData () {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        ArrayList<ToDoItem> results;

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(TODO_DATA, "");
        Type type = new TypeToken<List<ToDoItem>>(){}.getType();
        results = gson.fromJson(json, type);

        if (results == null) results = new ArrayList<>();

        saveToDoData(results);
        return results;
    }

    @Override
    public void saveToDoData(List<ToDoItem> results) {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(results);
        prefsEditor.putString(TODO_DATA, json);
        prefsEditor.apply();
    }

    @Override
    public void saveToDoData() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allToDoItems);
        prefsEditor.putString(TODO_DATA, json);
        prefsEditor.apply();
    }

    @Override
    public void clearToDoData () {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(new ArrayList<ToDoItem>());
        prefsEditor.putString(TODO_DATA, json);
        prefsEditor.apply();
    }

    private ArrayList<Category> getCategoryData() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        ArrayList<Category> results;

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(CATEGORY_DATA, "");
        Type type = new TypeToken<List<Category>>(){}.getType();
        results = gson.fromJson(json, type);

        if (results == null) results = new ArrayList<>();

        return results;
    }

    private void saveCategoryData(List<Category> results) {

        //results.remove(results.size() - 1);

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(results);
        prefsEditor.putString(CATEGORY_DATA, json);
        prefsEditor.apply();
    }

    private void clearCategoryData() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(new ArrayList<Category>());
        prefsEditor.putString(CATEGORY_DATA, json);
        prefsEditor.apply();
    }

    //
    //
    //
    //
    //
    //
    //

    @Override
    public void setActionsBarMode(int mode) { // Called when the edit blocks title or details are focused.
        actionsBarInteraction.setActionsBarMode(mode);
        Habit.EDITING_CONFIRM = false;
    }

    @Override
    public void onActionsBarAffirm() {

        if (Habit.EDITING_ACTIVE) { // The user is currently editing an item.

            // Since the affirm button is pressed...
            // Clear the focus on title or details text which triggers the action bar change.
            editInteraction.clearFocus();

            if (isKeyboardShowing) toggleKeyboard();

            if (Habit.EDITING_CONFIRM) { // The user is done with the name and details and is picking the date, time, etc.

                vibratePhone(10);

                // If the user is in edit-confirm mode when affirm is pressed
                // the item is saved and the edit block is closed.
                closeEditBlock();
                closeCategoryEditBlock();

                // Save all of the fields. Which calls the addToDoItem method.
                editInteraction.saveCurrentItemFields();

            } else {

                // The title and details are confirmed and the confirm-mode is started.
                Habit.EDITING_CONFIRM = true;
                actionsBarInteraction.setActionsBarMode(EDIT_CONFIRM);
            }

        } else {

            // If the user is not currently editing an item, open edit mode.
            openEditBlock(new ToDoItem());

        }
    }

    @Override
    public void onActionsBarReject() {

        if (Habit.EDITING_ACTIVE) {

            // If the user is editing an item when reject is pressed
            // the item is deleted and the edit block is closed.
            editInteraction.deleteCurrentItem();

            if (APP_STATE == Habit.MODULE) {
                closeCategoryEditBlock();
            }
            if (APP_STATE == Habit.LIST) {
                closeEditBlock();
            }

            Habit.EDITING_ACTIVE = false;
            Habit.EDITING_CONFIRM = false;

        } else if (APP_STATE == Habit.LIST) {
            openModulePage();
        }
    }

    @Override
    public void onActionsBarToggle() {

        Toast.makeText(this, "Toggle", Toast.LENGTH_SHORT).show();

    }

    //
    //
    //
    //
    //
    //
    //

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!Habit.EDITING_ACTIVE) {
            openModulePage();
        } else {
            closeEditBlock();
        }
    }

    private void startKeyboardListener(final View contentView) {

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) isKeyboardShowing = true;
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) isKeyboardShowing = false;
                        }
                    }
                }
            );
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && isKeyboardShowing) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    @Override
    public void toggleKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void vibratePhone(int milliseconds) {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert vibrator != null;
            vibrator.vibrate(VibrationEffect
                    .createWaveform(
                            new long[]{milliseconds},
                            new int[] {100},
                            -1));
        } else {
            assert vibrator != null;
            vibrator.vibrate(milliseconds);
        }
    }

    //
    //
    //
    //
    //
    //
    //

    private void animateCollapseView(final View view, boolean condense, int collapseTo, int expandTo) {

        ValueAnimator anim = ValueAnimator.ofInt(condense ? expandTo : collapseTo, condense ? collapseTo : expandTo);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(300);
        anim.start();

    }

    public void showRepeatPickerDialog(View v) {
        DialogFragment dialogFragment = DialogRepeatFragment.newInstance(editInteraction);
        dialogFragment.show(getSupportFragmentManager(), "repeatPicker");
    }

    public void closeRepeatPickerDialog() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("repeatPicker");
        if (dialogFragment != null) getSupportFragmentManager().beginTransaction().remove(dialogFragment).commit();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment dialogFragment = new TimePickerFragment(editInteraction);
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void closeTimePickerDialog() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("timePicker");
        if (dialogFragment != null) getSupportFragmentManager().beginTransaction().remove(dialogFragment).commit();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment dialogFragment = DialogCalendarFragment.newInstance(moduleInteraction);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void closeDatePickerDialog() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("datePicker");
        if (dialogFragment != null) getSupportFragmentManager().beginTransaction().remove(dialogFragment).commit();
    }
}
