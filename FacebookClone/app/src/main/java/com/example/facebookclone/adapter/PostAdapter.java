package com.example.facebookclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookclone.R;
import com.example.facebookclone.model.PostModel;
import com.example.facebookclone.rest.ApiClient;
import com.example.facebookclone.util.AgoDateParse;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<PostModel> postModels;


    public PostAdapter(Context context, List<PostModel> postModels) {
        this.context = context;
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PostModel postModel = postModels.get(position);
        if (postModel.getPost() != null && postModel.getPost().length() > 1) {
            holder.post.setText(postModel.getPost());
        } else {
            holder.post.setVisibility(View.GONE);
        }
        holder.peopleName.setText(postModel.getName());
        if (postModel.getPrivacy().equals("0")) {
            holder.privacyIcon.setImageResource(R.drawable.icon_friends);
        } else if (postModel.getPrivacy().equals("1")) {
            holder.privacyIcon.setImageResource(R.drawable.icon_onlyme);
        } else {
            holder.privacyIcon.setImageResource(R.drawable.icon_public);
        }

        if(!postModel.getStatusImage().isEmpty()) {
            Picasso.get().load(ApiClient.BASE_URL_1 + postModel.getStatusImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.statusImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(ApiClient.BASE_URL_1 + postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).into(holder.statusImage);
                }
            });
        } else {
            holder.statusImage.setImageDrawable(null);
        }

        try{
            holder.date.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(postModel.getStatusTime())));
        }catch(ParseException e){
            e.printStackTrace();
        }

        if (!postModel.getProfileUrl().isEmpty()) {
            Picasso.get().load(postModel.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.peopleImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(postModel.getProfileUrl()).placeholder(R.drawable.default_image_placeholder).into(holder.peopleImage);
                }
            });
        }









    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.people_image)
        ImageView peopleImage;
        @BindView(R.id.people_name)
        TextView peopleName;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.privacy_icon)
        ImageView privacyIcon;
        @BindView(R.id.memory_meta_rel)
        RelativeLayout memoryMetaRel;
        @BindView(R.id.post)
        TextView post;
        @BindView(R.id.status_image)
        ImageView statusImage;
        @BindView(R.id.like_img)
        ImageView likeImg;
        @BindView(R.id.like_txt)
        TextView likeTxt;
        @BindView(R.id.likeSection)
        LinearLayout likeSection;
        @BindView(R.id.comment_img)
        ImageView commentImg;
        @BindView(R.id.comment_txt)
        TextView commentTxt;
        @BindView(R.id.commentSection)
        LinearLayout commentSection;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
