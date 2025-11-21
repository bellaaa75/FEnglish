package com.example.fenglishandroid.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fenglishandroid.R;

public class AdminAccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 空白布局，后续可替换为实际内容
        return inflater.inflate(R.layout.frag_admin_account, container, false);
    }
}