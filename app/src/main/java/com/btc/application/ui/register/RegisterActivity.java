package com.btc.application.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.btc.application.myapplication.R;
import com.btc.application.ui.login.LoginActivity;
import com.btc.application.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private HashMap<String, String> stringHashMap;
    String TAG = RegisterActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText inviteCodeEditText = findViewById(R.id.inviteCode);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText confirmPwdEditText = findViewById(R.id.confirmPwd);
        final Button registerButton = findViewById(R.id.registerSubmit);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        stringHashMap = new HashMap<>();

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState RegisterFormState) {
                if (RegisterFormState == null) {
                    return;
                }
                if (RegisterFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(RegisterFormState.getUsernameError()));
                }
                if (RegisterFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(RegisterFormState.getPasswordError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                if (registerResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showLoginFailed(registerResult.getError());
                }
                if (registerResult.getSuccess() != null) {
                    updateUiWithUser(registerResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailEditText.getText().toString().trim().length()==0 ||
                        passwordEditText.getText().toString().trim().length()==0 ||
                        confirmPwdEditText.getText().toString().trim().length()==0 ||
                        usernameEditText.getText().toString().trim().length()==0 ||
                        inviteCodeEditText.getText().toString().trim().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "输入有误，请重新输入！", Toast.LENGTH_LONG).show();
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                    confirmPwdEditText.setText("");
                    emailEditText.setText("");
                    inviteCodeEditText.setText("");
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userAccount", emailEditText.getText().toString());
                    jsonObject.put("password", passwordEditText.getText().toString());
                    jsonObject.put("confirmPwd", confirmPwdEditText.getText().toString());
                    jsonObject.put("userName", usernameEditText.getText().toString());
                    jsonObject.put("inviteCode", inviteCodeEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String method = "user/register";
                String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                Log.v(TAG , result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        Log.v(TAG , code);
                        Toast.makeText(getApplicationContext(), "注册成功，请到邮箱里激活！", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUiWithUser(RegisterUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}