package com.example.mrsa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);

        TextView whatHomeScreen = rootView.findViewById(R.id.whatHomeScreen);
        TextView whatHomeScreenInfo = rootView.findViewById(R.id.whatHomeScreenInfo);

        TextView faqAddDevice = rootView.findViewById(R.id.faqAddDevice);
        TextView faqAddDeviceInfo = rootView.findViewById(R.id.faqAddDeviceInfo);

        TextView faqControlDevice = rootView.findViewById(R.id.faqControlDevice);
        TextView faqControlDeviceInfo = rootView.findViewById(R.id.faqControlDeviceInfo);

        TextView faqDeleteDevice = rootView.findViewById(R.id.faqDeleteDevice);
        TextView faqDeleteDeviceInfo = rootView.findViewById(R.id.faqDeleteDeviceInfo);

        TextView whatScheduleScreen = rootView.findViewById(R.id.whatScheduleScreen);
        TextView whatScheduleScreenInfo = rootView.findViewById(R.id.whatScheduleScreenInfo);

        TextView faqCreateSchedule = rootView.findViewById(R.id.faqCreateSchedule);
        TextView faqCreateScheduleInfo = rootView.findViewById(R.id.faqCreateScheduleInfo);

        TextView faqDeleteSchedule = rootView.findViewById(R.id.faqDeleteSchedule);
        TextView faqDeleteScheduleInfo = rootView.findViewById(R.id.faqDeleteScheduleInfo);

        TextView whatAccountScreen = rootView.findViewById(R.id.whatAccountScreen);
        TextView whatAccountScreenInfo = rootView.findViewById(R.id.whatAccountScreenInfo);

        TextView faqSignOut = rootView.findViewById(R.id.faqSignOut);
        TextView faqSignOutInfo = rootView.findViewById(R.id.faqSignOutInfo);

        TextView faqChangePassword = rootView.findViewById(R.id.faqChangePassword);
        TextView faqChangePasswordInfo = rootView.findViewById(R.id.faqChangePasswordInfo);

        TextView faqDeleteAccount = rootView.findViewById(R.id.faqDeleteAccount);
        TextView faqDeleteAccountInfo = rootView.findViewById(R.id.faqDeleteAccountInfo);

        dropDownMenu(whatHomeScreen, whatHomeScreenInfo);
        dropDownMenu(faqAddDevice, faqAddDeviceInfo);
        dropDownMenu(faqControlDevice, faqControlDeviceInfo);
        dropDownMenu(faqDeleteDevice, faqDeleteDeviceInfo);
        dropDownMenu(whatScheduleScreen, whatScheduleScreenInfo);
        dropDownMenu(faqCreateSchedule, faqCreateScheduleInfo);
        dropDownMenu(faqDeleteSchedule, faqDeleteScheduleInfo);
        dropDownMenu(whatAccountScreen, whatAccountScreenInfo);
        dropDownMenu(faqSignOut, faqSignOutInfo);
        dropDownMenu(faqChangePassword, faqChangePasswordInfo);
        dropDownMenu(faqDeleteAccount, faqDeleteAccountInfo);

        return rootView;
    }

    public void dropDownMenu(TextView summary, TextView details) {
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (details.getVisibility() == View.GONE) {
                    details.setVisibility(View.VISIBLE);
                    summary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_drop_down, 0, 0, 0);
                } else {
                    details.setVisibility(View.GONE);
                    summary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_drop_up, 0, 0, 0);
                }
            }
        });
    }
}