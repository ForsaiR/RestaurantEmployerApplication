package com.example.restaurantemployerapplication.services;

import android.app.Activity;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentService {
    public static void openFragmentReplace(Activity activity, int idOfPlacement, Fragment fragment) {
        final FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(idOfPlacement, fragment);
        fragmentTransaction.commit();
    }

    public static void openFragmentAsDialog(Activity activity, DialogFragment fragment) {
        final FragmentManager fm = ((FragmentActivity)activity).getSupportFragmentManager();
        fragment.show(fm, "tag");
    }
}
