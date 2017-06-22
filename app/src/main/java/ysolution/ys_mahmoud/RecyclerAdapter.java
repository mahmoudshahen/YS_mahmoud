package ysolution.ys_mahmoud;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemHolder> {
    List<Item> itemList;
    Context context;
    public RecyclerAdapter(List<Item> list, Context context) {
        this.itemList = list;
        this.context = context;
    }
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycle_item, parent,false);
        return new RecyclerAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.title.setText(itemList.get(position).getTitle());
        holder.publishedDate.setText(itemList.get(position).getPublishedDate());
       Log.v("pic",itemList.get(position).getPublishedDate());
        if(itemList.get(position).getUrl() != null)
            Picasso.with(context).load(itemList.get(position).getUrl())
                .placeholder(R.drawable.placeholder).error(R.drawable.notification_error).into(holder.imageView);
        if(position%2 ==0)
            Anime.animate(holder, true);
        else
            Anime.animate(holder, false);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView title, publishedDate;
        ImageView imageView;
        public ItemHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            publishedDate = itemView.findViewById(R.id.tv_date);
            imageView = itemView.findViewById(R.id.iv_photo);
        }
    }
}
