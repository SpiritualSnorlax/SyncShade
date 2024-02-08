package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    protected TextInputEditText etEmail;
    protected TextInputEditText etPhone;
    protected TextInputEditText etPassword;
    protected TextView passwordRequirementsState;
    protected CheckedTextView characterReq;
    protected CheckedTextView uppercaseReq;
    protected CheckedTextView numberReq;
    protected CheckedTextView symbolReq;
    protected TextInputEditText etCPassword;
    protected Button signUpBtn;
    protected TextView yesAccountBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EdgeToEdge.enable(this);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        passwordRequirementsState = findViewById(R.id.passwordRequirementsState);
        characterReq = findViewById(R.id.characterReq);
        uppercaseReq = findViewById(R.id.uppercaseReq);
        numberReq = findViewById(R.id.numberReq);
        symbolReq = findViewById(R.id.symbolReq);
        etCPassword = findViewById(R.id.etCPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
        yesAccountBtn = findViewById(R.id.yesAccountBtn);

        signUp();
        passwordRequirementsChecker();
        passwordRequirementsVisibility();
        yesAccountButton();

    }

    public void signUp() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailField, phoneField, passwordField, confirmPasswordField;
                emailField = etEmail.getText().toString();
                phoneField = etPhone.getText().toString();
                passwordField = etPassword.getText().toString();
                confirmPasswordField = etCPassword.getText().toString();

                if(emailField.isEmpty() || phoneField.isEmpty() || passwordField.isEmpty() || confirmPasswordField.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(passwordField)) {
                    Toast.makeText(Register.this, "Password does not meet requirements", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(emailField, passwordField).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Register.this, "Account Creation Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();

                            } else {
                                Toast.makeText(Register.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,12}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    public static boolean hasMinMaxCharacters(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.{8,12}$)";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.find();
    }
    public static boolean hasUppercase(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.find();
    }
    public static boolean hasNumber(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.find();
    }
    public static boolean hasSymbol(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "(?=.*[@#$%^&+=!])";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.find();
    }
    public void passwordRequirementsVisibility() {
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordRequirementsState.setVisibility(View.VISIBLE);
                    characterReq.setVisibility(View.VISIBLE);
                    uppercaseReq.setVisibility(View.VISIBLE);
                    numberReq.setVisibility(View.VISIBLE);
                    symbolReq.setVisibility(View.VISIBLE);
                } else {
                    passwordRequirementsState.setVisibility(View.GONE);
                    characterReq.setVisibility(View.GONE);
                    uppercaseReq.setVisibility(View.GONE);
                    numberReq.setVisibility(View.GONE);
                    symbolReq.setVisibility(View.GONE);
                }
            }
        });
    }

    public void passwordRequirementsChecker(){
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordRequirements();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkPasswordRequirements() {

        if (hasMinMaxCharacters(etPassword.getText().toString())) {
            characterReq.setTextColor(getResources().getColor(R.color.GREEN));
            characterReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREEN), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (!hasMinMaxCharacters(etPassword.getText().toString())) {
            characterReq.setTextColor(getResources().getColor(R.color.GREY));
            characterReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREY), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (hasUppercase(etPassword.getText().toString())) {
            uppercaseReq.setTextColor(getResources().getColor(R.color.GREEN));
            uppercaseReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREEN), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (!hasUppercase(etPassword.getText().toString())) {
            uppercaseReq.setTextColor(getResources().getColor(R.color.GREY));
            uppercaseReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREY), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (hasNumber(etPassword.getText().toString())) {
            numberReq.setTextColor(getResources().getColor(R.color.GREEN));
            numberReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREEN), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (!hasNumber(etPassword.getText().toString())) {
            numberReq.setTextColor(getResources().getColor(R.color.GREY));
            numberReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREY), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (hasSymbol(etPassword.getText().toString())) {
            symbolReq.setTextColor(getResources().getColor(R.color.GREEN));
            symbolReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREEN), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (!hasSymbol(etPassword.getText().toString())) {
            symbolReq.setTextColor(getResources().getColor(R.color.GREY));
            symbolReq.getCheckMarkDrawable().setColorFilter(getResources().getColor(R.color.GREY), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (hasMinMaxCharacters(etPassword.getText().toString()) && hasUppercase(etPassword.getText().toString()) && hasNumber(etPassword.getText().toString()) && hasSymbol(etPassword.getText().toString())) {
            passwordRequirementsState.setText("Password Requirements: Complete");
            passwordRequirementsState.setTextColor(getResources().getColor(R.color.GREEN));
        } else {
            passwordRequirementsState.setText("Password Requirements: Incomplete");
            passwordRequirementsState.setTextColor(getResources().getColor(R.color.RED));
        }
    }

    public void yesAccountButton() {
        yesAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
    }
}