package nl.rsdt.japp.application.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.maps.OnMapReadyCallback;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.fragments.AboutFragment;
import nl.rsdt.japp.application.fragments.HomeFragment;
import nl.rsdt.japp.application.fragments.JappMapFragment;
import nl.rsdt.japp.application.fragments.JappPreferenceFragment;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class FragmentNavigationManager {

    public static final String FRAGMENT_HOME = "FRAGMENT_HOME";

    public static final String FRAGMENT_MAP = "FRAGMENT_MAP";

    public static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";

    public static final String FRAGMENT_ABOUT = "FRAGMENT_ABOUT";

    private static final String BUNDLE_KEY_FRAGMENT = "BUNDLE_KEY_FRAGMENT";

    private FragmentManager manager;

    private String currentFragmentTag = "";

    private HomeFragment homeFragment;

    private JappMapFragment mapFragment;

    private JappPreferenceFragment preferenceFragment;

    private AboutFragment aboutFragment;

    public void initialize(FragmentManager manager)
    {
        this.manager = manager;
        setupFragments();
    }

    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        if(!currentFragmentTag.isEmpty())
        {
            saveInstanceState.putString(BUNDLE_KEY_FRAGMENT, currentFragmentTag);
        }
    }

    public void onSavedInstance(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            String value = savedInstanceState.getString(BUNDLE_KEY_FRAGMENT, currentFragmentTag);
            switchTo(value);
        }
    }

    public void switchTo(String fragment)
    {
        if(currentFragmentTag == null || currentFragmentTag.equals(fragment)) return;

        FragmentTransaction ft = manager.beginTransaction();

        switch (fragment)
        {
            case FRAGMENT_HOME:
                ft.show(homeFragment);
                break;
            case FRAGMENT_MAP:
                ft.show(mapFragment);
                break;
            case FRAGMENT_SETTINGS:
                ft.show(preferenceFragment);
                break;
            case FRAGMENT_ABOUT:
                ft.show(aboutFragment);
                break;
        }

        switch (currentFragmentTag)
        {
            case FRAGMENT_HOME:
                ft.hide(homeFragment);
                break;
            case FRAGMENT_MAP:
                ft.hide(mapFragment);
                break;
            case FRAGMENT_SETTINGS:
                ft.hide(preferenceFragment);
                break;
            case FRAGMENT_ABOUT:
                ft.hide(aboutFragment);
                break;
        }

        ft.commit();
        currentFragmentTag = fragment;
    }

    public void setupMap(OnMapReadyCallback callback)
    {
        if(mapFragment == null)
        {
            mapFragment = (JappMapFragment) manager.findFragmentByTag(FRAGMENT_MAP);
            mapFragment.getMapAsync(callback);
        }
        else
        {
            mapFragment.getMapAsync(callback);
        }
    }

    private void setupFragments()
    {
        FragmentTransaction ft = manager.beginTransaction();

        homeFragment = (HomeFragment) manager.findFragmentByTag(HomeFragment.TAG);
        if(homeFragment == null)
        {
            homeFragment = new HomeFragment();
            ft.add(R.id.container, homeFragment, HomeFragment.TAG);
        }
        ft.hide(homeFragment);


        mapFragment = (JappMapFragment) manager.findFragmentByTag(JappMapFragment.TAG);
        if(mapFragment == null)
        {
            mapFragment = new JappMapFragment();
            ft.add(R.id.container, mapFragment, JappMapFragment.TAG);
        }
        ft.show(mapFragment);
        currentFragmentTag = FRAGMENT_MAP;


        preferenceFragment  = (JappPreferenceFragment)manager.findFragmentByTag(JappPreferenceFragment.TAG);
        if(preferenceFragment == null)
        {
            preferenceFragment = new JappPreferenceFragment();
            ft.add(R.id.container, preferenceFragment, JappPreferenceFragment.TAG);
        }
        ft.hide(preferenceFragment);

        aboutFragment = (AboutFragment)manager.findFragmentByTag(AboutFragment.TAG);
        if(aboutFragment == null)
        {
            aboutFragment = new AboutFragment();
            ft.add(R.id.container, aboutFragment, AboutFragment.TAG);
        }
        ft.hide(aboutFragment);

        ft.commit();
    }

    public void onDestroy()
    {
        if(manager != null)
        {
            manager = null;
        }

        if(homeFragment != null)
        {
            homeFragment = null;
        }

        if(mapFragment != null)
        {
            mapFragment = null;
        }

        if(preferenceFragment != null)
        {
            preferenceFragment = null;
        }

        if(aboutFragment != null)
        {
            aboutFragment = null;
        }


    }


}
