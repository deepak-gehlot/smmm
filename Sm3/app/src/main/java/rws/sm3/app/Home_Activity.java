package rws.sm3.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CircleImageView;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.Fragments.AboutUsFragment;
import rws.sm3.app.Fragments.AddSongFragment;
import rws.sm3.app.Fragments.ContactUsFragment;
import rws.sm3.app.Fragments.EditProfileFragment;
import rws.sm3.app.Fragments.FollowerFragment;
import rws.sm3.app.Fragments.HomeFragment;
import rws.sm3.app.Fragments.MoreFragment;
import rws.sm3.app.Fragments.SearchFragment;
import rws.sm3.app.NavigationPack.NavigationDrawerAdapter;

/**
 * Created by Android-2 on 10/14/2015.
 */
public class Home_Activity extends AppCompatActivity implements View.OnClickListener {
    Context appContext;
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerPanel;
    private static int[] list_img = null;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appContext = this;
        setUpNavigationDrawer();
        Init();
        SetUserData();
    }

    public void SetUserData() {
        if (!Utility.getSharedPreferences(appContext, Constant.LOG_U_PRO_PIC).equals("")) {
            Picasso.with(appContext)
                    .load(Constant.PRO_PIC_IMG_URL + Utility.getSharedPreferences(appContext, Constant.LOG_U_PRO_PIC))
                    .placeholder(R.drawable.user_default).error(R.drawable.user_default)
                    .into(((CircleImageView) findViewById(R.id.profile_pic)));
        }
        if (!Utility.getSharedPreferences(appContext, Constant.FIRST_NAME).equals("")) {
            ((CustomTextView) findViewById(R.id.user_name)).setText(Utility.getSharedPreferences(appContext, Constant.FIRST_NAME));
        }
        if (!Utility.getSharedPreferences(appContext, Constant.STATUS).equals("")) {
            ((CustomTextView) findViewById(R.id.loc)).setText(Utility.getSharedPreferences(appContext, Constant.STATUS));
        }
    }

    public void SetLike() {
        if (!Utility.getSharedPreferences(appContext, Constant.COUNT_PLAYLIST).equals("")) {
            ((CustomTextView) findViewById(R.id.no_of_play_songs)).setText(Utility.getSharedPreferences(appContext, Constant.COUNT_PLAYLIST));
        }
        if (!Utility.getSharedPreferences(appContext, Constant.COUNT_FOLLOWER).equals("")) {
            ((CustomTextView) findViewById(R.id.no_of_followers)).setText(Utility.getSharedPreferences(appContext, Constant.COUNT_FOLLOWER));
        }
        if (!Utility.getSharedPreferences(appContext, Constant.COUNT_FOLLOWING).equals("")) {
            ((CustomTextView) findViewById(R.id.no_of_following)).setText(Utility.getSharedPreferences(appContext, Constant.COUNT_FOLLOWING));
        }
        if (!Utility.getSharedPreferences(appContext, Constant.COUNT_UPLOADS).equals("")) {
            ((CustomTextView) findViewById(R.id.no_of_uploads)).setText(Utility.getSharedPreferences(appContext, Constant.COUNT_UPLOADS));
        }
    }

    private void Init() {
        openActivites(0);
    }

    @Override
    protected void onResume() {
        SetUserData();
        if (Utility.getSharedPreferences(appContext, "ACTFOR").equals("PRO")) {
            Utility.setSharedPreference(appContext, "ACTFOR", "");
            if (Utility.getSharedPreferences(appContext, "USERIS").equals(Utility.getSharedPreferences(appContext, Constant.ID))) {
                openActivites(3);
            } else {
                Utility.setSharedPreference(appContext, "USER_DATA", Utility.getSharedPreferences(appContext, "USERIS2"));
                openActivites(10);
            }
        }
        if (Utility.getSharedPreferences(appContext, "ACTFOR").equals("HOME")) {
            Utility.setSharedPreference(appContext, "ACTFOR", "");
            openActivites(Integer.parseInt(Utility.getSharedPreferences(appContext, "POSITION")));
        }
        super.onResume();
    }

    private void setUpNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        ((CustomTextView) findViewById(R.id.title)).setText(getResources().getString(R.string.title));
        (toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(mDrawerPanel) == true) {
                    mDrawerLayout.closeDrawer(mDrawerPanel);
                } else {
                    mDrawerLayout.openDrawer(mDrawerPanel);
                }
            }
        });
        //  list_img = new int[]{R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow,R.drawable.arrow};
        list_img = new int[]{R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow};
        ListView mDrawerListView = (ListView) findViewById(R.id.navDrawerList);
        mDrawerPanel = (LinearLayout) findViewById(R.id.navDrawerPanel);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(appContext, getResources().getStringArray(R.array.list_item), list_img);
        mDrawerListView.setAdapter(navigationDrawerAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(mDrawerPanel);
                if (!Utility.getSharedPreferences(appContext, "POSITION").equals("" + position)) {
                    openActivites(position);
                }
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        (findViewById(R.id.lay1)).setOnClickListener(this);
        (findViewById(R.id.lay2)).setOnClickListener(this);
        (findViewById(R.id.lay3)).setOnClickListener(this);
        (findViewById(R.id.lay4)).setOnClickListener(this);
        (findViewById(R.id.log_out)).setOnClickListener(this);
        (findViewById(R.id.fb)).setOnClickListener(this);
        (findViewById(R.id.tw)).setOnClickListener(this);
        (findViewById(R.id.is)).setOnClickListener(this);
        (findViewById(R.id.log_out)).setOnClickListener(this);
        (findViewById(R.id.profile_pic)).setOnClickListener(this);
        (toolbar.findViewById(R.id.ty)).setOnClickListener(this);
    }

    public void openActivites(int position) {
        switch (position) {
            case 0:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.INVISIBLE);
                fragment = new HomeFragment();
                break;
            case 1:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new AddSongFragment();
                break;
            case 2:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new SearchFragment();
                break;
            case 3:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                Utility.setSharedPreference(appContext, "EDIT_FOR", "");
                fragment = new EditProfileFragment();
                break;
            case 4:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new ContactUsFragment();
                break;
            case 5:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new AboutUsFragment();
                break;
            case 6:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                startActivity(new Intent(appContext, ChangePaswordActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case 9:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new MoreFragment();
                break;
            case 10:
                Utility.setSharedPreference(appContext, "POSITION", "" + position);
                (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
                fragment = new FollowerFragment();
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }

    private void Edit_profile(int pos) {
        Utility.setSharedPreference(appContext, "POSITION", "" + pos);
        (toolbar.findViewById(R.id.ty)).setVisibility(View.VISIBLE);
        fragment = new EditProfileFragment();
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ty:
                onBackPressed();
                break;
            case R.id.profile_pic:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                openActivites(3);
                break;
            case R.id.lay1:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                Utility.setSharedPreference(appContext, "EDIT_FOR", "");
                Edit_profile(3);
                break;
            case R.id.lay2:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                Utility.setSharedPreference(appContext, "EDIT_FOR", "2");
                Edit_profile(3);
                break;
            case R.id.lay3:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                Utility.setSharedPreference(appContext, "EDIT_FOR", "3");
                Edit_profile(3);
                break;
            case R.id.lay4:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                Utility.setSharedPreference(appContext, "EDIT_FOR", "4");
                Edit_profile(3);
                break;
            case R.id.fb:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AudioKetab-407799662739429/")));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.tw:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/AudioKetab")));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.is:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/audioketab/")));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.log_out:
                mDrawerLayout.closeDrawer(mDrawerPanel);
                Utility.setSharedPreference(appContext, Constant.ID, "");
                startActivity(new Intent(appContext, MainActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;
        }
    }

    public void PlaySongs(int pos) {
        startActivity(new Intent(appContext, PlaySongActivity.class).putExtra("song_object", pos));
        // startActivity(new Intent(appContext, PlaySongNewActivity.class).putExtra("song_object", pos));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void CallEditProfile() {
        startActivity(new Intent(appContext, Edit_Profile_Activity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        if (!Utility.getSharedPreferences(appContext, "POSITION").equals("0")) {
            if (Utility.getSharedPreferences(appContext, "PREVPOS").equals("2")) {
                openActivites(Integer.parseInt(Utility.getSharedPreferences(appContext, "PREVPOS")));
                Utility.setSharedPreference(appContext, "PREVPOS", "0");
            } else {
                openActivites(0);
            }
        } else {
            finish();
        }
    }
}