package com.example.restaurantemployerapplication.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.data.model.LoginState;
import com.example.restaurantemployerapplication.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {

    LoginViewModel loginViewModel;
    Disposable stateLoginObserver;
    TextInputLayout loginLayout;
    TextInputLayout passwordLayout;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitView();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        stateLoginObserver = loginViewModel.loginState().observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::stateHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (stateLoginObserver != null) {
            stateLoginObserver.dispose();
        }
    }

    private void InitView() {
        loginLayout = this.findViewById(R.id.loginLayout);
        passwordLayout = this.findViewById(R.id.passwordLayout);
        loginButton = this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this::loginClick);
    }

    private void loginClick(View view) {

        String login = loginLayout.getEditText().getText().toString();
        String password = passwordLayout.getEditText().getText().toString();

        loginViewModel.login(login, password);
    }

    private void resetError() {
        loginButton.setEnabled(true);
        passwordLayout.setError(null);
        loginLayout.setError(null);
        loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void stateHandler(LoginState nextState) {
        resetError();

        switch (nextState) {
            case Idle:
                break;
            case InProgress:
                loginButton.setEnabled(false);
                break;
            case PasswordError:
                passwordLayout.setError(getString(R.string.passwordError));
                break;
            case LoginError:
                loginLayout.setError(getString(R.string.loginError));
                break;
            case Error:
                loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
                break;
            case Success:
                loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSuccess));
                openMainActivity();
                break;
        }
    }

    private void openMainActivity() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
