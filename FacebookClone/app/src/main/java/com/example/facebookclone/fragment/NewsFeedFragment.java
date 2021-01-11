package com.example.facebookclone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookclone.R;
import com.example.facebookclone.adapter.PostAdapter;
import com.example.facebookclone.model.PostModel;
import com.example.facebookclone.rest.ApiClient;
import com.example.facebookclone.rest.services.UserInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedFragment extends Fragment {
    Context context;
    @BindView(R.id.newsfeed1)
    RecyclerView newsfeed;
    @BindView(R.id.newsfeedProgressBar1)
    ProgressBar newsfeedProgressBar;
    int limit = 3;
    int offset = 0;
    boolean isFromStart = true;
    PostAdapter postAdapter;
    List<PostModel> postModels = new ArrayList<>();
    Unbinder unbinder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        newsfeed.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(context, postModels);
        newsfeed.setAdapter(postAdapter);
        newsfeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int passVisibleItems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (passVisibleItems + visibleItemCount >= (totalItemCount)) {
                    isFromStart = false;
                    newsfeedProgressBar.setVisibility(View.VISIBLE);
                    offset = offset + limit;
                    loadTimeLine();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        isFromStart = true;
        offset = 0;
        loadTimeLine();
    }

    private void loadTimeLine() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        params.put("limit", limit + "");
        params.put("offset", offset + "");

        Call<List<PostModel>> postModelCall = userInterface.getTimeline(params);
        postModelCall.enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                newsfeedProgressBar.setVisibility(View.GONE);
                if(response.body()!=null){
                    postModels.addAll(response.body());
                    if(isFromStart){
                        newsfeed.setAdapter(postAdapter);
                    }else{
                        postAdapter.notifyItemRangeInserted(postModels.size(),response.body().size());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                newsfeedProgressBar.setVisibility(View.GONE);
                Toast.makeText(context,"Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        postModels.clear();
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
