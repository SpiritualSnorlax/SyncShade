package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://syncshade-2f602-default-rtdb.firebaseio.com/");

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
                } else if (!confirmPasswordField.equals(passwordField)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(emailField, passwordField).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String uid = currentUser.getUid();
                                String hashedPassword = hashPassword(passwordField);
                                databaseReference.child("Users").child(uid).child("email").setValue(emailField);
                                databaseReference.child("Users").child(uid).child("password").setValue(hashedPassword);
                                databaseReference.child("Users").child(uid).child("phone").setValue(phoneField);
                                makeNotification("Account created successfully");
                                Toast.makeText(Register.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();

                            } else {
                                Toast.makeText(Register.this, "Account creation failed, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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

    public void makeNotification(String contentText) {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("SyncShade")
                .setContentText(contentText)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID,
                        "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }
}