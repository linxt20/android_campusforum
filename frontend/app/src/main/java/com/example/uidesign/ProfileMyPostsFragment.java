package com.example.uidesign;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.RecycleAdapter;

public class ProfileMyPostsFragment extends Fragment {

    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapter adapter;//声明适配器

    private BoardItemList boardItemList = new BoardItemList();

    public ProfileMyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_my_posts, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        for(int i = 0; i < 5; i++)
            boardItemList.insert("man_6", "Home " + i, "2023-05-16");
        adapter = new RecycleAdapter(getActivity(), boardItemList);
        // Connect the adapter with the recycler view.
        recyclerView.setAdapter(adapter);
        // Give the recycler view a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}