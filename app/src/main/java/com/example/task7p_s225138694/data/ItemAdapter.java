package com.example.task7p_s225138694.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task7p_s225138694.DetailsActivity;
import com.example.task7p_s225138694.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private ArrayList<ItemDataModel> items = new ArrayList<>();
    private ArrayList<ItemDataModel> itemsBackUp;

    public interface OnEventButtonClickListener {
        void onDeleteClicked(ItemDataModel itm, int position);
    }

    // Listener that will be set from Fragment
    private OnEventButtonClickListener listener;

    // Setter listener
    public void setOnEventButtonClickListener(OnEventButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        ItemDataModel itm = items.get(position);
        holder.type.setText(itm.getType());
        holder.name.setText(itm.getName());
        holder.date.setText(itm.getDate());

        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();

            android.content.Intent intent = new android.content.Intent(context, DetailsActivity.class);
            intent.putExtra("item_id", itm.getId()); // send the ID as parameter

            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClicked(itm, position);
                    Toast.makeText(v.getContext(), "An Item is Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView type, name, date;
        ImageButton btnDelete;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.textViewType);
            name = itemView.findViewById(R.id.textViewTitle);
            date = itemView.findViewById(R.id.textViewDate);
            btnDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public void setItems(ArrayList<ItemDataModel> items){
        this.items = items;
        this.itemsBackUp = new ArrayList<ItemDataModel>(items);
        notifyDataSetChanged();
    }

    public void searchFilter(String words){
        items.clear();

        if(words.isEmpty()){
            items.addAll(itemsBackUp);
        }
        else{
            String q = words.toLowerCase();

            for(ItemDataModel itm : itemsBackUp) {
                if(itm.getName().toLowerCase().contains(q)){
                    items.add(itm);
                }
            }
        }

        notifyDataSetChanged();
    }
}
