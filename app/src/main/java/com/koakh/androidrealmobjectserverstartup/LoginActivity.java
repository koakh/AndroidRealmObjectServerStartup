package com.koakh.androidrealmobjectserverstartup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.SyncCredentials;
import io.realm.ObjectServerError;
import io.realm.SyncUser;

public class LoginActivity extends AppCompatActivity {

  private EditText mUsername;
  private EditText mPassword;
  private Button mLoginButton;
  private Button mCreateUserButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mUsername = (EditText) findViewById(R.id.input_username);
    mPassword = (EditText) findViewById(R.id.input_password);
    mLoginButton = (Button) findViewById(R.id.button_login);
    mCreateUserButton = (Button) findViewById(R.id.button_create);

    mLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        login(false);
      }
    });
    mCreateUserButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        login(true);
      }
    });
  }

  public void login(boolean createUser) {
    if (!validate()) {
      onLoginFailed(getResources().getString(R.string.realm_login_invalid_user_and_password));
      return;
    }

    mCreateUserButton.setEnabled(false);
    mLoginButton.setEnabled(false);

    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
    progressDialog.setIndeterminate(true);
    progressDialog.setMessage(getResources().getString(R.string.realm_login_authenticating));
    progressDialog.show();

    String username = this.mUsername.getText().toString();
    String password = this.mPassword.getText().toString();

    SyncCredentials credentials = SyncCredentials.usernamePassword(username, password, createUser);
    SyncUser.Callback callback = new SyncUser.Callback() {
      @Override
      public void onSuccess(SyncUser user) {
        progressDialog.dismiss();
        onLoginSuccess();
      }

      @Override
      public void onError(ObjectServerError error) {
        progressDialog.dismiss();
        String errorMsg;
        switch (error.getErrorCode()) {
          case UNKNOWN_ACCOUNT:
            errorMsg = getResources().getString(R.string.realm_login_unknown_account);
            break;
          case INVALID_CREDENTIALS:
            errorMsg = getResources().getString(R.string.realm_login_invalid_credentials);
            break;
          default:
            errorMsg = error.toString();
        }
        onLoginFailed(errorMsg);
      }
    };

    SyncUser.loginAsync(credentials, App.REALM_AUTH_URL, callback);
  }

  @Override
  public void onBackPressed() {
    // Disable going back to the MainActivity
    moveTaskToBack(true);
  }

  public void onLoginSuccess() {
    mLoginButton.setEnabled(true);
    mCreateUserButton.setEnabled(true);
    finish();
  }

  public void onLoginFailed(String errorMsg) {
    mLoginButton.setEnabled(true);
    mCreateUserButton.setEnabled(true);
    Toast.makeText(getBaseContext(), errorMsg, Toast.LENGTH_LONG).show();
  }

  public boolean validate() {
    boolean valid = true;
    String email = mUsername.getText().toString();
    String password = this.mPassword.getText().toString();

    if (email.isEmpty()) {
      valid = false;
    }

    if (password.isEmpty()) {
      valid = false;
    }

    return valid;
  }
}
