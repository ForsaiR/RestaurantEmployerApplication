package com.example.restaurantemployerapplication.ui.login;

import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.Application;
import com.example.restaurantemployerapplication.data.exceptions.AuthLoginException;
import com.example.restaurantemployerapplication.data.exceptions.AuthPasswordException;
import com.example.restaurantemployerapplication.data.model.LoginInfo;
import com.example.restaurantemployerapplication.data.model.LoginState;
import com.example.restaurantemployerapplication.services.AuthenticationService;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class LoginViewModel extends ViewModel {

    private AuthenticationService authenticationService;
    private BehaviorSubject<LoginState> loginStateSubject = BehaviorSubject.create();
    private Disposable statedCheck;

    public LoginViewModel() {
        this(Application.getDaggerGraph().authService());
    }

    public LoginViewModel(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        loginStateSubject.onNext(LoginState.InProgress);
        statedCheck = this.authenticationService
                .isAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (result) {
                                loginStateSubject.onNext(LoginState.Success);
                            } else {
                                loginStateSubject.onNext(LoginState.Idle);
                            }

                            statedCheck.dispose();
                        }
                );
    }

    public Observable<LoginState> loginState() {
        return loginStateSubject.hide();
    }

    public void login(String login, String password) {
        authenticationService.signIn(new LoginInfo(login, password))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> this.loginStateSubject.onNext(LoginState.Success), error -> {
                    if (error instanceof AuthPasswordException) {
                        this.loginStateSubject.onNext(LoginState.PasswordError);
                    } else if (error instanceof AuthLoginException) {
                        this.loginStateSubject.onNext(LoginState.LoginError);
                    } else {
                        this.loginStateSubject.onNext(LoginState.Error);
                    }
                });
    }
}
