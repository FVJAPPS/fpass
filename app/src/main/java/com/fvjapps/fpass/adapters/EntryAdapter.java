package com.fvjapps.fpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.entities.Entry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    private List<Entry> entries;
    private final Set<Integer> unmaskedIds = new HashSet<>();
    private OnEntryEditListener editListener;
    private OnEntryDeleteListener deleteListener;

    public interface OnEntryEditListener {
        void onEntryEdit(Entry entry);
    }

    public interface OnEntryDeleteListener {
        void onEntryDelete(Entry entry);
    }

    public void setOnEntryEditListener(OnEntryEditListener listener) {
        this.editListener = listener;
    }

    public void setOnEntryDeleteListener(OnEntryDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void submitList(List<Entry> list) {
        entries = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.username.setText(entry.getUsername());
        holder.email.setText(entry.getEmail());

        boolean isUnmasked = unmaskedIds.contains(entry.getId());
        if (isUnmasked) {
            holder.password.setText(entry.getPassword());
            holder.toggleBtn.setImageResource(R.drawable.ic_visibility_off);
        } else {
            holder.password.setText(mask(entry.getPassword()));
            holder.toggleBtn.setImageResource(R.drawable.ic_visibility);
        }

        holder.toggleBtn.setOnClickListener(v -> {
            if (unmaskedIds.contains(entry.getId())) {
                unmaskedIds.remove(entry.getId());
            } else {
                unmaskedIds.add(entry.getId());
            }
            notifyItemChanged(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
            popup.getMenu().add(0, 1, 0, R.string.edit);
            popup.getMenu().add(0, 2, 0, R.string.delete);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        if (editListener != null) editListener.onEntryEdit(entry);
                        return true;
                    case 2:
                        if (deleteListener != null) deleteListener.onEntryDelete(entry);
                        return true;
                }
                return false;
            });
            popup.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    private String mask(String password) {
        if (password == null) return "";
        char[] dots = new char[password.length()];
        java.util.Arrays.fill(dots, '\u2022');
        return new String(dots);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView email;
        TextView password;
        ImageButton toggleBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.text_username);
            email = itemView.findViewById(R.id.text_email);
            password = itemView.findViewById(R.id.text_password);
            toggleBtn = itemView.findViewById(R.id.btn_toggle_mask);
        }
    }
}
