package com.mrbreak.todo.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.google.gson.Gson;
import com.mrbreak.todo.R;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.events.BackPressedFinished;
import com.mrbreak.todo.events.DisplayFragmentFinished;
import com.mrbreak.todo.events.DisplayFragmentStarted;
import com.mrbreak.todo.events.FilterTodos;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.fragments.CalendarFragment;
import com.mrbreak.todo.fragments.DashBoardFragment;
import com.mrbreak.todo.fragments.ToDoDoneFragment;
import com.mrbreak.todo.fragments.ToDoListFragment;
import com.mrbreak.todo.fragments.ToDoOverDueFragment;
import com.mrbreak.todo.jobmanager.ToDoJobManager;
import com.mrbreak.todo.jobs.DisplayFragmentJob;
import com.mrbreak.todo.jobs.GetToDoJob;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.SharedPrefUtil;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ToDoJobManager toDoJobManager;
    private JobManager jobManager;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private List<ToDo> toDos;
    private boolean executeOnFirstLoad = true;
    private TextView todoTextView;
    private TextView doneTextView;
    private TextView overDueTextView;
    private FrameLayout badgeCountFrameLayout;
    private TextView badgeCountOverdue;
    private List<ToDo> overdueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder()
                    // .migration(new MyRealmMigration())
                    .deleteRealmIfMigrationNeeded()
                    .name(Constants.DATABASE_NAME)
                    .schemaVersion(2)
                    .build();

            Realm.setDefaultConfiguration(config);

            mDrawerLayout = findViewById(R.id.drawer_layout);

            badgeCountFrameLayout = findViewById(R.id.badgeCountFrameLayout);
            badgeCountOverdue = findViewById(R.id.badgeCountOverdue);

            todoTextView = findViewById(R.id.toDo);
            doneTextView = findViewById(R.id.done);
            overDueTextView = findViewById(R.id.overDue);

            todoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDoClick();
                }
            });

            doneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doneClick();
                }
            });

            overDueTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overDueClick();
                }
            });

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }

            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();
                            switch (menuItem.getItemId()) {
                                case R.id.dashboard:
                                    SharedPrefUtil.saveCurrentFragment(getBaseContext(),
                                            Constants.ZERO_INT);
                                    openDashBoard();
                                    return true;

                                case R.id.calendar:
                                    SharedPrefUtil.saveCurrentFragment(getBaseContext(),
                                            Constants.ZERO_INT);
                                    openCalendar();
                                    return true;

                                case R.id.share:
                                    shareApp();
                                    return true;

                                case R.id.rateUs:
//                                    SharedPrefUtil.saveCurrentFragment(getBaseContext(),
//                                            Constants.ZERO_INT);
                                    rateUs();
                                    return true;
                            }

                            return true;
                        }
                    });

            mDrawerLayout.addDrawerListener(
                    new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            // Respond when the drawer's position changes
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                            // Respond when the drawer is opened
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            // Respond when the drawer is closed
                            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                                navigationView.getMenu().getItem(i).setChecked(false);
                            }
                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            // Respond when the drawer motion state changes
                        }
                    }
            );

            setStatusBarColor();
            if (executeOnFirstLoad) {
                displayActionBar(true);
                executeOnFirstLoad = false;
                toDoClick();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //SearchFragment.this.onQueryTextChange(s);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        badgeCountOverdue.setVisibility(View.GONE);
        badgeCountFrameLayout.setVisibility(View.GONE);

        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));

        if (toDoJobManager == null) {
            toDoJobManager = new ToDoJobManager();
        }

        if (jobManager == null) {
            jobManager = toDoJobManager.getJobManager(getApplicationContext());
        }

        if (SharedPrefUtil.getCurrentFragment(getApplicationContext()) == Constants.ZERO_INT) {
            return;
        }

        if (SharedPrefUtil.getCurrentFragment(getApplicationContext()) == Constants.TODO_LIST) {
            setFragment(new ToDoListFragment());
        } else if (SharedPrefUtil.getCurrentFragment(getApplicationContext()) == Constants.DONE_LIST) {
            setFragment(new ToDoDoneFragment());
        } else if (SharedPrefUtil.getCurrentFragment(getApplicationContext()) == Constants.OVER_DUE_LIST) {
            setFragment(new ToDoOverDueFragment());
        }
    }

    private void displayActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_filter:
                EventBus.getDefault().post(new FilterTodos());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getName();
        if (fragmentName.contains(ToDoDoneFragment.class.getName()) ||
                fragmentName.contains(ToDoListFragment.class.getName()) ||
                fragmentName.contains(ToDoOverDueFragment.class.getName())) {
            finish();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            displayActionBar(true);
        } else {
            finish();
        }
    }

    @Subscribe
    public void onEvent(DisplayFragmentStarted event) {
        jobManager = toDoJobManager.getJobManager(getApplicationContext());
        jobManager.addJobInBackground(new DisplayFragmentJob(EventBus.getDefault(), event.getFragment()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BackPressedFinished e) {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DisplayFragmentFinished e) {
        setFragment(e.getFragment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetToDosFinished event) {
        if (event != null && event.getToDoList() != null && !event.getToDoList().isEmpty()) {
            overdueList = Utils.getOverDueList(event.getToDoList());
            if (overdueList != null && !overdueList.isEmpty()) {
                badgeCountFrameLayout.setVisibility(View.VISIBLE);
                badgeCountOverdue.setVisibility(View.VISIBLE);
                String badgeCount = "" + overdueList.size();
                badgeCountOverdue.setText(badgeCount);
            }
        }
    }

    @Subscribe
    public void onEvent(GetToDosStarted event) {
        jobManager.addJobInBackground(new GetToDoJob(EventBus.getDefault()));
    }

    private void toDoClick() {
        setFragment(new ToDoListFragment());
        setBackGroundDrawable(1, todoTextView);
        SharedPrefUtil.saveCurrentFragment(getApplicationContext(), Constants.TODO_LIST);
    }

    private void doneClick() {
        setFragment(new ToDoDoneFragment());
        setBackGroundDrawable(2, doneTextView);
        SharedPrefUtil.saveCurrentFragment(getApplicationContext(), Constants.DONE_LIST);

    }

    private void overDueClick() {
        setFragment(new ToDoOverDueFragment());
        setBackGroundDrawable(3, overDueTextView);
        SharedPrefUtil.saveCurrentFragment(getApplicationContext(), Constants.OVER_DUE_LIST);
    }

    private void setBackGroundDrawable(int selectedItem, TextView textView) {
        clearHighlightedTextView(todoTextView);
        todoTextView.setBackground(getResources().getDrawable(R.drawable.todo_header_background));
        clearHighlightedTextView(doneTextView);
        doneTextView.setBackground(getResources().getDrawable(R.drawable.done_new_header_background));
        clearHighlightedTextView(overDueTextView);
        overDueTextView.setBackground(getResources().getDrawable(R.drawable.over_due_header_background));

        HighlightTextView(textView, selectedItem);
    }

    private void clearHighlightedTextView(TextView textView) {
        textView.setTypeface(Typeface.DEFAULT);
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    private void HighlightTextView(TextView textView, int selectedHeader) {
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.teal));
        if (selectedHeader == 1) {
            textView.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.todo_selected_header_background));
        } else if (selectedHeader == 2) {
            textView.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.done_selected_header_background));
        } else {
            textView.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.over_due_selected_header_background));
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at:" +
                        " https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    private void openCalendar() {
        Intent intent = new Intent(getBaseContext(), CalendarActivity.class);
        intent.putExtra(Constants.LIST, getBundle());
        displayActivity(intent);
    }


    private void openDashBoard() {
        Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
        intent.putExtra(Constants.LIST, getBundle());
        displayActivity(intent);
    }

    private void rateUs() {
    }


    public void displayActionBar(boolean isVisiBleActionBar) {
        if (isVisiBleActionBar) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void setFragment(Fragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        if (fragment.getClass().getName().contains(CalendarFragment.class.getName()) ||
                fragment.getClass().getName().contains(DashBoardFragment.class.getName()) ||
                fragment.getClass().getName().contains(ToDoDoneFragment.class.getName())) {
            fragment.setArguments(getBundle());
        }

        fragmentTransaction.replace(R.id.fragmentFrameLayout, fragment).setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        if (fragment.getClass().getName().contains(ToDoListFragment.class.getName()) ||
                fragment.getClass().getName().contains(ToDoDoneFragment.class.getName()) ||
                fragment.getClass().getName().contains(ToDoOverDueFragment.class.getName())) {
            displayActionBar(true);
        } else {
            displayActionBar(false);
        }
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIST, new Gson().toJson(toDos));
        return bundle;
    }
}
