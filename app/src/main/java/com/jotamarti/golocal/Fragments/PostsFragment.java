package com.jotamarti.golocal.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jotamarti.golocal.Adapters.PostsRecyclerViewAdapter;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.dummy.DummyContent;
import com.jotamarti.golocal.dummy.PostsDummy;

import java.util.List;


public class PostsFragment extends Fragment {

    private MainActivityViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_item_list, container, false);
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        List<Post> posts = model.getPosts().getValue();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PostsRecyclerViewAdapter(posts, context));
        }
        return view;
    }
}