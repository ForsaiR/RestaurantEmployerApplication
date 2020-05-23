package com.example.restaurantemployerapplication.ui.login;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantemployerapplication.R;
import com.tamagotchi.tamagotchiserverprotocol.RestaurantClient;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestaurantClient.getInstance().getRestaurantsService().getAllRestaurants()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Toast.makeText(this, data.get(0).getAddress(), Toast.LENGTH_LONG).show();
                }, error -> {
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                });
    }
}
