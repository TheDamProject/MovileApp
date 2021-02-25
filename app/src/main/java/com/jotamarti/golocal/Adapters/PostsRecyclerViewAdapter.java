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
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private final List<Post> mValues;
    private final String TAG = "POSTS_REC_VIEW_ADAPTER";
    private Context context;

    public PostsRecyclerViewAdapter(List<Post> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.cardHeader.setText(mValues.get(position).getHeader());
        holder.cardMessage.setText(mValues.get(position).getMessage());
        holder.postImage.setImageBitmap(mValues.get(position).getImage());
        holder.postImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Hello from POST REC VIEW ADAPTER");
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("content", mValues.get(position).getMessage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView cardHeader;
        public final TextView cardMessage;
        private ImageView postImage;
        public Post mItem;
        public LinearLayout parent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cardHeader = (TextView) view.findViewById(R.id.txtViewHeaderCardView);
            cardMessage = (TextView) view.findViewById(R.id.txtViewMessageCardView);
            postImage = (ImageView) view.findViewById(R.id.imageViewCardView);
            parent = view.findViewById(R.id.parent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cardMessage.getText() + "'";
        }
    }
}