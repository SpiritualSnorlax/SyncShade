package com.example.mrsa;

import static android.graphics.BlendMode.COLOR;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
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
    Button fourthScheduleBtn;
    Button fifthScheduleBtn;
    Switch firstScheduleSwitch;
    Switch secondScheduleSwitch;
    Switch thirdScheduleSwitch;
    Switch fourthScheduleSwitch;
    Switch fifthScheduleSwitch;

    TextView daysOfWeek;

    int selectedHour;
    int selectedMinute;

    private TimePickerDialog timePickerDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-services-default-rtdb.firebaseio.com/");

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

        firstScheduleSwitch = rootView.findViewById(R.id.firstScheduleSwitch);
        secondScheduleSwitch = rootView.findViewById(R.id.secondScheduleSwitch);
        thirdScheduleSwitch = rootView.findViewById(R.id.thirdScheduleSwitch);

        daysOfWeek = rootView.findViewById(R.id.daysOfWeek);

        scheduleCommands();
        return rootView;
    }

    public void openFullyScheduledCommand() {
        databaseReference.child("Users").child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int appRequestValue = 1;
                int ocFullyValue = 1;
                String ocRollerShadeValue = "Opened";

                databaseReference.child("Roller Shade Control").child("App Request").setValue(appRequestValue);
                databaseReference.child("Roller Shade Control").child("Open-Close Fully").setValue(ocFullyValue);
                databaseReference.child("Roller Shade Control").child("Open-Close Roller Shade").setValue(ocRollerShadeValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                            // ChipOpenFully is checked, set its background color to GREEN
                            chipOpenFully.setChipBackgroundColorResource(R.color.GREEN);
                            // Uncheck ChipCloseFully if it is checked
                            chipCloseFully.setChecked(false);
                            // Set its background color back to THEMECOLOR
                            chipCloseFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        } else {
                            // ChipOpenFully is unchecked, set its background color back to THEMECOLOR
                            chipOpenFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        }
                    }
                });

                chipCloseFully.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // ChipCloseFully is checked, set its background color to GREEN
                            chipCloseFully.setChipBackgroundColorResource(R.color.GREEN);
                            // Uncheck ChipOpenFully if it is checked
                            chipOpenFully.setChecked(false);
                            // Set its background color back to THEMECOLOR
                            chipOpenFully.setChipBackgroundColorResource(R.color.THEMECOLOR);
                        } else {
                            // ChipCloseFully is unchecked, set its background color back to THEMECOLOR
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
                        timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedHour = hourOfDay;
                                selectedMinute = minute;

                                for (int checkedId : chipGroupDays.getCheckedChipIds()) {

                                    if (checkedId == R.id.chipSunday) {
                                        scheduleTask(Calendar.SUNDAY, hourOfDay, minute);
                                    } else if(checkedId == R.id.chipMonday) {
                                        scheduleTask(Calendar.MONDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipTuesday) {
                                        scheduleTask(Calendar.TUESDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipWednesday) {
                                        scheduleTask(Calendar.WEDNESDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipThursday) {
                                        scheduleTask(Calendar.THURSDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipFriday) {
                                        scheduleTask(Calendar.FRIDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipSaturday) {
                                        scheduleTask(Calendar.SATURDAY, hourOfDay, minute);
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
                        // Retrieve the hour and minute values from the TimePickerDialog
                        int hour = selectedHour;
                        int minute = selectedMinute;

                        String amPm;
                        if (hour >= 12) {
                            amPm = "PM";
                            if (hour > 12) {
                                hour -= 12;
                            }
                        } else {
                            amPm = "AM";
                            if (hour == 0) {
                                hour = 12;
                            }
                        }
                        String firstScheduleBtnTime = String.format("%2d:%02d %s", hour, minute, amPm);


                        String daysOfWeekText = "S M T W T F S";
                        SpannableString spannableString = new SpannableString(daysOfWeekText);
                        int color = Color.GREEN;

                        for (int checkedId : chipGroupDays.getCheckedChipIds()) {
                            int start = 0;
                            int end = 0;

                            if (checkedId == R.id.chipSunday) {
                                start = 0;
                                end = 1;
                            } else if(checkedId == R.id.chipMonday) {
                                start = 2;
                                end = 3;
                            } else if (checkedId == R.id.chipTuesday) {
                                start = 4;
                                end = 5;
                            } else if (checkedId == R.id.chipWednesday) {
                                start = 6;
                                end = 7;
                            } else if (checkedId == R.id.chipThursday) {
                                start = 8;
                                end = 9;
                            } else if (checkedId == R.id.chipFriday) {
                                start = 10;
                                end = 11;
                            } else if (checkedId == R.id.chipSaturday) {
                                start = 12;
                                end = 13;
                            }
                            spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        daysOfWeek.setText(spannableString);
                        firstScheduleBtn.setText(firstScheduleBtnTime);
                        firstScheduleSwitch.setChecked(true);
                        firstScheduleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                        firstScheduleBtn.setVisibility(View.VISIBLE);


                    }
                });
            }
        });
    }

    private void scheduleTask(int dayOfWeek, int hourOfDay, int minute) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Activate your function here
                // This code will be executed at the specified day and time
                // Execute the function or perform any desired action
                openFullyScheduledCommand();
            }
        }, calculateDelay(dayOfWeek, hourOfDay, minute));
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

}