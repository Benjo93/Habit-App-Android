package com.example.habitapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Adapters.ScrollSelectorAdapter;
import com.example.habitapp.MainActivity;
import com.example.habitapp.Managers.ActionsBarManager;
import com.example.habitapp.R;
import com.example.habitapp.Utils.Habit;
import com.example.habitapp.Utils.ToDoItem;

import java.util.Calendar;

import static android.widget.CalendarView.*;
import static android.widget.CompoundButton.*;
import static com.example.habitapp.Managers.ToDoManager.*;

public class EditFragment extends Fragment implements
        EditInteraction,
        OnCheckedChangeListener,
        OnDateChangeListener,
        OnFocusChangeListener,
        OnScrollSelectorListener,
        View.OnClickListener {

    private MainListener mainListener;

    private ToDoItem toDoItem; // The item being edited.
    private boolean editing;

    private RecyclerView scrollingRecycler;
    private ScrollSelectorInteraction scrollSelectorInteraction;

    private EditText editTitleInput;
    private EditText editDetailsInput;

    private Button editButtonToday;
    private Button editButtonTomorrow;
    private Button editButtonWeek;

    // TODO move current date to main activity, accessible through a method call.
    private CalendarView editCalender;
    private Calendar selectedDate;
    private Calendar currentDate;
    private int year;
    private int month;
    private int day;

    private TextView editRepeatResult;
    private ImageButton editRepeatButton;

    private TextView editTimeResult;
    private ImageButton editReminderButton;

    private TextView editReminderResult;
    private boolean reminderIsOn;

    public static EditFragment newInstance(ToDoItem toDoItem) {
        EditFragment editFragment = new EditFragment();
        Bundle args = new Bundle();
        args.putParcelable("toDoItem", toDoItem);
        editFragment.setArguments(args);
        return editFragment;
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
            toDoItem = getArguments().getParcelable("toDoItem");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Category Scrolling Selector
        scrollingRecycler = view.findViewById(R.id.edit_category_scroller);
        final LinearSnapHelper snapHelper = new LinearSnapHelper();

        snapHelper.attachToRecyclerView(scrollingRecycler);
        scrollingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
                    if (state == RecyclerView.SCROLL_STATE_IDLE) {
                        View view = snapHelper.findSnapView(this);
                        scrollItemSnapped(view);
                    }
                }
        });

        ScrollSelectorAdapter scrollingAdapter = new ScrollSelectorAdapter(mainListener.getCategoryNames(), this, 140);
        scrollingRecycler.setAdapter(scrollingAdapter);
        scrollSelectorInteraction = scrollingAdapter;

        // --- Title, details, repeat.

        editTitleInput = view.findViewById(R.id.edit_title_input);
        editDetailsInput = view.findViewById(R.id.edit_details_input);

        editTitleInput.setOnFocusChangeListener(this);
        editDetailsInput.setOnFocusChangeListener(this);

        editButtonToday = view.findViewById(R.id.edit_button_today);
        editButtonTomorrow = view.findViewById(R.id.edit_button_tomorrow);
        editButtonWeek = view.findViewById(R.id.edit_button_week);

        editButtonToday.setOnClickListener(this);
        editButtonTomorrow.setOnClickListener(this);
        editButtonWeek.setOnClickListener(this);

        editCalender = view.findViewById(R.id.edit_calender);
        editCalender.setOnDateChangeListener(this);
        editCalender.setDateTextAppearance(R.style.CalendarDateTextAppearance);
        editCalender.setWeekDayTextAppearance(R.style.CalendarWeekTextAppearance);

        // Get the current day, month and year.
        currentDate = Calendar.getInstance();
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, month);
        currentDate.set(Calendar.DAY_OF_MONTH, day);

        selectedDate = currentDate;
        selectedDate.set(Calendar.DAY_OF_YEAR, mainListener.getActiveModule() + 1);

        //Switch editRepeatSwitch = view.findViewById(R.id.edit_repeat_switch);
        //editRepeatSwitch.setOnCheckedChangeListener(this);

        editRepeatResult = view.findViewById(R.id.edit_repeat_result);
        editRepeatButton = view.findViewById(R.id.edit_repeat_button);

        editTimeResult = view.findViewById(R.id.edit_time_result);
        editReminderResult = view.findViewById(R.id.edit_reminder_result);

        editReminderButton = view.findViewById(R.id.edit_reminder_button);
        editReminderButton.setOnClickListener(this);

        // Fill in the values of the edit item if editing an existing item.
        if (toDoItem.isActive()) {

            editing = true;

            scrollToPosition(toDoItem.getCategory()); // Set the category to the items category.

            editTitleInput.setText(toDoItem.getName());

            //editTitleInput.requestFocus();

            editDetailsInput.setText(toDoItem.getDetails());

            editCalender.setDate(toDoItem.getDueDate());

            if (toDoItem.isRepeat()) {
                reminderIsOn = true;
                editReminderButton.setImageResource(R.drawable.edit_reminder_button_on);
                editReminderResult.setText("On");
            }

            setRepeatResult(toDoItem.getRepeatType());


            // Check the items module and set the button selection accordingly.
            if (toDoItem.getModule() == mainListener.getCurrentDay()) {

                selectButtonToday();

            } else if (toDoItem.getModule() == mainListener.getCurrentDay() + 1) {

                selectButtonTomorrow();

            } else {

                onSelectedDayChange(
                        editCalender, selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH));
            }

        } else {

            // Scroll the the main active category.
            scrollToPosition(mainListener.getActiveCategory());

            editTitleInput.requestFocus();

            editCalender.setDate(selectedDate.getTimeInMillis());

            // Check the current main active module and set the button selection accordingly.
            if (mainListener.getActiveModule() == mainListener.getCurrentDay()) {

                selectButtonToday();

            } else if (mainListener.getActiveModule() == mainListener.getCurrentDay() + 1) {

                selectButtonTomorrow();

            } else {

                onSelectedDayChange(
                        editCalender, selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH));
            }
        }

        // TODO figure out how to check if the item is ready to be scrolled.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                scrollToPosition(mainListener.getActiveCategory());
            }
        }, 100);

    }

    private void scrollItemSnapped(View view) {

        // Called when the snap helper is triggered. Does not scroll to position.
        int position = scrollingRecycler.getChildLayoutPosition(view);

        scrollSelectorInteraction.selectItem(position); // Change the selected items color, size, etc.

        toDoItem.setCategory(position); // set the current items category.

    }

    public void scrollItemSelected(View view) {

        int position = scrollingRecycler.getChildLayoutPosition(view);

        // Select the item, making the item interact accordingly (text color change).
        scrollSelectorInteraction.selectItem(position);

        scrollingRecycler.smoothScrollToPosition(position); // Scroll to the selected category.

        toDoItem.setCategory(position); // set the current items category.
    }

    @Override
    public void scrollToPosition(int position) {

        // Select the item, making the item interact accordingly (text color change).
        scrollSelectorInteraction.selectItem(position);

        scrollingRecycler.smoothScrollToPosition(position); // Scroll to the selected category.

        toDoItem.setCategory(position); // set the current items category.
    }

    @Override
    public void clearFocus() { // Clear the title and details focus when the check is pressed.
        editTitleInput.clearFocus();
        editDetailsInput.clearFocus();
    }

    @Override
    public void saveCurrentItemFields() {

        // Set the main category so that the list can scroll to the new item.
        mainListener.setActiveCategory(toDoItem.getCategory());

        boolean moduleWasChanged = false;
        int oldModule = mainListener.getActiveModule();
        int newModule = mainListener.getActiveModule();

        if (toDoItem.isActive() && toDoItem.getModule() != selectedDate.get(Calendar.DAY_OF_YEAR) - 1) {
            oldModule = toDoItem.getModule();
            newModule = selectedDate.get(Calendar.DAY_OF_YEAR) - 1;
            moduleWasChanged = true;
        }

        // Fill in the To Do Item values.
        toDoItem.setName(editTitleInput.getText().toString());
        toDoItem.setDetails(editDetailsInput.getText().toString());
        toDoItem.setDayOfYearDue(selectedDate.get(Calendar.DAY_OF_YEAR));
        toDoItem.setDaysLeft(mainListener.getCurrentDay());

        // Set the current items module.
        toDoItem.setModule(toDoItem.getDayOfYearDue() - 1);

        if (!editing) { // If this is a new item.

            mainListener.addToDoItem(toDoItem); // Add the new To Do Item.
            mainListener.updateTodoItems(); // Refresh the items being displayed.

        } else { // If the item is being edited.
            if (moduleWasChanged) {
                mainListener.itemModuleMoved(toDoItem, oldModule, newModule);
                Toast.makeText(getContext(), "Item Moved", Toast.LENGTH_SHORT).show();
            }
        }

        // Start the module and scroll to the category of the new item.
        mainListener.startSpecificList(toDoItem.getModule(), toDoItem.getCategory());
    }

    @Override
    public void deleteCurrentItem () {
        mainListener.deleteToDoItem(toDoItem);
        mainListener.updateTodoItems();

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setTime(int hourOfDay, int minute) {
        editTimeResult.setText(String.format("%2d:%2d", hourOfDay % 12, minute));
    }

    @Override
    public void setRepeat(int repeatType) {
        toDoItem.setRepeatType(repeatType);
        setRepeatResult(repeatType);
    }

    private void setRepeatResult(int repeatType) {

        switch (repeatType) {

            case Habit.REPEAT_DAILY:
                editRepeatResult.setText(String.format("%s", "Item will repeat every day"));
                break;

            case Habit.REPEAT_WEEKLY:
                editRepeatResult.setText(String.format("%s", "Item will repeat every week"));
                break;

            case Habit.REPEAT_MONTHLY:
                editRepeatResult.setText(String.format("%s", "Item will repeat every month"));
                break;
        }
    }

    @Override
    public void onClick(@NonNull View v) {

        switch (v.getId()) {

            case R.id.edit_title_input:
                mainListener.setActionsBarMode(ActionsBarManager.EDIT_ACTIVE);
                break;

            case R.id.edit_button_today:
                selectButtonToday();
                break;

            case R.id.edit_button_tomorrow:
                selectButtonTomorrow();
                break;

            case R.id.edit_button_week:
                selectButtonWeekend();
                break;

            case R.id.edit_reminder_button:

                reminderIsOn = !reminderIsOn;

                if (reminderIsOn) {
                    editReminderResult.setText("On");
                    editReminderButton.setImageResource(R.drawable.edit_reminder_button_on);
                } else {
                    editReminderResult.setText("Off");
                    editReminderButton.setImageResource(R.drawable.edit_reminder_button);
                }

                //toDoItem.setRepeat(reminderIsOn);

                break;
        }
    }

    private void selectButtonToday () {

        editButtonToday.setTextColor(MainActivity.PRIMARY_COLOR);
        editButtonTomorrow.setTextColor(Color.WHITE);
        editButtonWeek.setTextColor(Color.WHITE);

        onSelectedDayChange(editCalender, year, month, day);
    }

    private void selectButtonTomorrow () {

        editButtonToday.setTextColor(Color.WHITE);
        editButtonTomorrow.setTextColor(MainActivity.PRIMARY_COLOR);
        editButtonWeek.setTextColor(Color.WHITE);

        int day = this.day;
        int month = this.month;
        int monthDuration = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Check if the next day goes past the current number of days in the current month.
        if (day++ > monthDuration) {
            day %= monthDuration;
            if (month++ > 12) month = 1;
        }

        onSelectedDayChange(editCalender, year, month, day);
    }

    private void selectButtonWeekend () {

        editButtonToday.setTextColor(Color.WHITE);
        editButtonTomorrow.setTextColor(Color.WHITE);
        editButtonWeek.setTextColor(MainActivity.PRIMARY_COLOR);

        int day = this.day;
        int month = this.month;
        int monthDuration = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Check the days left until the weekend.
        int daysTillWeekend =  7 - currentDate.get(Calendar.DAY_OF_WEEK);

        // If the current day is the weekend, set the due date to next weekend.
        if (daysTillWeekend == 0) daysTillWeekend = 7;

        day += daysTillWeekend;

        // If the day overlaps the month end, mod the day.
        if (day > monthDuration) {
            day %= monthDuration;

            // Cycle back around to the first month.
            if (month++ > 12) month = 1;
        }

        onSelectedDayChange(editCalender, year, month, day);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            toDoItem.setRepeat(true);
        } else {
            toDoItem.setRepeat(false);
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        editCalender.setDate(selectedDate.getTimeInMillis());

        toDoItem.setDayOfYearDue(selectedDate.get(Calendar.DAY_OF_YEAR));
        toDoItem.setDueDate(selectedDate.getTimeInMillis());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        // If the title or details are pressed, confirm mode ends and edit-active mode resumes.
        if (hasFocus && (v.getId() == R.id.edit_title_input || v.getId() == R.id.edit_details_input))
            mainListener.setActionsBarMode(ActionsBarManager.EDIT_ACTIVE);
    }
}
