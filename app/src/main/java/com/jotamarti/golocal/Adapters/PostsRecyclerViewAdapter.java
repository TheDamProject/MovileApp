package com.jotamarti.golocal.Adapters;

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
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private final List<Post> postsList;
    private List<Shop> shopList;
    private Shop shop;
    private final String TAG = "PostsRecyclerVAdapter";
    private Context context;
    private MainActivityViewModel model;
    private String caller;

    public PostsRecyclerViewAdapter(List<Post> items, Context context, String caller) {
        postsList = items;
        this.context = context;
        this.caller = caller;
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
        Picasso.get().load(postsList.get(position).getImageUrl().toString()).into(holder.postImage);
        holder.postImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post", postsList.get(position));
                if (caller.equals("MainActivity")){
                    intent.putExtra("caller", "MainActivity");

                    String shopId = postsList.get(position).getCompanyUid();
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
                } else if(caller.equals("PostDetailActivityFromMainActivity")){
                    intent.putExtra("caller", "MainActivity");
                    intent.putExtra("post", postsList.get(position));
                    intent.putExtra("shop", shop);
                    context.startActivity(intent);
                } else if (caller.equals("MapsFragment")) {
                    intent.putExtra("caller", caller);
                    intent.putExtra("post", postsList.get(position));
                    intent.putExtra("shop", shop);
                    context.startActivity(intent);
                } else {
                    intent.putExtra("caller", "visit");
                    intent.putExtra("post", postsList.get(position));
                    context.startActivity(intent);
                }

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

    public void setShop(Shop shop){
        this.shop = shop;
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
            cardHeader = view.findViewById(R.id.txtViewHeaderCardView);
            cardMessage = view.findViewById(R.id.txtViewMessageCardView);
            postImage = view.findViewById(R.id.PostDetailsActivity_imageView_postImage);
            parent = view.findViewById(R.id.parent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cardMessage.getText() + "'";
        }
    }
}