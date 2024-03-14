package com.example.mrsa;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

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

    public void scheduleCommands(Button scheduleBtn) {
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_schedule_command, null);
                ChipGroup chipGroup  = dialogView.findViewById(R.id.chipGroup);
                Button chooseTime = dialogView.findViewById(R.id.chooseTime);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                for (int checkedId : chipGroup.getCheckedChipIds()) {
                                    if (checkedId == R.id.chipMonday) {
                                        scheduleTask(Calendar.MONDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipTuesday) {
                                        scheduleTask(Calendar.TUESDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipWednesday) {
                                        scheduleTask(Calendar.WEDNESDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipThursday) {
                                        scheduleTask(Calendar.THURSDAY, hourOfDay, minute);
                                    } else if (checkedId == R.id.chipFriday) {
                                        scheduleTask(Calendar.FRIDAY, hourOfDay, minute);
                                    }
                                }
                                dialog.dismiss();
                            }
                        }, 15, 00, false);
                        timePickerDialog.show();
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