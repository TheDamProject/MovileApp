package com.jotamarti.golocal.UseCases.Users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public class UserUseCases implements UserApi {

    private final String TAG = "UserUseCases";

    private FirebaseAuth firebaseAuth;

    public UserUseCases() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loginUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackLoginUserInAuthService onResponseCallBackLoginUserInAuthService) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            onResponseCallBackLoginUserInAuthService.onResponse(user.getUid());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.WRONG_PASSWORD);
                            } catch (FirebaseAuthInvalidUserException e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.EMAIL_NOT_FOUND);
                            } catch (Exception e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.GENERIC_LOGIN_ERROR);
                            }
                        }
                    }
                });
    }

    @Override
    public void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackRegisterUserInAuthService onResponseCallBackRegisterUserInAuthService) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = task.getResult().getUser();
                            onResponseCallBackRegisterUserInAuthService.onResponse(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                onResponseCallBackRegisterUserInAuthService.onErrorResponse(AuthErrors.EMAIL_ALREADY_IN_USE);
                            } catch (Exception e) {
                                onResponseCallBackRegisterUserInAuthService.onErrorResponse(AuthErrors.GENERIC_REGISTER_ERROR);
                            }
                        }
                    }
                });
    }
}
