package com.mrbreak.todo.view.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.mrbreak.todo.BuildConfig;
import com.mrbreak.todo.R;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.view.MainActivityCallBack;
import com.mrbreak.todo.view.fragments.DashBoardFragment;
import com.mrbreak.todo.view.fragments.ToDoDetailFragment;
import com.mrbreak.todo.view.fragments.ToDoDoneFragment;
import com.mrbreak.todo.view.fragments.ToDoListFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityCallBack {

    private DrawerLayout mDrawerLayout;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private boolean executeOnFirstLoad = true;
    private MenuItem actionFilterMenu;
    private MenuItem addToDoMenu;
    private View gradientLine;
    private View toolBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            MobileAds.initialize(this, "ca-app-pub-4608841867290381~2690434643");
            TextView versionNumber = findViewById(R.id.versionNumber);
            String version = getString(R.string.version) + Constants.SPACE + BuildConfig.VERSION_NAME;
            versionNumber.setText(version);
            toolBarLayout = findViewById(R.id.toolBarLayout);
            mDrawerLayout = findViewById(R.id.drawer_layout);
            gradientLine = toolBarLayout.findViewById(R.id.gradientLine);
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
                                    openDashBoard();
                                    return true;

                                case R.id.share:
                                    shareApp();
                                    return true;

                                case R.id.rateUs:
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
            }

            if (savedInstanceState == null) {
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
        actionFilterMenu = menu.findItem(R.id.action_filter);
        addToDoMenu = menu.findItem(R.id.add_todo);
        return true;
    }

    private void displayActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getName();
                if (!TextUtils.isEmpty(fragmentName) && fragmentName.contains(ToDoDetailFragment.class.getName())) {
                    onBackPressed();
                    return true;
                }
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_filter:
                openFilterDialog();

                return true;

            case R.id.add_todo:
                setFragment(new ToDoDetailFragment());

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        if (frags.size() <= 0) {
            finish();
        }

        String fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getName();

        if (!TextUtils.isEmpty(fragmentName) &&
                fragmentName.contains(DashBoardFragment.class.getName())) {
            gradientLine.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(fragmentName) && fragmentName.contains
                (ToDoDoneFragment.class.getName()) ||
                fragmentName.contains(ToDoListFragment.class.getName())) {
            finish();
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (fragmentName.contains(ToDoDetailFragment.class.getName())) {
                displayHomeIcon();
            }
            getSupportFragmentManager().popBackStack();
            displayActionBar(true);
        } else {
            finish();
        }
    }

    private void toDoClick() {
        setFragment(new ToDoListFragment());
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey, please check out this app:" +
                        " https://play.google.com/store/apps/details?id=com.mrbreak.todo");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }




    private void openDashBoard() {
        setFragment(new DashBoardFragment());
        displayActionBar(false);
    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            getApplicationContext().getPackageName())));
        }

    }


    public void displayActionBar(boolean isVisiBleActionBar) {
        if (actionBar == null) {
            actionBar = getSupportActionBar();
        }
        if (isVisiBleActionBar) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.off_white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void setFragment(Fragment fragment) {
        if (fragment.getClass().getName().contains(ToDoDetailFragment.class.getName())) {
            actionFilterMenu.setVisible(false);
            addToDoMenu.setVisible(false);
        } else {
            if (actionFilterMenu != null) {
                actionFilterMenu.setVisible(true);
                addToDoMenu.setVisible(true);
            }
        }

        if (fragment.getClass().getName().contains(DashBoardFragment.class.getName())) {
            gradientLine.setVisibility(View.GONE);
        } else {
            gradientLine.setVisibility(View.VISIBLE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out,
                R.anim.enter_from_left, R.anim.exit_to_right);

        fragmentTransaction.replace(R.id.fragmentFrameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    private void openFilterDialog() {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.to_do_filter);

        RadioButton toDoRadioButton = dialog.findViewById(R.id.toDoRadioButton);
        RadioButton doneRadioButton = dialog.findViewById(R.id.doneRadioButton);

        toDoRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!checkAttachedFragment()) {
                        dialog.dismiss();
                        return;
                    }

                    String fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getName();
                    if (fragmentName.contains(ToDoListFragment.class.getName())) {
                        dialog.dismiss();
                        return;
                    }

                    setFragment(new ToDoListFragment());
                    dialog.dismiss();
                }
            }
        });

        doneRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!checkAttachedFragment()) {
                        dialog.dismiss();
                        return;
                    }

                    String fragmentName = getSupportFragmentManager().getFragments().get(0).getClass().getName();
                    if (fragmentName.contains(ToDoDoneFragment.class.getName())) {
                        dialog.dismiss();
                        return;
                    }

                    setFragment(new ToDoDoneFragment());
                    dialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ;
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private boolean checkAttachedFragment() {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        return frags.size() > 0;
    }

    private void displayHomeIcon() {
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        actionFilterMenu.setVisible(true);
        addToDoMenu.setVisible(true);
    }

    @Override
    public void changeHeaderBar() {
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.outline_arrow_back_black_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.details));
        actionFilterMenu.setVisible(false);
        addToDoMenu.setVisible(false);
    }
}
