package com.example.habitapp.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.habitapp.Adapters.CategoryGridAdapter;
import com.example.habitapp.Adapters.DateScrollerAdapter;
import com.example.habitapp.Adapters.ModuleAdapter;
import com.example.habitapp.Utils.Category;
import com.example.habitapp.Utils.Module;
import com.example.habitapp.R;
import com.example.habitapp.Utils.SnapHelperOneByOne;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.habitapp.Managers.ToDoManager.*;

public class ModuleFragment extends Fragment implements
        OnModuleListener,
        SnapListener,
        ModuleInteraction,
        OnScrollSelectorListener,
        View.OnClickListener {

    private MainListener mainListener;

    private DateScrollerInteraction dateScrollerInteraction;
    private ModuleItemInteraction moduleItemInteraction;

    private ArrayList<Module> modulesLocal;
    private ArrayList<Category> categoriesLocal;

    private RecyclerView moduleRecycler;
    private ModuleAdapter moduleAdapter;

    private CategoryGridAdapter categoryGridAdapter;

    private boolean arrowWasClicked = false;
    private int moduleBeingViewed;

    private RecyclerView dateScrollingRecycler;

    public ModuleFragment() {}

    public static ModuleFragment newInstance(ArrayList<Module> modules, ArrayList<Category> categories) {
        ModuleFragment fragment = new ModuleFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("modules", modules);
        args.putParcelableArrayList("categories", categories);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modulesLocal = getArguments().getParcelableArrayList("modules");
            categoriesLocal = getArguments().getParcelableArrayList("categories");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_module, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moduleBeingViewed = mainListener.getActiveModule();

        // Module Recycler.

        moduleRecycler = view.findViewById(R.id.module_recycler);
        RecyclerView moduleCategoryRecycler = view.findViewById(R.id.category_recycler);

        final SnapHelperOneByOne moduleSnapHelper = new SnapHelperOneByOne(this);
        moduleSnapHelper.attachToRecyclerView(moduleRecycler);
        moduleRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
                if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = moduleSnapHelper.findSnapView(this);
                    moduleBeingViewed = moduleRecycler.getChildLayoutPosition(view);

                    if (!arrowWasClicked) moduleChanged(moduleBeingViewed);
                    else arrowWasClicked = false;
                }
            }
        });

        moduleAdapter = new ModuleAdapter(modulesLocal, mainListener, this);
        moduleItemInteraction = moduleAdapter;
        moduleRecycler.setAdapter(moduleAdapter);

        // Day Recycler.

        dateScrollingRecycler = view.findViewById(R.id.day_scrolling_recycler);
        final SnapHelperOneByOne daySnapHelper = new SnapHelperOneByOne(this);

        daySnapHelper.attachToRecyclerView(dateScrollingRecycler);
        dateScrollingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
                if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = daySnapHelper.findSnapView(this);
                    scrollToPosition(dateScrollingRecycler.getChildLayoutPosition(view));
                }
            }
        });

        // Setting the padding, linear snap helper and setting the adapter.
        int padding = (Resources.getSystem().getDisplayMetrics().widthPixels/2) -
                51 * getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;

        dateScrollingRecycler.setPadding(padding, 0, padding, 0);

        DateScrollerAdapter dateScrollerAdapter = new DateScrollerAdapter(getScrollingDays(), this, mainListener);
        dateScrollerInteraction = dateScrollerAdapter;
        dateScrollingRecycler.setAdapter(dateScrollerAdapter);

        dateScrollingRecycler.scrollToPosition(mainListener.getCurrentDay() - 1);

        // Left and Right module arrows.

        ImageButton moduleLeftButton = view.findViewById(R.id.module_left_button);
        ImageButton moduleRightButton = view.findViewById(R.id.module_right_button);

        moduleLeftButton.setOnClickListener(this);
        moduleRightButton.setOnClickListener(this);

        moduleCategoryRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //moduleCategoryRecycler.setNestedScrollingEnabled(false);
        categoryGridAdapter = new CategoryGridAdapter(categoriesLocal, this, mainListener);
        moduleCategoryRecycler.setAdapter(categoryGridAdapter);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                moduleBeingViewed = mainListener.getActiveModule();
                goToModule(mainListener.getActiveModule());
                moduleChanged(mainListener.getActiveModule());
                categoryGridAdapter.notifyDataSetChanged();
            }
        }, 1);
    }

    private void goToModule(int position) {
        moduleRecycler.scrollToPosition(position);
        dateScrollingRecycler.scrollToPosition(position);
        refresh();
    }

    @Override
    public void onItemSnapped(int position) {
        //moduleChanged(position);
        //scrollToPosition(position);
    }

    @Override
    public void entireModuleSelected(int module) {
        // Go to the first category of the selected module.
        mainListener.startSpecificList(module, 0);
    }

    @Override
    public void categoryInModuleSelected (int category) {
        // Go to the selected category at the active module.
        mainListener.startSpecificList(mainListener.getActiveModule(), category);
    }

    private void scrollToModule(int module) {
        moduleRecycler.smoothScrollToPosition(module);
        dateScrollingRecycler.smoothScrollToPosition(module);
        mainListener.setActiveModule(module);
        refresh();
    }

    @Override
    public void scrollItemSelected(View view) {
        int position = dateScrollingRecycler.getChildLayoutPosition(view);
        scrollToModule(position);
        scrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int day) {
        dateScrollingRecycler.smoothScrollToPosition(day);
    }

    private void moduleChanged(int module) {

        scrollToModule(module);

        dateScrollerInteraction.setInterval(module, module);
        dateScrollerInteraction.setModule(module);
        moduleItemInteraction.setDate(getMillisecondOfDay(module));

    }

    private long getMillisecondOfDay(int dayOfYear) {

        // TODO check if a new year has been selected.

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

        // Day of year is not the remainder; the day of month.

        calendar.set(mainListener.getCurrentDate().get(Calendar.YEAR), month, dayOfYear + 1); // Set the calender to the resulting day.

        return calendar.getTimeInMillis();
    }

    private ArrayList<String> getScrollingDays () {

        // TODO set scrolling days to an interval of N days on both sides of the current date.

        ArrayList<String> scrollItems = new ArrayList<>();

        int count = 1;

        YearMonth yearMonth;

        while (count <= 12) {

            yearMonth = Year.now().atMonth(count);

            for (int d = 1; d <= yearMonth.lengthOfMonth(); d++)

                    scrollItems.add(String.format("%s", d));

            count++;

        }

        return scrollItems;
    }

    //
    //
    //

    @Override
    public void updateCategory(Category category) { }

    @Override
    public void refresh() {

        categoriesLocal.clear();
        categoriesLocal.addAll(mainListener.filterCategoriesByModule(mainListener.getActiveModule()));

        moduleAdapter.notifyDataSetChanged();
        categoryGridAdapter.notifyDataSetChanged();

    }

    @Override
    public void changeDate(int year, int month, int day) {
        Toast.makeText(getContext(), "Day has changed", Toast.LENGTH_SHORT).show();

        int days = 0;
        Calendar calendar = Calendar.getInstance();

        for (int i=0; i < month; i++) {
            days += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        days += day;

        moduleChanged(days + 1);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.module_left_button:
                arrowWasClicked = true;
                if (moduleBeingViewed > 0) moduleChanged(moduleBeingViewed - 1);
                break;

            case R.id.module_right_button:
                arrowWasClicked = true;
                if (moduleBeingViewed < modulesLocal.size()) moduleChanged(moduleBeingViewed + 1);
                break;
        }
    }
}
