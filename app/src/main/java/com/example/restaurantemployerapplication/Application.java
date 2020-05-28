package com.example.restaurantemployerapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.restaurantemployerapplication.ui.login.LoginActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Application extends android.app.Application {
    private SharedPreferences mPrefs;
    private static Application mApp;
    private ApplicationGraph applicationGraph;
    private boolean workInMainViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        mApp = this;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        applicationGraph = DaggerApplicationGraph.create();

        InitLogoutHandler();
    }

    private void InitLogoutHandler() {
        applicationGraph.authService().isAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isAuth -> {
                    // Если пользователь был разлогирован,
                    // то нужно закрыть доступ к основной части приложения.
                    if (!isAuth && workInMainViewModel) {
                        Intent loginActivity = new Intent(this, LoginActivity.class);
                        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginActivity);
                    }
                });
    }

    public static void startWorking() {
        get().workInMainViewModel = true;
    }

    public static ApplicationGraph getDaggerGraph() {
        return get().applicationGraph;
    }

    public static Application get() {
        return mApp;
    }

    public static SharedPreferences getPrefs() {
        return get().mPrefs;
    }
}
