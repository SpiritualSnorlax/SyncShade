package com.example.mrsa;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected FloatingActionButton addDeviceBtn;
    protected Button firstGridBtn;
    protected Button secondGridBtn;
    protected Button thirdGridBtn;
    protected Button fourthGridBtn;
    protected TextView firstPositionIndicator;
    protected TextView secondPositionIndicator;
    protected TextView thirdPositionIndicator;
    protected TextView fourthPositionIndicator;
    int selectedIcon = -1;
    int firstSelectedIcon;
    int secondSelectedIcon;
    int thirdSelectedIcon;
    int fourthSelectedIcon;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-services-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        addDeviceBtn = rootView.findViewById(R.id.addDeviceBtn);
        firstGridBtn = rootView.findViewById(R.id.firstGridBtn);
        secondGridBtn = rootView.findViewById(R.id.secondGridBtn);
        thirdGridBtn = rootView.findViewById(R.id.thirdGridBtn);
        fourthGridBtn = rootView.findViewById(R.id.fourthGridBtn);
        firstPositionIndicator = rootView.findViewById(R.id.firstPositionIndicator);
        secondPositionIndicator = rootView.findViewById(R.id.secondPositionIndicator);
        thirdPositionIndicator = rootView.findViewById(R.id.thirdPositionIndicator);
        fourthPositionIndicator = rootView.findViewById(R.id.fourthPositionIndicator);

        addDeviceBtn();
        toCommandScreen();
        updatePositionIndicator(firstPositionIndicator);
        updateButtonColor(firstGridBtn);
        restoreAppPreferencesHomeScreen(firstGridBtn, firstPositionIndicator, "firstDeviceBtn");
        restoreAppPreferencesHomeScreen(secondGridBtn, secondPositionIndicator, "secondDeviceBtn");
        restoreAppPreferencesHomeScreen(thirdGridBtn, thirdPositionIndicator, "thirdDeviceBtn");
        restoreAppPreferencesHomeScreen(fourthGridBtn, fourthPositionIndicator, "fourthDeviceBtn");

        return rootView;
    }
    public void sendCommandDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_command, null);
        Button openBtn = dialogView.findViewById(R.id.openBtn);
        Button closeBtn = dialogView.findViewById(R.id.closeBtn);
        Chip decreaseBtn = dialogView.findViewById(R.id.decreaseBtn);
        Chip increaseBtn = dialogView.findViewById(R.id.increaseBtn);
        EditText percentOCValue = dialogView.findViewById(R.id.seekBarValue);


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        openFullyCommand(openBtn);
        closeFullyCommand(closeBtn);
        closeByTenPercent(decreaseBtn);
        openByTenPercent(increaseBtn);
        updatePercentOCValue(percentOCValue);
    }

    public void openFullyCommand(Button openBtn) {
            openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("Roller Shade Control").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                            if (!isAdded()) {
                                return;
                            }

                            // Check if the State value is 1
                            Float stateValue = stateSnapshot.getValue(Float.class);
                            if (stateValue == 1) {
                                // Do not run the command if the State value is 1
                                makeNotification("The roller shade is already fully open.", R.drawable.icon_notification);
                                return;
                            }

                            // If State value is not 1, check App Request value
                            databaseReference.child("Roller Shade Control").child("App Request").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot appRequestSnapshot) {
                                    if (!isAdded()) {
                                        return;
                                    }

                                    String appRequestStringValue = appRequestSnapshot.getValue(String.class);
                                    if (!"0".equals(appRequestStringValue)) {
                                        // Do not run the command if the App Request value is 1
                                        makeNotification("A command is already in progress. Please wait until it is completed.", R.drawable.icon_notification);
                                        return;
                                    }

                                    // If State and App Request values are acceptable, execute the command
                                    String appRequestValue = "1";
                                    int ocFullyValue = 1;
                                    String ocRollerShadeValue = "Opened";

                                    databaseReference.child("Roller Shade Control").child("App Request").setValue(appRequestValue);
                                    databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(ocFullyValue);
                                    databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue(ocRollerShadeValue);

                                    makeNotification("Roller Shade - Opened Fully", R.drawable.rs_open);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError appRequestError) {
                                    // Handle onCancelled event for App Request
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError stateError) {
                            // Handle onCancelled event for State
                        }
                    });
                }
            });
        }

    public void closeFullyCommand(Button closeBtn) {
            closeBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    databaseReference.child("Roller Shade Control").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot stateSnapshot) {

                            if (!isAdded()) {
                                return;
                            }

                            // Check if the State value is 0
                            Float stateValue = stateSnapshot.getValue(Float.class);
                            if (stateValue == 0) {
                                // Do not run the command if the State value is 0
                                makeNotification("The roller shade is already fully close.", R.drawable.icon_notification);
                                return;
                            }

                            // If State value is not 0, check App Request value
                            databaseReference.child("Roller Shade Control").child("App Request").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot appRequestSnapshot) {
                                    if (!isAdded()) {
                                        return;
                                    }

                                    String appRequestStringValue = appRequestSnapshot.getValue(String.class);
                                    if (!"0".equals(appRequestStringValue)) {
                                        // Do not run the command if the App Request value is not 0
                                        makeNotification("A command is already in progress. Please wait until it is completed.", R.drawable.icon_notification);
                                        return;
                                    }
                                    String appRequestValue = "1";
                                    int ocFullyValue = 0;
                                    String ocRollerShadeValue = "Closed";

                                    databaseReference.child("Roller Shade Control").child("App Request").setValue(appRequestValue);
                                    databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(ocFullyValue);
                                    databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue(ocRollerShadeValue);

                                    makeNotification("Roller Shade - Closed Fully", R.drawable.rs_close);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError appRequestError) {
                                    // Handle onCancelled event for App Request
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError stateError) {
                            // Handle onCancelled event for State
                        }
                    });
                }
            });
        }

    public void closeByTenPercent(Button decreaseBtn) {
            decreaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve the current value of the State from the database
                    databaseReference.child("Roller Shade Control").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                            if (!isAdded()) {
                                return;
                            }

                            // Get the value of State
                            Float stateValue = stateSnapshot.getValue(Float.class);
                            // Check if the state is already fully closed
                            if (stateValue == 0) {
                                makeNotification("The roller shade is already fully closed.", R.drawable.icon_notification);
                                return;
                            }

                            // Check if App Request value is acceptable
                            databaseReference.child("Roller Shade Control").child("App Request").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot appRequestSnapshot) {
                                    if (!isAdded()) {
                                        return;
                                    }

                                    String appRequestStringValue = appRequestSnapshot.getValue(String.class);
                                    if (!"0".equals(appRequestStringValue)) {
                                        makeNotification("A command is already in progress. Please wait until it is completed.", R.drawable.icon_notification);
                                        return;
                                    }

                                    databaseReference.child("Roller Shade Control").child("App Request").setValue("Down");

                                    makeNotification("Roller Shade - Closed By 10%", R.drawable.rs_close);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError appRequestError) {
                                    // Handle onCancelled event for App Request
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError stateError) {
                            // Handle onCancelled event for State
                        }
                    });
                }
            });
        }

    public void openByTenPercent(Button increaseBtn) {
            increaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if State value is acceptable
                    databaseReference.child("Roller Shade Control").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                            if (!isAdded()) {
                                return;
                            }

                            // Get the value of State
                            Float stateValue = stateSnapshot.getValue(Float.class);
                            // Check if the state is already fully closed
                            if (stateValue == 1) {
                                makeNotification("The roller shade is already fully open.", R.drawable.icon_notification);
                                return;
                            }

                            // Check if App Request value is acceptable
                            databaseReference.child("Roller Shade Control").child("App Request").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot appRequestSnapshot) {
                                    if (!isAdded()) {
                                        return;
                                    }

                                    String appRequestStringValue = appRequestSnapshot.getValue(String.class);
                                    if (!"0".equals(appRequestStringValue)) {
                                        makeNotification("A command is already in progress. Please wait until it is completed.", R.drawable.icon_notification);
                                        return;
                                    }

                                    // Continue with the command execution
                                    databaseReference.child("Roller Shade Control").child("App Request").setValue("Up");

                                    makeNotification("Roller Shade - Opened By 10%", R.drawable.rs_open);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError appRequestError) {
                                    // Handle onCancelled event for App Request
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError stateError) {
                            // Handle onCancelled event for State
                        }
                    });
                }
            });
        }

    public void updatePercentOCValue(EditText percentOCValue) {
        databaseReference.child("Roller Shade Control").child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                if (!isAdded()) {
                    return;
                }

                // Check if the State value is available
                Float stateValue = stateSnapshot.getValue(Float.class);
                if (stateValue != null) {
                    // Convert state value to percentage format
                    int statePercentage = (int) (stateValue * 100);

                    // Set the percentage value to the percentOCValue EditText
                    percentOCValue.setText(statePercentage + "%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError stateError) {
                // Handle onCancelled event for State
            }
        });
    }

    public void updatePositionIndicator(TextView positionIndicator) {
        databaseReference.child("Roller Shade Control").child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot stateSnapshot) {

                if (!isAdded()) {
                    return;
                }

                // Get the value of State
                Float stateValue = stateSnapshot.getValue(Float.class);

                // Check if the state value is available
                if (stateValue != null) {
                    String positionText;
                    // Convert state value to percentage format
                    int statePercentage = (int) (stateValue * 100);

                    // Check if the state is fully closed
                    if (stateValue == 0) {
                        positionText = "Position: Closed - Fully";
                    } else if (stateValue == 1) {
                        positionText = "Position: Open - Fully";
                    } else {
                        positionText = "Position: Open - " + statePercentage + "%";
                    }

                    // Set the position text to the firstPositionIndicator TextView
                    positionIndicator.setText(positionText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError stateError) {
                // Handle onCancelled event for State
            }
        });
    }

    public void updateButtonColor(Button button) {
        databaseReference.child("Roller Shade Control").child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                if (!isAdded()) {
                    return;
                }

                // Get the value of State
                Float stateValue = stateSnapshot.getValue(Float.class);

                // Check if the state is fully closed
                if (stateValue == 0) {
                    button.setBackgroundTintList(ColorStateList.valueOf(requireContext().getResources().getColor(R.color.THEMECOLOR)));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(requireContext().getResources().getColor(R.color.GREEN)));
                }
            }

                @Override
                public void onCancelled (@NonNull DatabaseError stateError){
                    // Handle onCancelled event for State
                }
            });
    }




    public void restoreAppPreferencesHomeScreen(Button button, TextView positionIndicator, String devicePath) {
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() != null && isAdded()) { // Check if fragment is attached to an activity
                    if (snapshot.exists()) {

                        String roomName = snapshot.child("Room Name").getValue(String.class);
                        if (roomName != null) {
                            button.setText(roomName);
                        }

                        Integer selectedIcon = snapshot.child("Selected Icon").getValue(Integer.class);
                        if (selectedIcon != null) {
                            if (selectedIcon == 1) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
                            } else if (selectedIcon == 2) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
                            } else if (selectedIcon == 3) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
                            } else if (selectedIcon == 4) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
                            }
                        }

                        Integer btnVisibility = snapshot.child("Visibility").getValue(Integer.class);
                        if (btnVisibility != null) {
                            if (btnVisibility == 0) {
                                button.setVisibility(View.VISIBLE);
                            } else if (btnVisibility == 1) {
                                button.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveAppPreferencesHomeScreen(String devicePath, String roomName, Integer iconSelected, String positionText, Integer btnColor, Integer btnVisibility ) {
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Room Name").setValue(roomName);
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Selected Icon").setValue(iconSelected);
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Position").setValue(positionText);
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Color").setValue(btnColor);
        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Visibility").setValue(btnVisibility);
    }

//    public void saveAppPreferencesHomeScreen(String devicePath, String roomName, Integer iconSelected, String positionText, Integer btnColor, Integer btnVisibility ) {
//        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Room Name").setValue(roomName);
//        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Selected Icon").setValue(iconSelected);
//        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Position").setValue(positionText);
//        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Color").setValue(btnColor);
//        databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child(devicePath).child("Visibility").setValue(btnVisibility);
//    }

    public void addDeviceBtn() {
        addDeviceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_device, null);
                EditText roomNameField = dialogView.findViewById(R.id.roomNameField);
                ImageButton livingRoomIcon = dialogView.findViewById(R.id.livingRoomIcon);
                ImageButton bedroomIcon = dialogView.findViewById(R.id.bedroomIcon);
                ImageButton kitchenIcon = dialogView.findViewById(R.id.kitchenIcon);
                ImageButton bathroomIcon = dialogView.findViewById(R.id.bathroomIcon);
                Button addBtn = dialogView.findViewById(R.id.addBtn);
                Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                View.OnClickListener iconClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetIconColors();
                        v.setSelected(!v.isSelected());

                        // Set the selected icon value based on the clicked icon
                        if (v.isSelected()) {
                            if (v.getId() == R.id.livingRoomIcon) {
                                selectedIcon = 1; // Set the integer value for living room icon
                            } else if (v.getId() == R.id.bedroomIcon) {
                                selectedIcon = 2; // Set the integer value for bedroom icon
                            } else if (v.getId() == R.id.kitchenIcon) {
                                selectedIcon = 3; // Set the integer value for kitchen icon
                            } else if (v.getId() == R.id.bathroomIcon) {
                                selectedIcon = 4; // Set the integer value for bathroom icon
                            } else {
                                selectedIcon = -1; // Default value
                            }
                            v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.GREEN)));
                        } else {
                            v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                        }
                    }

                    private void resetIconColors() {
                        livingRoomIcon.setSelected(false);
                        livingRoomIcon.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));

                        bedroomIcon.setSelected(false);
                        bedroomIcon.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));

                        kitchenIcon.setSelected(false);
                        kitchenIcon.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));

                        bathroomIcon.setSelected(false);
                        bathroomIcon.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                    }
                };

                livingRoomIcon.setOnClickListener(iconClickListener);
                bedroomIcon.setOnClickListener(iconClickListener);
                kitchenIcon.setOnClickListener(iconClickListener);
                bathroomIcon.setOnClickListener(iconClickListener);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roomName = roomNameField.getText().toString();

                        if (firstGridBtn.getVisibility() == View.GONE) {
                            firstGridBtn.setText(roomName);
                            firstPositionIndicator.setText("Position:");

                            String firstPositionText = firstPositionIndicator.getText().toString();

                            if (selectedIcon == 1) {
                                firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
                            } else if (selectedIcon == 2) {
                                firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
                            } else if (selectedIcon == 3) {
                                firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
                            } else if (selectedIcon == 4) {
                                firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
                            }

                            firstSelectedIcon = selectedIcon;

                            databaseReference.child("Users").child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control").child("App Request").setValue(1);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Fully").setValue(0);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Closed");
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control").child("State").setValue(0);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            firstGridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));;
                            int firstBtnColor = 0;
                            firstGridBtn.setVisibility(View.VISIBLE);
                            int firstBtnVisibility = firstGridBtn.getVisibility();

                            String databasePath = "firstDeviceBtn";
                            saveAppPreferencesHomeScreen(databasePath, roomName, firstSelectedIcon, firstPositionText, firstBtnColor, firstBtnVisibility);
                            dialog.dismiss();

                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.GONE) {
                            secondGridBtn.setText(roomName);
                            secondPositionIndicator.setText("Position: N/A");

                            String secondPositionText = secondPositionIndicator.getText().toString();

                            if (selectedIcon == 1) {
                                secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
                            } else if (selectedIcon == 2) {
                                secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
                            } else if (selectedIcon == 3) {
                                secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
                            } else if (selectedIcon == 4) {
                                secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
                            }

                            secondSelectedIcon = selectedIcon;

                            databaseReference.child("Users").child("Roller Shade Control 2").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 2").child("App Request").setValue(1);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 2").child("Open-Close Fully").setValue(0);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 2").child("Open-Close Roller Shade").setValue("Closed");
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 2").child("State").setValue(0);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            secondGridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));;
                            int secondBtnColor = 0;
                            secondGridBtn.setVisibility(View.VISIBLE);
                            int secondBtnVisibility = firstGridBtn.getVisibility();

                            String databasePath = "secondDeviceBtn";
                            saveAppPreferencesHomeScreen(databasePath, roomName, secondSelectedIcon, secondPositionText, secondBtnColor, secondBtnVisibility);
                            dialog.dismiss();

                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.GONE) {
                            thirdGridBtn.setText(roomName);
                            thirdPositionIndicator.setText("Position: N/A");

                            String thirdPositionText = thirdPositionIndicator.getText().toString();

                            if (selectedIcon == 1) {
                                thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
                            } else if (selectedIcon == 2) {
                                thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
                            } else if (selectedIcon == 3) {
                                thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
                            } else if (selectedIcon == 4) {
                                thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
                            }

                            thirdSelectedIcon = selectedIcon;

                            databaseReference.child("Users").child("Roller Shade Control 3").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 3").child("App Request").setValue(1);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 3").child("Open-Close Fully").setValue(0);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 3").child("Open-Close Roller Shade").setValue("Closed");
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 3").child("State").setValue(0);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            thirdGridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));;
                            int thirdBtnColor = 0;
                            thirdGridBtn.setVisibility(View.VISIBLE);
                            int thirdBtnVisibility = thirdGridBtn.getVisibility();

                            String databasePath = "thirdDeviceBtn";
                            saveAppPreferencesHomeScreen(databasePath, roomName, thirdSelectedIcon, thirdPositionText, thirdBtnColor, thirdBtnVisibility);
                            dialog.dismiss();

                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.VISIBLE && fourthGridBtn.getVisibility() == View.GONE) {
                            fourthGridBtn.setText(roomName);
                            fourthPositionIndicator.setText("Position: N/A");

                            String fourthPositionText = fourthPositionIndicator.getText().toString();

                            if (selectedIcon == 1) {
                                fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
                            } else if (selectedIcon == 2) {
                                fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
                            } else if (selectedIcon == 3) {
                                fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
                            } else if (selectedIcon == 4) {
                                fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
                            }

                            fourthSelectedIcon = selectedIcon;

                            databaseReference.child("Users").child("Roller Shade Control 4").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 4").child("App Request").setValue(1);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 4").child("Open-Close Fully").setValue(0);
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 4").child("Open-Close Roller Shade").setValue("Closed");
                                    databaseReference.child("Users").child(uid).child("Roller Shade Control 4").child("State").setValue(0);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            fourthGridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));;
                            int fourthBtnColor = 0;
                            fourthGridBtn.setVisibility(View.VISIBLE);
                            int fourthBtnVisibility = firstGridBtn.getVisibility();

                            String databasePath = "fourthDeviceBtn";
                            saveAppPreferencesHomeScreen(databasePath, roomName, fourthSelectedIcon, fourthPositionText, fourthBtnColor, fourthBtnVisibility);
                            dialog.dismiss();
                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.VISIBLE && fourthGridBtn.getVisibility() == View.VISIBLE) {
                            makeNotification("Max devices reached. Please delete one or more devices before adding another", R.drawable.icon_notification);
                            Toast.makeText(requireContext(), "No more devices can be added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void deleteDevice(Button button) {
        // Hide the button
        button.setVisibility(View.GONE);

        if (button == firstGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control").removeValue();
            databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child("firstDeviceBtn").removeValue();
        } else if (button == secondGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 2").removeValue();
            databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child("secondDeviceBtn").removeValue();
        } else if (button == thirdGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 3").removeValue();
            databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child("thirdDeviceBtn").removeValue();
        } else if (button == fourthGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 4").removeValue();
            databaseReference.child("Users").child(uid).child("App Preferences - Home Screen").child("fourthDeviceBtn").removeValue();
        }
    }

    public void makeNotification(String contentText, int iconResourceId) {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(requireContext(), channelID);
        builder.setSmallIcon(iconResourceId)
                .setContentTitle("SyncShade")
                .setContentText(contentText)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(requireContext(), HomeFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

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

    public void toCommandScreen() {
        firstGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommandDialog();
            }
        });
        firstGridBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(firstGridBtn);
                return true;
            }
        });
        secondGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Device not registered", Toast.LENGTH_SHORT).show();
            }
        });
        secondGridBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(secondGridBtn);
                return true;
            }
        });
        thirdGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Device not registered", Toast.LENGTH_SHORT).show();
            }
        });
        thirdGridBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(thirdGridBtn);
                return true;
            }
        });
        fourthGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Device not registered", Toast.LENGTH_SHORT).show();
            }
        });
        fourthGridBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(fourthGridBtn);
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(Button button) {
        String message = "Are you sure you want to delete this device?";
        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Delete Device")
                .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+message+"</font>"))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(button);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    //    public void mimicMainRollerShade(String device) {
//        databaseReference.child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Integer appRequestValue = snapshot.child("App Request").getValue(Integer.class);
//                Integer ocFullyValue = snapshot.child("Open-Close Fully").getValue(Integer.class);
//                String ocRollerShadeValue = snapshot.child("Open-Close Roller Shade").getValue(String.class);
//
//                databaseReference.child("Users").child(uid).child(device).child("App Request").setValue(appRequestValue);
//                databaseReference.child("Users").child(uid).child(device).child("Open-Close Fully").setValue(ocFullyValue);
//                databaseReference.child("Users").child(uid).child(device).child("Open-Close Roller Shade").setValue(ocRollerShadeValue);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}