package com.example.mrsa;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

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
    int selectedIcon = -1; // Initialize with a default value indicating no icon is selected
    private FirebaseAuth mAuth;


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
        mAuth = FirebaseAuth.getInstance();
        firstGridBtn = rootView.findViewById(R.id.firstGridBtn);
        secondGridBtn = rootView.findViewById(R.id.secondGridBtn);
        thirdGridBtn = rootView.findViewById(R.id.thirdGridBtn);
        fourthGridBtn = rootView.findViewById(R.id.fourthGridBtn);

        addDeviceBtn();
        return rootView;
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
                            selectedIcon = -1; // Reset the selected icon value
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
                                    fourthGridBtn.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
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
}