package com.example.uidesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.uidesign.profile.BoardItem;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.RecycleAdapter;

public class ProfileFragment extends Fragment {
    Button settings;
    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapter adapter;//声明适配器

    private  BoardItemList boardItemList = new BoardItemList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);  // 救命这狗bug我真是de了一年
        // Create an adapter and supply the data to be displayed.
        for(int i = 0; i < 5; i++)
            boardItemList.insert("man_6", "Home " + i, "2023-05-16");
        adapter = new RecycleAdapter(getActivity(), boardItemList);
        // Connect the adapter with the recycler view.
        recyclerView.setAdapter(adapter);
        // Give the recycler view a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferences = getActivity().getSharedPreferences("com.example.android.myapp", Context.MODE_PRIVATE);

        settings = view.findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}