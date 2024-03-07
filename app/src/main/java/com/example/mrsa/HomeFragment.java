package com.example.mrsa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    int selectedIcon = -1;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-default-rtdb.firebaseio.com/");
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
        sharedPreferences = getActivity().getSharedPreferences("ButtonVisibility", Context.MODE_PRIVATE);


        restoreButtonVisibility();
        addDeviceBtn();
        toCommandScreen();

        return rootView;
    }

    private void restoreButtonVisibility() {
        firstGridBtn.setVisibility(sharedPreferences.getBoolean("firstGridBtn", false) ? View.VISIBLE : View.GONE);
        firstGridBtn.setText(sharedPreferences.getString("firstGridBtnRoomName", ""));
        selectedIcon = sharedPreferences.getInt("firstGridBtnIcon", -1);
        setCompoundDrawable(firstGridBtn, selectedIcon);

        secondGridBtn.setVisibility(sharedPreferences.getBoolean("secondGridBtn", false) ? View.VISIBLE : View.GONE);
        secondGridBtn.setText(sharedPreferences.getString("secondGridBtnRoomName", ""));
        selectedIcon = sharedPreferences.getInt("secondGridBtnIcon", -1);
        setCompoundDrawable(secondGridBtn, selectedIcon);

        thirdGridBtn.setVisibility(sharedPreferences.getBoolean("thirdGridBtn", false) ? View.VISIBLE : View.GONE);
        thirdGridBtn.setText(sharedPreferences.getString("thirdGridBtnRoomName", ""));
        selectedIcon = sharedPreferences.getInt("thirdGridBtnIcon", -1);
        setCompoundDrawable(thirdGridBtn, selectedIcon);

        fourthGridBtn.setVisibility(sharedPreferences.getBoolean("fourthGridBtn", false) ? View.VISIBLE : View.GONE);
        fourthGridBtn.setText(sharedPreferences.getString("fourthGridBtnRoomName", ""));
        selectedIcon = sharedPreferences.getInt("fourthGridBtnIcon", -1);
        setCompoundDrawable(fourthGridBtn, selectedIcon);
    }

    private void setCompoundDrawable(Button button, int selectedIcon) {
        if (selectedIcon == 1) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_living_room, 0, 0, 0);
        } else if (selectedIcon == 2) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bedroom, 0, 0, 0);
        } else if (selectedIcon == 3) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_kitchen, 0, 0, 0);
        } else if (selectedIcon == 4) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bathroom, 0, 0, 0);
        } else {
            // Set default compound drawable if no icon is selected
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void saveButtonVisibility() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("firstGridBtn", firstGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("firstGridBtnRoomName", firstGridBtn.getText().toString());
        editor.putInt("firstGridBtnIcon", selectedIcon);

        editor.putBoolean("secondGridBtn", secondGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("secondGridBtnRoomName", secondGridBtn.getText().toString());
        editor.putInt("secondGridBtnIcon", selectedIcon);

        editor.putBoolean("thirdGridBtn", thirdGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("thirdGridBtnRoomName", thirdGridBtn.getText().toString());
        editor.putInt("thirdGridBtnIcon", selectedIcon);

        editor.putBoolean("fourthGridBtn", fourthGridBtn.getVisibility() == View.VISIBLE);
        editor.putString("fourthGridBtnRoomName", fourthGridBtn.getText().toString());
        editor.putInt("fourthGridBtnIcon", selectedIcon);

        editor.apply();
    }

        public void sendCommandDialog() {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_command, null);
            Button openBtn = dialogView.findViewById(R.id.openBtn);
            Button closeBtn = dialogView.findViewById(R.id.closeBtn);
            SeekBar seekBar = dialogView.findViewById(R.id.seekBar);
            Chip decreaseBtn = dialogView.findViewById(R.id.decreaseBtn);
            Chip increaseBtn = dialogView.findViewById(R.id.increaseBtn);
            EditText seekBarValue = dialogView.findViewById(R.id.seekBarValue);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("Users").child("Roller Shade Control").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("App Request").setValue(1);
                            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Fully").setValue(1);
                            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Opened");
                            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("State").setValue(1);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

                }
            });

            decreaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() - 1);
                seekBarValue.setText(seekBar.getProgress() + "0%");
                if (seekBar.getProgress() == 0) {
                    seekBarValue.setText("0%");
                }
                readSeekBarValue(seekBarValue);
                }
            });

            increaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBar.setProgress(seekBar.getProgress() + 1);
                    seekBarValue.setText(seekBar.getProgress() + "0%");
                    readSeekBarValue(seekBarValue);
                }
            });
        }

    public void readSeekBarValue(EditText seekBarValue) {
        String text = seekBarValue.getText().toString();
        double percentage = Double.parseDouble(text.replace("%", "")) / 100.0;
        double state = percentage;

        if (percentage == 0) {
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("App Request").setValue(1);
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Fully").setValue(0);
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Closed");
        } else {
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("App Request").setValue(1);
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Fully").setValue(1);
            databaseReference.child("Users").child(uid).child("Roller Shade Control").child("Open-Close Roller Shade").setValue("Opened");
        }
        databaseReference.child("Users").child(uid).child("Roller Shade Control").child("State").setValue(state);
    }

        public void addDeviceBtn() {
        addDeviceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_device, null);
                EditText deviceIDField = dialogView.findViewById(R.id.deviceIDField);
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
                        // Reset all icons to default color
                        resetIconColors();

                        // Toggle the selected state of the clicked icon
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
                            //selectedIcon = -1; // Reset the selected icon value
                            v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.THEMECOLOR)));
                        }
                    }

                    private void resetIconColors() {
                        // Reset all icons to default color
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

                // Set click listeners for all icons
                livingRoomIcon.setOnClickListener(iconClickListener);
                bedroomIcon.setOnClickListener(iconClickListener);
                kitchenIcon.setOnClickListener(iconClickListener);
                bathroomIcon.setOnClickListener(iconClickListener);

                addBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String deviceID = deviceIDField.getText().toString();
                                String roomName = roomNameField.getText().toString();

                                if (firstGridBtn.getVisibility() == View.GONE) {
                                    firstGridBtn.setText(roomName);
                                    if (selectedIcon == 1) {
                                        firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_living_room, 0, 0, 0);
                                    } else if (selectedIcon == 2) {
                                        firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bedroom, 0, 0, 0);
                                    } else if (selectedIcon == 3) {
                                        firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_kitchen, 0, 0, 0);
                                    } else if (selectedIcon == 4) {
                                        firstGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bathroom, 0, 0, 0);
                                    }
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
                                    firstGridBtn.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }
                                else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.GONE) {
                                    secondGridBtn.setText(roomName);
                                    if (selectedIcon == 1) {
                                        secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_living_room, 0, 0, 0);
                                    } else if (selectedIcon == 2) {
                                        secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bedroom, 0, 0, 0);
                                    } else if (selectedIcon == 3) {
                                        secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_kitchen, 0, 0, 0);
                                    } else if (selectedIcon == 4) {
                                        secondGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bathroom, 0, 0, 0);
                                    }
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
                                    secondGridBtn.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }
                                else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.GONE) {
                                    thirdGridBtn.setText(roomName);
                                    if (selectedIcon == 1) {
                                        thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_living_room, 0, 0, 0);
                                    } else if (selectedIcon == 2) {
                                        thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bedroom, 0, 0, 0);
                                    } else if (selectedIcon == 3) {
                                        thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_kitchen, 0, 0, 0);
                                    } else if (selectedIcon == 4) {
                                        thirdGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bathroom, 0, 0, 0);
                                    }
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
                                    thirdGridBtn.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }
                                else if (firstGridBtn.getVisibility() == View.VISIBLE && secondGridBtn.getVisibility() == View.VISIBLE && thirdGridBtn.getVisibility() == View.VISIBLE && fourthGridBtn.getVisibility() == View.GONE) {
                                    fourthGridBtn.setText(roomName);
                                    if (selectedIcon == 1) {
                                        fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_living_room, 0, 0, 0);
                                    } else if (selectedIcon == 2) {
                                        fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bedroom, 0, 0, 0);
                                    } else if (selectedIcon == 3) {
                                        fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_kitchen, 0, 0, 0);
                                    } else if (selectedIcon == 4) {
                                        fourthGridBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bathroom, 0, 0, 0);
                                    }
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
                sendCommandDialog();
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
                sendCommandDialog();
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
                sendCommandDialog();
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