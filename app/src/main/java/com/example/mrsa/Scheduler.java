package com.example.mrsa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
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

public class Scheduler extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-services-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
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
                Chip chipMonday = dialogView.findViewById(R.id.chipMonday);
                Chip chipTuesday = dialogView.findViewById(R.id.chipTuesday);
                Chip chipWednesday = dialogView.findViewById(R.id.chipWednesday);
                Chip chipThursday = dialogView.findViewById(R.id.chipThursday);
                Chip chipFriday = dialogView.findViewById(R.id.chipFriday);
                Button chooseTime = dialogView.findViewById(R.id.chooseTime);

                AlertDialog.Builder builder = new AlertDialog.Builder(Scheduler.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Scheduler.this, new TimePickerDialog.OnTimeSetListener() {
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