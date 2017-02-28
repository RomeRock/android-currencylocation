package com.romerock.modules.android.currencylocation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.romerock.modules.android.currencylocation.Adapters.RecyclerViewAdapter;
import com.romerock.modules.android.currencylocation.Interfaces.ItemClickInterface;
import com.romerock.modules.android.currencylocation.Model.ItemSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CurrencySettings extends AppCompatActivity {

    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.tittle)
    TextView tittle;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.toolbarsearch)
    Toolbar toolbarsearch;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String[] locales;
    private RecyclerViewAdapter mAdapter;
    private List<ItemSettings> itemsData;
    private SharedPreferences sharedPrefs;
    private int position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_settings);
        ButterKnife.bind(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerCurrency);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getApplication().getResources().getColor(R.color.colorPrimaryDark));
        }
        final String[] locales = Locale.getISOCountries();
        itemsData = new ArrayList<ItemSettings>(locales.length);
        int country = 0;
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            Locale locale = new Locale(obj.getCountry(), countryCode);
            int id = getResources().getIdentifier(obj.getCountry().toLowerCase(), "drawable", getPackageName());
            if (id != 0) {
                Currency currency = Currency.getInstance(locale);
                String nameCurrencyMoneey = "";
                if (currency != null) {
                    if (Build.VERSION.SDK_INT >= 19)
                        nameCurrencyMoneey += currency.getDisplayName() + " ";
                    nameCurrencyMoneey += currency.getSymbol();
                    System.out.println("Country Code = " + obj.getCountry()
                            + ", Country Name = " + obj.getDisplayCountry()
                            + ",  Symbol = " + currency.getSymbol());
                }
                itemsData.add(new ItemSettings(obj.getDisplayCountry() + " - " + nameCurrencyMoneey + "#" + countryCode, id, true,  sharedPrefs.getString(getString(R.string.preferences_currency_country_code),"").toUpperCase().equals(countryCode)?true:false));
                boolean st =sharedPrefs.getString(getString(R.string.preferences_currency_country_code),"").toUpperCase().equals(countryCode)?true:false;
            }
        }
        Collections.sort(itemsData, ItemSettings.ItemByName);
        for(int i=0; i<itemsData.size();i++){
            if(itemsData.get(i).isSelected())
                position=i;
        }
        country++;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(position);
        mAdapter = new RecyclerViewAdapter(itemsData, new ItemClickInterface() {
            @Override
            public void onItemClicked(View view, int position, String code) {
                Locale obj = new Locale("", code);
                Locale locale = new Locale(obj.getCountry(), code);
                Currency currency = Currency.getInstance(locale);
                SharedPreferences.Editor ed;
                ed = sharedPrefs.edit();
                String nameCurrencyMoneey = " - ";
                if (currency != null) {
                    if (Build.VERSION.SDK_INT >= 19)
                        nameCurrencyMoneey += currency.getDisplayName() + " ";
                    nameCurrencyMoneey += currency.getSymbol();
                }
                ed.putString(getString(R.string.preferences_currency_symbol), currency.getSymbol());
                ed.putString(getString(R.string.preferences_currency_country_money_symbol), currency.getCurrencyCode());
                ed.putString(getString(R.string.preferences_currency_name_money), nameCurrencyMoneey);
                ed.putString(getString(R.string.preferences_currency_country_code), obj.getCountry());
                ed.commit();
                Toast.makeText(CurrencySettings.this, getString(R.string.settings_option_currency_change, obj.getDisplayCountry()), Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager) CurrencySettings.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textSearch) {
                List<ItemSettings> itemsDataTemp = new ArrayList<ItemSettings>();
                for (int i = 0; i < itemsData.size(); i++) {
                    if (itemsData.get(i).getTitle().toUpperCase().contains(textSearch.toUpperCase())) {
                        itemsDataTemp.add(itemsData.get(i));
                    }
                }
                mAdapter.setFilter(itemsDataTemp);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.setVisibility(View.GONE);
            toolbarsearch.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    @OnClick({R.id.toolbarback, R.id.toolbarsearch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbarback:
                finish();
                break;
            case R.id.toolbarsearch:
                toolbarsearch.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
    }
}
