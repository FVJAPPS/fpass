package com.fvjapps.fpass.adapters;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fvjapps.fpass.R;
import com.fvjapps.fpass.dao.MediaBucketDao;
import com.fvjapps.fpass.entities.MediaBucket;
import com.fvjapps.fpass.entities.Section;
import com.fvjapps.fpass.relations.SectionWithEntries;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private List<SectionWithEntries> sections;
    private final MediaBucketDao mediaBucketDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private OnSectionClickListener clickListener;

    public interface OnSectionClickListener {
        void onSectionClick(Section section);
    }

    public SectionAdapter(MediaBucketDao mediaBucketDao) {
        this.mediaBucketDao = mediaBucketDao;
    }

    public void setOnSectionClickListener(OnSectionClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<SectionWithEntries> list) {
        sections = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SectionWithEntries sw = sections.get(position);
        Section section = sw.section;

        holder.colorStrip.setBackgroundColor(section.getColor());
        holder.name.setText(section.getName());

        int count = sw.entries != null ? sw.entries.size() : 0;
        holder.entryCount.setText(holder.itemView.getContext()
                .getString(R.string.entries_count, count));

        String iconHash = section.getIconHash();
        if (iconHash != null && !iconHash.isEmpty()) {
            executor.execute(() -> {
                MediaBucket bucket = mediaBucketDao.getByHashSync(iconHash);
                if (bucket != null && bucket.getBase64Data() != null) {
                    byte[] bytes = Base64.decode(bucket.getBase64Data(), Base64.NO_WRAP);
                    holder.itemView.post(() -> {
                        if (holder.getAdapterPosition() == position) {
                            Glide.with(holder.itemView.getContext())
                                    .load(bytes)
                                    .override(80, 80)
                                    .into(holder.icon);
                        }
                    });
                }
            });
        } else {
            holder.icon.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSectionClick(section);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
            popup.getMenu().add(0, 1, 0, R.string.edit);
            popup.getMenu().add(0, 2, 0, R.string.delete);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        Toast.makeText(holder.itemView.getContext(), "Edit: " + section.getName(), Toast.LENGTH_SHORT).show();
                        return true;
                    case 2:
                        Toast.makeText(holder.itemView.getContext(), "Delete: " + section.getName(), Toast.LENGTH_SHORT).show();
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
        return sections != null ? sections.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View colorStrip;
        ImageView icon;
        TextView name;
        TextView entryCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorStrip = itemView.findViewById(R.id.view_color_strip);
            icon = itemView.findViewById(R.id.image_icon);
            name = itemView.findViewById(R.id.text_section_name);
            entryCount = itemView.findViewById(R.id.text_entry_count);
        }
    }
}
