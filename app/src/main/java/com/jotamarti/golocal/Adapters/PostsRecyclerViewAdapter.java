package com.jotamarti.golocal.Adapters;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jotamarti.golocal.Activities.PostDetailActivity;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private final List<Post> postsList;
    private List<Shop> shopList;
    private final String TAG = "PostsRecyclerVAdapter";
    private Context context;
    private MainActivityViewModel model;

    public PostsRecyclerViewAdapter(List<Post> items, Context context) {
        postsList = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.mItem = postValues.get(position);
        holder.cardHeader.setText(postsList.get(position).getHeader());
        holder.cardMessage.setText(postsList.get(position).getMessage());
        Picasso.get().load(postsList.get(position).getImage().toString()).into(holder.postImage);
        holder.postImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Hello from POST REC VIEW ADAPTER");
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post", postsList.get(position));
                intent.putExtra("caller", "MainActivity");

                String shopId = postsList.get(position).getCompanyId();
                Shop theShop = null;
                Log.d(TAG, String.valueOf(shopList.size()));
                for (int i = 0; i < shopList.size(); i++){
                    Log.d(TAG, "El shop actual ID " + shopList.get(i).getUserUid());
                    Log.d(TAG, "El shopId" + shopId);
                    if(shopList.get(i).getUserUid().equals(shopId)){
                        theShop = shopList.get(i);
                        break;
                    }
                }
                intent.putExtra("shop", theShop);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public void setShopList(List<Shop> shopList){
        this.shopList = shopList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView cardHeader;
        public final TextView cardMessage;
        private ImageView postImage;
        //public Post mItem;
        public LinearLayout parent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cardHeader = (TextView) view.findViewById(R.id.txtViewHeaderCardView);
            cardMessage = (TextView) view.findViewById(R.id.txtViewMessageCardView);
            postImage = (ImageView) view.findViewById(R.id.PostDetailsActivity_imageView_postImage);
            parent = view.findViewById(R.id.parent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cardMessage.getText() + "'";
        }
    }
}