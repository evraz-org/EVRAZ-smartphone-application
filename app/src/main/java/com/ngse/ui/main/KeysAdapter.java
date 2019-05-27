package com.ngse.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.evrazcoin.evrazwallet.R;

import java.util.List;

public class KeysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Pair<String, String>> keys;
    private ClipboardManager clipboard;
    private Context context;

    public KeysAdapter(Context context, List<Pair<String, String>> keys) {
        this.context = context;
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        this.keys = keys;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType % 2) {
            case 0: {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_keys_title, viewGroup, false);
                return new SubtitleViewHolder(view);
            }
            default:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_keys, viewGroup, false);
                return new KeyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType() % 2) {
            case 0: {
                SubtitleViewHolder subtitleViewHolder = (SubtitleViewHolder) holder;
                subtitleViewHolder.subtitle.setText(position == 0 ? R.string.Owners_permission : R.string.Active_permission);
                break;
            }
            default: {
                Pair<String, String> keyPair = keys.get(position / 2);
                KeyViewHolder keyholder = (KeyViewHolder) holder;
                keyholder.textViewPub.setText(context.getString(R.string.Public_key) + ": " + keyPair.first.trim());
                keyholder.textViewPriv.setText(context.getString(R.string.Private_key) + ": " + keyPair.second.trim());

                keyholder.copyPub.setOnClickListener(v -> {
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.Public_key), keyPair.first);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, context.getString(R.string.pub_copied), Toast.LENGTH_SHORT).show();
                });

                keyholder.copyPriv.setOnClickListener(v -> {
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.Private_key), keyPair.second);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, context.getString(R.string.priv_copied), Toast.LENGTH_SHORT).show();
                });
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return keys.size() * 2;
    }

    static class KeyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPub;
        TextView textViewPriv;
        ImageView copyPub;
        ImageView copyPriv;

        public KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPub = itemView.findViewById(R.id.pubTextView);
            textViewPriv = itemView.findViewById(R.id.privTextView);
            copyPub = itemView.findViewById(R.id.pubCopy);
            copyPriv = itemView.findViewById(R.id.privCopy);
        }
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {
        TextView subtitle;

        public SubtitleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.subtitle = itemView.findViewById(R.id.subtitle);
        }
    }
}
