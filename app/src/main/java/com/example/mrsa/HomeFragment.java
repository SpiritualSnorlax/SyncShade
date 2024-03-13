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
    SharedPreferences sharedPreferences;


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
        sharedPreferences = getActivity().getSharedPreferences("ButtonVisibility", Context.MODE_PRIVATE);

        restoreButtonVisibility();
        addDeviceBtn();
        toCommandScreen();

        return rootView;
    }
    public void sendCommandDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_command, null);
        Button openBtn = dialogView.findViewById(R.id.openBtn);
        Button closeBtn = dialogView.findViewById(R.id.closeBtn);
        SeekBar seekBar = dialogView.findViewById(R.id.seekBar);
        Chip decreaseBtn = dialogView.findViewById(R.id.decreaseBtn);
        Chip increaseBtn = dialogView.findViewById(R.id.increaseBtn);
        EditText seekBarValue = dialogView.findViewById(R.id.seekBarValue);
        Button scheduleBtn = dialogView.findViewById(R.id.scheduleBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        openFullyCommand(openBtn, seekBar, seekBarValue);
        closeFullyCommand(closeBtn, seekBar, seekBarValue);
        closeByTenPercent(decreaseBtn, seekBar, seekBarValue);
        openByTenPercent(increaseBtn, seekBar, seekBarValue);
    }

    public void mimicMainRollerShade(String device) {
        databaseReference.child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer appRequestValue = snapshot.child("App Request").getValue(Integer.class);
                Integer ocFullyValue = snapshot.child("Open-Close Fully").getValue(Integer.class);
                String ocRollerShadeValue = snapshot.child("Open-Close Roller Shade").getValue(String.class);

                databaseReference.child("Users").child(uid).child(device).child("App Request").setValue(appRequestValue);
                databaseReference.child("Users").child(uid).child(device).child("Open-Close Fully").setValue(ocFullyValue);
                databaseReference.child("Users").child(uid).child(device).child("Open-Close Roller Shade").setValue(ocRollerShadeValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void openFullyCommand(Button openBtn, SeekBar seekBar, EditText seekBarValue) {
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Users").child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int appRequestValue = 1;
                        int ocFullyValue = 1;
                        String ocRollerShadeValue = "Opened";

                        databaseReference.child("Roller Shade Control").child("App Request").setValue(appRequestValue);
                        databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(ocFullyValue);
                        databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue(ocRollerShadeValue);

                        mimicMainRollerShade("Roller Shade Control");
                        makeNotification("Roller Shade - Opened Fully", R.drawable.rs_open);
                        changeButtonColor(firstPositionIndicator, firstGridBtn, "Roller Shade Control", "Position: Opened - Fully");
                        saveButtonVisibility();
                        seekBar.setProgress(10);
                        seekBarValue.setText("100%");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    public void closeFullyCommand(Button closeBtn, SeekBar seekBar, EditText seekBarValue) {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Users").child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int appRequestValue = 1;
                        int ocFullyValue = 0;
                        String ocRollerShadeValue = "Closed";

                        databaseReference.child("Roller Shade Control").child("App Request").setValue(appRequestValue);
                        databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(ocFullyValue);
                        databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue(ocRollerShadeValue);

                        seekBar.setProgress(0);
                        seekBarValue.setText("0%");
                        makeNotification("Roller Shade - Closed Fully", R.drawable.rs_close);
                        changeButtonColor(firstPositionIndicator, firstGridBtn, "Roller Shade Control", "Position: Closed - Fully");
                        mimicMainRollerShade("Roller Shade Control");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public void closeByTenPercent(Button decreaseBtn, SeekBar seekBar, EditText seekBarValue) {
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() - 1);
                seekBarValue.setText(seekBar.getProgress() + "0%");

                if (seekBar.getProgress() == 0) {
                    seekBarValue.setText("0%");
                }

                readSeekBarValue(seekBarValue, decreaseBtn);
                makeNotification("Roller Shade - Closed By 10%", R.drawable.rs_close);
                mimicMainRollerShade("Roller Shade Control");

            }
        });
    }

    public void openByTenPercent(Button increaseBtn, SeekBar seekBar, EditText seekBarValue) {
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() + 1);
                seekBarValue.setText(seekBar.getProgress() + "0%");
                readSeekBarValue(seekBarValue, increaseBtn);
                makeNotification("Roller Shade - Opened By 10%", R.drawable.rs_open);
                mimicMainRollerShade("Roller Shade Control");
            }
        });
    }

    public void readSeekBarValue(EditText seekBarValue, Button button) {
        String text = seekBarValue.getText().toString();
        double percentage = Double.parseDouble(text.replace("%", "")) / 100.0;
        double state = percentage;

        if (!button.isPressed()) {
            databaseReference.child("Roller Shade Control").child("App Request").setValue(0);
        } else {
            databaseReference.child("Roller Shade Control").child("App Request").setValue(1);
        }

        if (percentage == 0) {
            databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(0);
            databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Closed");
        } else {
            databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(1);
            databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Opened");
        }
        databaseReference.child("Roller Shade Control").child("State").setValue(state);

        databaseReference.child("Roller Shade Control").child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double newState = dataSnapshot.getValue(Double.class);
                if (newState == state) {
                    databaseReference.child("Roller Shade Control").child("App Request").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


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
                            themeButtonColor(firstGridBtn);
                            firstGridBtn.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.GONE) {
                            secondGridBtn.setText(roomName);
                            secondPositionIndicator.setText("Position: N/A");

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
                            themeButtonColor(secondGridBtn);
                            secondGridBtn.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.GONE) {
                            thirdGridBtn.setText(roomName);
                            thirdPositionIndicator.setText("Position: N/A");

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
                            themeButtonColor(thirdGridBtn);
                            thirdGridBtn.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.VISIBLE && fourthGridBtn.getVisibility() == View.GONE) {
                            fourthGridBtn.setText(roomName);
                            fourthPositionIndicator.setText("Position: N/A");

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
                            themeButtonColor(fourthGridBtn);
                            fourthGridBtn.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                        saveButtonVisibility();
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

    private void restoreButtonVisibility() {
        firstGridBtn.setVisibility(sharedPreferences.getBoolean("firstGridBtn", false) ? View.VISIBLE : View.GONE);
        firstGridBtn.setText(sharedPreferences.getString("firstGridBtnRoomName", ""));
        firstPositionIndicator.setText(sharedPreferences.getString("firstGridBtnPosition", ""));
        firstSelectedIcon = sharedPreferences.getInt("firstGridBtnIcon", -1);
        setCompoundDrawable(firstGridBtn, firstSelectedIcon);
        themeButtonColor(firstGridBtn);

        secondGridBtn.setVisibility(sharedPreferences.getBoolean("secondGridBtn", false) ? View.VISIBLE : View.GONE);
        secondGridBtn.setText(sharedPreferences.getString("secondGridBtnRoomName", ""));
        secondPositionIndicator.setText(sharedPreferences.getString("secondGridBtnPosition", ""));
        secondSelectedIcon = sharedPreferences.getInt("secondGridBtnIcon", -1);
        setCompoundDrawable(secondGridBtn, secondSelectedIcon);
        themeButtonColor(secondGridBtn);

        thirdGridBtn.setVisibility(sharedPreferences.getBoolean("thirdGridBtn", false) ? View.VISIBLE : View.GONE);
        thirdGridBtn.setText(sharedPreferences.getString("thirdGridBtnRoomName", ""));
        thirdPositionIndicator.setText(sharedPreferences.getString("thirdGridBtnPosition", ""));
        thirdSelectedIcon = sharedPreferences.getInt("thirdGridBtnIcon", -1);
        setCompoundDrawable(thirdGridBtn, thirdSelectedIcon);
        themeButtonColor(thirdGridBtn);

        fourthGridBtn.setVisibility(sharedPreferences.getBoolean("fourthGridBtn", false) ? View.VISIBLE : View.GONE);
        fourthGridBtn.setText(sharedPreferences.getString("fourthGridBtnRoomName", ""));
        fourthPositionIndicator.setText(sharedPreferences.getString("fourthGridBtnPosition", ""));
        fourthSelectedIcon = sharedPreferences.getInt("fourthGridBtnIcon", -1);
        setCompoundDrawable(fourthGridBtn, fourthSelectedIcon);
        themeButtonColor(fourthGridBtn);
    }

    private void setCompoundDrawable(Button button, int selectedIcon) {
        if (selectedIcon == 1) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_living_room, 0);
        } else if (selectedIcon == 2) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bedroom, 0);
        } else if (selectedIcon == 3) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_kitchen, 0);
        } else if (selectedIcon == 4) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.large_icon_bathroom, 0);
        } else {
            // Set default compound drawable if no icon is selected
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void saveButtonVisibility() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("firstGridBtn", firstGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("firstGridBtnRoomName", firstGridBtn.getText().toString());
        editor.putString("firstGridBtnPosition", firstPositionIndicator.getText().toString());
        editor.putInt("firstGridBtnIcon", firstSelectedIcon);

        editor.putBoolean("secondGridBtn", secondGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("secondGridBtnRoomName", secondGridBtn.getText().toString());
        editor.putString("secondGridBtnPosition", secondPositionIndicator.getText().toString());
        editor.putInt("secondGridBtnIcon", secondSelectedIcon);

        editor.putBoolean("thirdGridBtn", thirdGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("thirdGridBtnRoomName", thirdGridBtn.getText().toString());
        editor.putString("thirdGridBtnPosition", thirdPositionIndicator.getText().toString());
        editor.putInt("thirdGridBtnIcon", thirdSelectedIcon);

        editor.putBoolean("fourthGridBtn", fourthGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("fourthGridBtnRoomName", fourthGridBtn.getText().toString());
        editor.putString("fourthGridBtnPosition", fourthPositionIndicator.getText().toString());
        editor.putInt("fourthGridBtnIcon", fourthSelectedIcon);

        editor.apply();
    }

    private void deleteDevice(Button button) {
        // Hide the button
        button.setVisibility(View.GONE);
        saveButtonVisibility();

        if (button == firstGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control").removeValue();
        } else if (button == secondGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 2").removeValue();
        } else if (button == thirdGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 3").removeValue();
        } else if (button == fourthGridBtn) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control 4").removeValue();
        }
    }

    public void themeButtonColor(Button gridBtn) {
        gridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
    }

    public void changeButtonColor(TextView positionIndicator, Button gridBtn, String device, String position) {
        databaseReference.child(device).child("Open-Close Roller Shade")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String rollerShadeState = dataSnapshot.getValue(String.class);
                            if (rollerShadeState != null) {
                                if (rollerShadeState.equals("Opened")) {
                                    positionIndicator.setText(position);
                                    gridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.GREEN)));
                                } if (rollerShadeState.equals("Closed")) {
                                    positionIndicator.setText(position);
                                    gridBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Device")
                .setMessage("Are you sure you want to delete this device?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(button);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}