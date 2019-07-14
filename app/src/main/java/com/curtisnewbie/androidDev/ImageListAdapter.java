package com.curtisnewbie.androidDev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    public static final String TAG = "RecyclerView";
    public static final String IMG_TITLE = "img_title";

    private List<String> imagesName;
    private Context context;
    private AppDatabase db;

    public ImageListAdapter(Context context) {
        this.context = context;
        this.db = DataStorage.getInstance(null).getDB();
        this.imagesName = db.dao().getListOfImgName();
    }


    // this method is for inflating the view of each item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acitivity_each_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // loading resources for each item / holder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // setup the name for each image item
        holder.getName().setText(imagesName.get(position - 1));

        // setup the onClickListener for the layout of whole Recycler layout
        holder.getItem_layout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // jump to another Activity to see the view
                Intent intent = new Intent(".ImageViewActivity");
                intent.putExtra(IMG_TITLE, imagesName.get(holder.getAdapterPosition() - 1));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesName.size();
    }

    // each view holder holds data of each item
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private RelativeLayout item_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            item_layout = itemView.findViewById(R.id.item_layout);
        }

        public TextView getName() {
            return this.name;
        }

        public RelativeLayout getItem_layout() {
            return this.item_layout;

        }
    }
}
