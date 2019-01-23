package com.mrbreak.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mrbreak.todo.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private ImageView shareImageView;
    private ImageView copyImageView;
    private ImageView lockImageView;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment newInstance(String param1, String param2) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareImageView = view.findViewById(R.id.share);
        copyImageView = view.findViewById(R.id.copy);
        lockImageView = view.findViewById(R.id.lock);

        LinearLayout shareLinearLayout = view.findViewById(R.id.shareLinearLayout);
        LinearLayout copyLinearLayout = view.findViewById(R.id.copyLinearLayout);
        LinearLayout lockLinearLayout = view.findViewById(R.id.lockLinearLayout);

        shareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        copyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        lockLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setImageColor();
    }

    private void setImageColor() {
        copyImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        lockImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        shareImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
    }
}
