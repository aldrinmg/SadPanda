package com.ecchilon.sadpanda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;

import com.ecchilon.sadpanda.auth.ExhentaiAuth;
import com.ecchilon.sadpanda.auth.LoginFragment;
import com.ecchilon.sadpanda.overview.OverviewFragment;
import com.ecchilon.sadpanda.overview.SearchActivity;
import com.ecchilon.sadpanda.search.AbstractSearchActivity;
import com.google.inject.Inject;

import roboguice.inject.ContentView;


@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractSearchActivity implements LoginFragment.LoginListener {

    @Inject
    private ExhentaiAuth mAuth;

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.front_page);

        if(savedInstanceState == null) {
            if (mAuth.isLoggedIn()) {
                showOverviewFragment();
            } else {
                showErrorFragment();
                showLoginFragment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem login = menu.findItem(R.id.login_menu);

        if(mAuth.isLoggedIn()) {
            login.setTitle(R.string.logout_menu);
        }
        else {
            login.setTitle(R.string.login_menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_menu:
                showLoginFragment();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoginFragment() {
        if(mLoginFragment != null) {
            return;
        }

        mLoginFragment = new LoginFragment();
        mLoginFragment.setLoginListener(this);
        mLoginFragment.show(getSupportFragmentManager(), "LOGIN");
    }

    private void closeLoginFragment() {
        mLoginFragment.dismiss();
        mLoginFragment = null;
    }

    private void showOverviewFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, OverviewFragment.newInstance(getQuery()))
                .commit();
    }

    private void showErrorFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ErrorFragment.newInstance(R.string.login_request))
                .commit();
    }

    @Override
    public void onSuccess() {
        closeLoginFragment();
        showOverviewFragment();
    }

    @Override
    public void onSearchSubmitted(String url, String query) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra(OverviewFragment.URL_KEY, url);
        searchIntent.putExtra(OverviewFragment.QUERY_KEY, query);

        startActivity(searchIntent);
    }
}
