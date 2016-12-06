package com.example.brewc.react.main.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.brewc.react.R;
import com.example.brewc.react.main.Fragments.AboutPageFragment;
import com.example.brewc.react.main.Fragments.ContactsPageFragment;
import com.example.brewc.react.main.Fragments.DatabasePageFragment;
import com.example.brewc.react.main.Fragments.HomePageFragment;
import com.example.brewc.react.main.Fragments.ReactPageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Drawer
 */

public class DrawerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private final int HOME_POS = 0;

    private FirebaseAuth _auth;
    private ActionBarDrawerToggle _actionBarDrawerToggle;
    private DrawerLayout _drawerLayout;
    private ListView _navList;
    private FragmentTransaction _fragmentTransaction;
    private FragmentManager _fragmentManager;
    private int _lastClickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        _auth = FirebaseAuth.getInstance();

        this._drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this._navList = (ListView) findViewById(R.id.nav_list);

        ArrayList<String> navArray = new ArrayList<String>();

        navArray.add(this.getResources().getString(R.string.home_title));
        navArray.add(this.getResources().getString(R.string.react_title));
        navArray.add(this.getResources().getString(R.string.contacts_title));
        navArray.add(this.getResources().getString(R.string.database_title));
        navArray.add(this.getResources().getString(R.string.about_title));

        this._navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_activated_1, navArray);

        this._navList.setAdapter(adapter);
        this._navList.setOnItemClickListener(this);

        this._actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, this._drawerLayout, R.string.open_drawer, R.string.close_drawer);
        this._drawerLayout.addDrawerListener(this._actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        this._fragmentManager = getSupportFragmentManager();

        this._lastClickedPosition = 0;
        // default selection
        loadSelection(0);

    }

    private void safeLogout() {
        Intent loginPage = new Intent(DrawerActivity.this, LoginPageActivity.class);
        startActivity(loginPage);
        if (this._auth.getCurrentUser() != null) {
            this._auth.signOut();
        }
    }

    private void loadFragment(Fragment fragment, int fragmentName) {
        this._fragmentTransaction.replace(R.id.fragment_holder, fragment);
        this._fragmentTransaction.commit();
        this.getSupportActionBar().setTitle(fragmentName);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow((findViewById(R.id.fragment_holder)).getWindowToken(), 0);
    }

    private void loadSelection(int choice) {
        this._fragmentTransaction = this._fragmentManager.beginTransaction();
        switch (choice) {
            case 0:
                loadFragment(new HomePageFragment(), R.string.home_title);
                break;
            case 1:
                loadFragment(new ReactPageFragment(), R.string.react_title);
                break;
            case 2:
                loadFragment(new ContactsPageFragment(), R.string.contacts_title);
                break;
            case 3:
                loadFragment(new DatabasePageFragment(), R.string.database_title);
                break;
            case 4:
                loadFragment(new AboutPageFragment(), R.string.about_title);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this._actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            safeLogout();
        } else if (id == android.R.id.home) {
            if (this._drawerLayout.isDrawerOpen(this._navList)) {
                this._drawerLayout.closeDrawer(this._navList);
            } else {
                this._drawerLayout.openDrawer(this._navList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != this._lastClickedPosition) {
            loadSelection(position);
            this._lastClickedPosition = position;
            this._navList.setItemChecked(this._lastClickedPosition, true);
        }
        this._drawerLayout.closeDrawer(this._navList);
    }
}
