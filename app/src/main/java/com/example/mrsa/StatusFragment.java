package com.example.mrsa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    ArrayList<StatusModel>  statusModels = new ArrayList<>();
    int[] statusModelImages = {R.drawable.rs_close, R.drawable.rs_open, R.drawable.rs_close, R.drawable.rs_open};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.mRecyclerView);

        setUpStatusModels();

        Status_RecyclerViewAdapter adapter = new Status_RecyclerViewAdapter(getContext(), statusModels);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    private void setUpStatusModels() {
        String[] deviceNames = getResources().getStringArray(R.array.device_names);
        String[] operationsPerformed = getResources().getStringArray(R.array.operation_performed);
        String[] operationTimes = getResources().getStringArray(R.array.operation_time);

        for (int i = 0; i < deviceNames.length; i++) {
            statusModels.add(new StatusModel(deviceNames[i],
                    operationsPerformed[i],
                    operationTimes[i],
                    statusModelImages[i]));
        }
    }
}