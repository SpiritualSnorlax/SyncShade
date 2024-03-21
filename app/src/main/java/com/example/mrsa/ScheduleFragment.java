package com.example.mrsa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    FloatingActionButton addScheduleBtn;
    Button firstScheduleBtn;
    Button secondScheduleBtn;
    Button thirdScheduleBtn;
    int firstScheduleBtnIcon;
    int secondScheduleBtnIcon;
    int thirdScheduleBtnIcon;
    TextView firstSchedule;
    TextView secondSchedule;
    TextView thirdSchedule;
    int selectedHour;
    int selectedMinute;
    private TimePickerDialog timePickerDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-services-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();

    private List<TimerTask> scheduledTasks = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        addScheduleBtn = rootView.findViewById(R.id.addScheduleBtn);
        firstScheduleBtn = rootView.findViewById(R.id.firstScheduleBtn);
        secondScheduleBtn = rootView.findViewById(R.id.secondScheduleBtn);
        thirdScheduleBtn = rootView.findViewById(R.id.thirdScheduleBtn);

        firstSchedule = rootView.findViewById(R.id.firstSchedule);
        secondSchedule = rootView.findViewById(R.id.secondSchedule);
        thirdSchedule = rootView.findViewById(R.id.thirdSchedule);

        scheduleCommands();
        deleteSchedule();

        restoreAppPreferencesScheduleScreen(firstScheduleBtn, firstSchedule, "firstScheduleBtn");
        restoreAppPreferencesScheduleScreen(secondScheduleBtn, secondSchedule, "secondScheduleBtn");
        restoreAppPreferencesScheduleScreen(thirdScheduleBtn, thirdSchedule, "thirdScheduleBtn");
        return rootView;
    }

    public void openFullyScheduledCommand() {
            databaseReference.child("Roller Shade Control").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot stateSnapshot) {
                    if (!isAdded()) {
                        Log.d("TAG", "Function is going into the !isAdded() conditional block");
                        return;
                    }
                    // Check if the State value is 1
                    Float stateValue = stateSnapshot.getValue(Float.class);
                    if (stateValue == 1) {
                        // Do not run the command if the State value is 1
                        makeNotification("The roller shade is already fully open. Scheduled command has been canceled.", R.drawable.icon_notification);
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

                            makeNotification("Scheduled command to fully open roller shade has been completed", R.drawable.rs_open);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError appRequestError) {
                            // Handle onCancelled event for App Request
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError appRequestError) {
                    // Handle onCancelled event for App Request
                }
            });
        }


    public void closeFullyScheduledCommand() {
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
                        makeNotification("The roller shade is already fully close. Scheduled command has been canceled.", R.drawable.icon_notification);
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

                            makeNotification("Scheduled command to fully close roller shade has been completed", R.drawable.rs_close);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError appRequestError) {
                            // Handle onCancelled event for App Request
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError appRequestError) {
                    // Handle onCancelled event for App Request
                }
            });
    }

    private void executeCommandBasedOnChipSelection(boolean isOpenFully) {
        if (isOpenFully) {
            openFullyScheduledCommand();
        } else {
            closeFullyScheduledCommand();
        }
    }

    public void scheduleCommands() {
        addScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_schedule_command, null);
                Chip chipOpenFully = dialogView.findViewById(R.id.chipOpenFully);
                Chip chipCloseFully = dialogView.findViewById(R.id.chipCloseFully);
                ChipGroup chipGroupDays  = dialogView.findViewById(R.id.chipGroupDays);
                Chip chipSunday = dialogView.findViewById(R.id.chipSunday);
                Chip chipMonday = dialogView.findViewById(R.id.chipMonday);
                Chip chipTuesday = dialogView.findViewById(R.id.chipTuesday);
                Chip chipWednesday = dialogView.findViewById(R.id.chipWednesday);
                Chip chipThursday = dialogView.findViewById(R.id.chipThursday);
                Chip chipFriday = dialogView.findViewById(R.id.chipFriday);
                Chip chipSaturday = dialogView.findViewById(R.id.chipSaturday);
                Button chooseTime = dialogView.findViewById(R.id.chooseTime);
                Button confirmScheduleBtn = dialogView.findViewById(R.id.confirmScheduleBtn);
                Button cancelScheduleBtn = dialogView.findViewById(R.id.cancelScheduleBtn);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                chipOpenFully.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            chipOpenFully.setChipBackgroundColorResource(R.color.GREEN);
                            chipCloseFully.setChecked(false);
                            chipCloseFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        } else {
                            chipOpenFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        }
                    }
                });

                chipCloseFully.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            chipCloseFully.setChipBackgroundColorResource(R.color.GREEN);
                            chipOpenFully.setChecked(false);
                            chipOpenFully.setChipBackgroundColorResource(R.color.THEMECOLOR);

                        } else {
                            chipCloseFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        }
                    }
                });

                chipGroupDays.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                    @Override
                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                        chipSunday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipMonday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipTuesday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipWednesday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipThursday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipFriday.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        chipSaturday.setChipBackgroundColorResource(R.color.THEMECOLOR);

                        for (int checkedId : chipGroupDays.getCheckedChipIds()) {

                            if (checkedId == R.id.chipSunday) {
                                chipSunday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if(checkedId == R.id.chipMonday) {
                                chipMonday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if (checkedId == R.id.chipTuesday) {
                                chipTuesday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if (checkedId == R.id.chipWednesday) {
                                chipWednesday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if (checkedId == R.id.chipThursday) {
                                chipThursday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if (checkedId == R.id.chipFriday) {
                                chipFriday.setChipBackgroundColorResource(R.color.GREEN);
                            } else if (checkedId == R.id.chipSaturday) {
                                chipSaturday.setChipBackgroundColorResource(R.color.GREEN);
                            }
                        }
                    }
                });

                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog = new TimePickerDialog(requireContext(),R.style.CustomTimePickerDialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedHour = hourOfDay;
                                selectedMinute = minute;

                                boolean isOpenFully = chipOpenFully.isChecked();

                                for (int checkedId : chipGroupDays.getCheckedChipIds()) {
                                    if (checkedId == R.id.chipSunday) {
                                        scheduleTask(Calendar.SUNDAY, hourOfDay, minute, isOpenFully);
                                    } else if(checkedId == R.id.chipMonday) {
                                        scheduleTask(Calendar.MONDAY, hourOfDay, minute, isOpenFully);
                                    } else if (checkedId == R.id.chipTuesday) {
                                        scheduleTask(Calendar.TUESDAY, hourOfDay, minute, isOpenFully);
                                    } else if (checkedId == R.id.chipWednesday) {
                                        scheduleTask(Calendar.WEDNESDAY, hourOfDay, minute, isOpenFully);
                                    } else if (checkedId == R.id.chipThursday) {
                                        scheduleTask(Calendar.THURSDAY, hourOfDay, minute, isOpenFully);
                                    } else if (checkedId == R.id.chipFriday) {
                                        scheduleTask(Calendar.FRIDAY, hourOfDay, minute, isOpenFully);
                                    } else if (checkedId == R.id.chipSaturday) {
                                        scheduleTask(Calendar.SATURDAY, hourOfDay, minute, isOpenFully);
                                    }
                                }
                            }
                        }, 15, 00, false);
                        timePickerDialog.show();
                    }
                });
                confirmScheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstScheduleBtn.getVisibility() == View.GONE) {
                            int iconState;
                            if (chipOpenFully.isChecked()) {
                                // Set the icon for chipOpenFully
                                firstScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_open, 0);
                                iconState = 0;
                                firstScheduleBtnIcon = iconState;
                            } else if (chipCloseFully.isChecked()) {
                                // Set the icon for chipCloseFully
                                firstScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_close, 0);
                                iconState = 1;
                                firstScheduleBtnIcon = iconState;
                            }


                            // Retrieve the hour and minute values from the TimePickerDialog
                            int hour = selectedHour;
                            int minute = selectedMinute;

                            String amPm;
                            if (hour >= 12) {
                                amPm = "p.m.";
                                if (hour > 12) {
                                    hour -= 12;
                                }
                            } else {
                                amPm = "a.m.";
                                if (hour == 0) {
                                    hour = 12;
                                }
                            }
                            String firstScheduleBtnTime = String.format("%2d:%02d %s", hour, minute, amPm);

                            String[] daysOfWeekText = new String[7]; // Array size 7 for days of the week
                            int index = 0;

                            for (int checkedId : chipGroupDays.getCheckedChipIds()) {
                                if (checkedId == R.id.chipSunday) {
                                    daysOfWeekText[index] = "Sun";
                                } else if (checkedId == R.id.chipMonday) {
                                    daysOfWeekText[index] = "Mon";
                                } else if (checkedId == R.id.chipTuesday) {
                                    daysOfWeekText[index] = "Tue";
                                } else if (checkedId == R.id.chipWednesday) {
                                    daysOfWeekText[index] = "Wed";
                                } else if (checkedId == R.id.chipThursday) {
                                    daysOfWeekText[index] = "Thurs";
                                } else if (checkedId == R.id.chipFriday) {
                                    daysOfWeekText[index] = "Fri";
                                } else if (checkedId == R.id.chipSaturday) {
                                    daysOfWeekText[index] = "Sat";
                                }
                                index++; // Move to the next position in the array
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (String day : daysOfWeekText) {
                                if (day != null) {
                                    stringBuilder.append(day).append(", "); // Append each day followed by a comma and space
                                }
                            }

                            // Remove the trailing comma and space
                            String concatenatedDays = stringBuilder.toString().trim();
                            if (concatenatedDays.endsWith(",")) {
                                concatenatedDays = concatenatedDays.substring(0, concatenatedDays.length() - 1);
                            }

                            // Set the text of the TextView to the concatenated string
                            firstSchedule.setText(concatenatedDays);
                            String daysOfWeekSelected = firstSchedule.getText().toString();


                            firstScheduleBtn.setText(firstScheduleBtnTime);
                            String time = firstScheduleBtn.getText().toString();

                            firstScheduleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                            int firstBtnColor = 0;

                            firstScheduleBtn.setVisibility(View.VISIBLE);
                            int firstScheduleBtnVisibility = firstScheduleBtn.getVisibility();

                            String databasePath = "firstScheduleBtn";
                            saveAppPreferencesScheduleScreen(databasePath, time, daysOfWeekSelected, firstBtnColor, firstScheduleBtnVisibility, firstScheduleBtnIcon);
                            makeNotification("Schedule Created", R.drawable.icon_schedule_created);
                            dialog.dismiss();

                        } else if (firstScheduleBtn.getVisibility() == View.VISIBLE && secondScheduleBtn.getVisibility() == View.GONE) {
                            int iconState;
                            if (chipOpenFully.isChecked()) {
                                // Set the icon for chipOpenFully
                                secondScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_open, 0);
                                iconState = 0;
                                secondScheduleBtnIcon = iconState;
                            } else if (chipCloseFully.isChecked()) {
                                // Set the icon for chipCloseFully
                                secondScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_close, 0);
                                iconState = 1;
                                secondScheduleBtnIcon = iconState;
                            }


                            // Retrieve the hour and minute values from the TimePickerDialog
                            int hour = selectedHour;
                            int minute = selectedMinute;

                            String amPm;
                            if (hour >= 12) {
                                amPm = "p.m.";
                                if (hour > 12) {
                                    hour -= 12;
                                }
                            } else {
                                amPm = "a.m.";
                                if (hour == 0) {
                                    hour = 12;
                                }
                            }
                            String secondScheduleBtnTime = String.format("%2d:%02d %s", hour, minute, amPm);

                            String[] daysOfWeekText = new String[7]; // Array size 7 for days of the week
                            int index = 0;

                            for (int checkedId : chipGroupDays.getCheckedChipIds()) {
                                if (checkedId == R.id.chipSunday) {
                                    daysOfWeekText[index] = "Sun";
                                } else if (checkedId == R.id.chipMonday) {
                                    daysOfWeekText[index] = "Mon";
                                } else if (checkedId == R.id.chipTuesday) {
                                    daysOfWeekText[index] = "Tue";
                                } else if (checkedId == R.id.chipWednesday) {
                                    daysOfWeekText[index] = "Wed";
                                } else if (checkedId == R.id.chipThursday) {
                                    daysOfWeekText[index] = "Thurs";
                                } else if (checkedId == R.id.chipFriday) {
                                    daysOfWeekText[index] = "Fri";
                                } else if (checkedId == R.id.chipSaturday) {
                                    daysOfWeekText[index] = "Sat";
                                }
                                index++; // Move to the next position in the array
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (String day : daysOfWeekText) {
                                if (day != null) {
                                    stringBuilder.append(day).append(", "); // Append each day followed by a comma and space
                                }
                            }

                            // Remove the trailing comma and space
                            String concatenatedDays = stringBuilder.toString().trim();
                            if (concatenatedDays.endsWith(",")) {
                                concatenatedDays = concatenatedDays.substring(0, concatenatedDays.length() - 1);
                            }

                            // Set the text of the TextView to the concatenated string
                            secondSchedule.setText(concatenatedDays);
                            String daysOfWeekSelected = secondSchedule.getText().toString();


                            secondScheduleBtn.setText(secondScheduleBtnTime);
                            String time = secondScheduleBtn.getText().toString();

                            secondScheduleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                            int secondBtnColor = 0;

                            secondScheduleBtn.setVisibility(View.VISIBLE);
                            int secondScheduleBtnVisibility = secondScheduleBtn.getVisibility();

                            String databasePath = "secondScheduleBtn";
                            saveAppPreferencesScheduleScreen(databasePath, time, daysOfWeekSelected, secondBtnColor, secondScheduleBtnVisibility, secondScheduleBtnIcon);
                            makeNotification("Schedule Created", R.drawable.icon_schedule_created);
                            dialog.dismiss();

                        } else if (firstScheduleBtn.getVisibility() == View.VISIBLE && secondScheduleBtn.getVisibility() == View.VISIBLE) {
                            int iconState;
                            if (chipOpenFully.isChecked()) {
                                // Set the icon for chipOpenFully
                                thirdScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_open, 0);
                                iconState = 0;
                                thirdScheduleBtnIcon = iconState;
                            } else if (chipCloseFully.isChecked()) {
                                // Set the icon for chipCloseFully
                                thirdScheduleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_close, 0);
                                iconState = 1;
                                thirdScheduleBtnIcon = iconState;
                            }


                            // Retrieve the hour and minute values from the TimePickerDialog
                            int hour = selectedHour;
                            int minute = selectedMinute;

                            String amPm;
                            if (hour >= 12) {
                                amPm = "p.m.";
                                if (hour > 12) {
                                    hour -= 12;
                                }
                            } else {
                                amPm = "a.m.";
                                if (hour == 0) {
                                    hour = 12;
                                }
                            }
                            String thirdScheduleBtnTime = String.format("%2d:%02d %s", hour, minute, amPm);

                            String[] daysOfWeekText = new String[7]; // Array size 7 for days of the week
                            int index = 0;

                            for (int checkedId : chipGroupDays.getCheckedChipIds()) {
                                if (checkedId == R.id.chipSunday) {
                                    daysOfWeekText[index] = "Sun";
                                } else if (checkedId == R.id.chipMonday) {
                                    daysOfWeekText[index] = "Mon";
                                } else if (checkedId == R.id.chipTuesday) {
                                    daysOfWeekText[index] = "Tue";
                                } else if (checkedId == R.id.chipWednesday) {
                                    daysOfWeekText[index] = "Wed";
                                } else if (checkedId == R.id.chipThursday) {
                                    daysOfWeekText[index] = "Thurs";
                                } else if (checkedId == R.id.chipFriday) {
                                    daysOfWeekText[index] = "Fri";
                                } else if (checkedId == R.id.chipSaturday) {
                                    daysOfWeekText[index] = "Sat";
                                }
                                index++; // Move to the next position in the array
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (String day : daysOfWeekText) {
                                if (day != null) {
                                    stringBuilder.append(day).append(", "); // Append each day followed by a comma and space
                                }
                            }

                            // Remove the trailing comma and space
                            String concatenatedDays = stringBuilder.toString().trim();
                            if (concatenatedDays.endsWith(",")) {
                                concatenatedDays = concatenatedDays.substring(0, concatenatedDays.length() - 1);
                            }

                            // Set the text of the TextView to the concatenated string
                            thirdSchedule.setText(concatenatedDays);
                            String daysOfWeekSelected = thirdSchedule.getText().toString();


                            thirdScheduleBtn.setText(thirdScheduleBtnTime);
                            String time = thirdScheduleBtn.getText().toString();

                            thirdScheduleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                            int thirdBtnColor = 0;

                            thirdScheduleBtn.setVisibility(View.VISIBLE);
                            int thirdScheduleBtnVisibility = thirdScheduleBtn.getVisibility();

                            String databasePath = "thirdScheduleBtn";
                            saveAppPreferencesScheduleScreen(databasePath, time, daysOfWeekSelected, thirdBtnColor, thirdScheduleBtnVisibility, thirdScheduleBtnIcon);
                            makeNotification("Schedule Created", R.drawable.icon_schedule_created);
                            dialog.dismiss();
                        }
                    }
                });
                cancelScheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void deleteScheduleFromScreenAndDatabase(Button button) {
        button.setVisibility(View.GONE);

        if (button == firstScheduleBtn) {
            databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child("firstScheduleBtn").removeValue();
            deleteScheduledTasks();
        } else if (button == secondScheduleBtn) {
            databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child("secondScheduleBtn").removeValue();
            deleteScheduledTasks();
        } else if (button == thirdScheduleBtn) {
            databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child("thirdScheduleBtn").removeValue();
            deleteScheduledTasks();
        }
    }

    private void deleteSchedule(){
        firstScheduleBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(firstScheduleBtn);
                return true;
            }
        });
        secondScheduleBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(secondScheduleBtn);
                return true;
            }
        });
        thirdScheduleBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(thirdScheduleBtn);
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(Button button) {
        String message = "Are you sure you want to delete this schedule?";
        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Delete Schedule")
                .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+message+"</font>"))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteScheduleFromScreenAndDatabase(button);
                        makeNotification("Schedule Deleted", R.drawable.icon_schedule_deleted);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void saveAppPreferencesScheduleScreen(String devicePath, String time, String daysOfWeekSelected, Integer btnColor, Integer btnVisibility, Integer btnIcon) {
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).child("Time").setValue(time);
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).child("Days of Week").setValue(daysOfWeekSelected);
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).child("Color").setValue(btnColor);
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).child("Btn Visibility").setValue(btnVisibility);
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).child("Btn Icon").setValue(btnIcon);
    }

    public void restoreAppPreferencesScheduleScreen(Button button, TextView daysOfWeekIndicator, String devicePath) {
        databaseReference.child("Users").child(uid).child("App Preferences - Schedule Screen").child(devicePath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() != null && isAdded()) { // Check if fragment is attached to an activity
                    if (snapshot.exists()) {
                        String time = snapshot.child("Time").getValue(String.class);
                        if (time != null) {
                            button.setText(time);
                        }

                        Integer btnIcon = snapshot.child("Btn Icon").getValue(Integer.class);
                        if (btnIcon != null) {
                            if (btnIcon == 0) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_open, 0);
                            } else if (btnIcon == 1) {
                                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rs_close, 0);
                            }
                        }

                        String daysOfWeekSelected = snapshot.child("Days of Week").getValue(String.class);
                        if (daysOfWeekSelected != null) {
                            daysOfWeekIndicator.setText(daysOfWeekSelected);
                        }

                        Integer btnColor = snapshot.child("Color").getValue(Integer.class);
                        if (btnColor != null) {
                            if (btnColor == 0) {
                                button.setBackgroundTintList(ColorStateList.valueOf(requireContext().getResources().getColor(R.color.THEMECOLOR)));
                            }
                        }

                        Integer btnVisibility = snapshot.child("Btn Visibility").getValue(Integer.class);
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

    private void scheduleTask(int dayOfWeek, int hourOfDay, int minute, boolean isOpenFully) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // Execute the appropriate command
                executeCommandBasedOnChipSelection(isOpenFully);
            }
        };
        timer.schedule(timerTask, calculateDelay(dayOfWeek, hourOfDay, minute));

        // Add the scheduled task to the list
        scheduledTasks.add(timerTask);
    }

    private void deleteScheduledTasks() {
        // Iterate through the list of scheduled tasks and cancel each one
        for (TimerTask task : scheduledTasks) {
            task.cancel();
        }
        // Clear the list of scheduled tasks
        scheduledTasks.clear();
    }

    private long calculateDelay(int dayOfWeek, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay /* specify hour here */);
        calendar.set(Calendar.MINUTE, minute /* specify minute here */);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTimeMillis = System.currentTimeMillis();
        long scheduledTimeMillis = calendar.getTimeInMillis();

        // Calculate delay until the next occurrence of the scheduled time
        long delay = scheduledTimeMillis - currentTimeMillis;
        if (delay < 0) {
            // If the scheduled time has already passed this week, schedule it for the next week
            delay += TimeUnit.DAYS.toMillis(7);
        }
        return delay;
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

}