package com.romerock.modules.android.currencylocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.img_logo_romerock)
    ImageView img_logo_romerock;
    @BindView(R.id.follow_twitter)
    ImageView followTwitter;
    @BindView(R.id.follow_gitHub)
    ImageView followGitHub;
    @BindView(R.id.follow_facebook)
    ImageView followFacebook;
    @BindView(R.id.languageDetect)
    TextView languageDetect;
    @BindView(R.id.textDescription)
    TextView textDescription;
    @BindView(R.id.btn_detect)
    Button btnDetect;
    @BindView(R.id.content_main)
    RelativeLayout contentMain;
    @BindView(R.id.currency)
    TextView currency;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Nullable
    @BindView(R.id.imgUseCurrencyDetected)
    ImageView imgUseCurrencyDetected;
    @BindView(R.id.txtValueWithCurrency)
    TextView txtValueWithCurrency;
    private static android.app.AlertDialog alertDialog;
    private SharedPreferences sharedPref;
    private Locale obj;
    private int DETECT_CODE = 500;
    private Typeface font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        sharedPref = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);

        WebView view = new WebView(this);
        view.setVerticalScrollBarEnabled(false);
        view.setBackgroundColor(getResources().getColor(R.color.drawable));
        ((RelativeLayout) findViewById(R.id.relContent)).addView(view);
        view.loadData(getString(R.string.thank_you), "text/html; charset=utf-8", "utf-8");
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        textDescription.setTypeface(font);
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        languageDetect.setTypeface(font);
        currency.setTypeface(font);
        int id = getResources().getIdentifier(sharedPref.getString(getString(R.string.preferences_currency_country_code), "").toLowerCase(), "drawable", getPackageName());
        imgUseCurrencyDetected.setImageResource(id);
        obj = new Locale("", sharedPref.getString(getString(R.string.preferences_currency_country_code), ""));
        currency.setText(obj.getDisplayCountry() +
                sharedPref.getString(getString(R.string.preferences_currency_name_money), "$").toString());
        txtValueWithCurrency.setText(getString(R.string.value_with_currency, sharedPref.getString(getString(R.string.preferences_currency_symbol), "$")));
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        txtValueWithCurrency.setTypeface(font);
        boolean firstTime=sharedPref.getBoolean("firstTimeOpen",true);
        if(firstTime){
            popUp();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void popUp(){
        // Detect currency
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putBoolean("firstTimeOpen",false);
        ed.commit();
        android.app.AlertDialog.Builder builder;
        builder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setCancelable(true);
        View view = inflater.inflate(R.layout.pop_up, null);
        view.findViewById(R.id.popUpOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.popUpChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent i = new Intent(MainActivity.this, CurrencySettings.class);
                startActivityForResult(i, DETECT_CODE);
            }
        });
        TextView text = (TextView) view.findViewById(R.id.txtUseCurrencyDetected);
        TextView txtTittleDetect = (TextView) view.findViewById(R.id.txtTittleDetect);
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        txtTittleDetect.setTypeface(font);
        text.setText(sharedPref.getString(getString(R.string.preferences_currency_country_money_symbol), "").toString() +
                " - " + sharedPref.getString(getString(R.string.preferences_currency_name_money), "$").toString());
        ImageView imgFlag = (ImageView) view.findViewById(R.id.imgUseCurrencyDetected);
        int id = getResources().getIdentifier(sharedPref.getString(getString(R.string.preferences_currency_country_code), "").toLowerCase(), "drawable", getPackageName());
        imgFlag.setImageResource(id);
        builder.setView(view);
        builder.create();
        alertDialog = builder.show();
    }


    @OnClick({R.id.img_logo_romerock, R.id.follow_twitter, R.id.follow_gitHub, R.id.follow_facebook, R.id.textDescription, R.id.btn_detect})
    public void onClick(View view) {
        String url = "";
        switch (view.getId()) {
            case R.id.img_logo_romerock:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.romerock_page))));
                break;
            case R.id.follow_facebook:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook_profile)));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook))));
                }
                break;
            case R.id.follow_gitHub:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_git_hub))));
                break;
            case R.id.follow_twitter:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter_profile)));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter))));
                }
                break;
            case R.id.btn_detect:
                Intent i = new Intent(MainActivity.this, CurrencySettings.class);
                startActivityForResult(i, DETECT_CODE);
                break;
            case R.id.textDescription:
                popUp();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETECT_CODE) {
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
