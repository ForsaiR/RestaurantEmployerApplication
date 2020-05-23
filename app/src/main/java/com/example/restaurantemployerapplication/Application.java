package com.example.restaurantemployerapplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.restaurantemployerapplication.services.RemoteLibraryModule;

public class Application extends android.app.Application {
    private SharedPreferences mPrefs;
    private static Application mApp;

    @Override
    public void onCreate() {
        super.onCreate();

        mApp = this;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // StaffModule staffModule = DaggerStaffModule.builder().remoteLibraryModule(new RemoteLibraryModule()).build();
        // BootstrapService.getInstance().InitializeApplication();
        // AuthenticationService.getInstance().signOut();
    }

    public static Application get() {
        return mApp;
    }

    public static SharedPreferences getPrefs() {
        return get().mPrefs;
    }
}
