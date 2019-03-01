package com.steve.retrofit1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EntryListAdapter extends ArrayAdapter<GitHubEntry> {
    private LayoutInflater inflater;
    private Context context;
    private List<GitHubEntry> entries;

    public EntryListAdapter(@NonNull Context context, @NonNull List<GitHubEntry> entries) {
        super(context, R.layout.list_item, entries);

        this.context = context;
        this.entries = entries;

        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = Activity.getLayoutInflater();
        inflater = LayoutInflater.from(context);
    }

    private int lastPosition = -1;
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        TextView tvId = (TextView)convertView.findViewById(R.id.item_id);
        TextView tview = (TextView)convertView.findViewById(R.id.item_login);
        tview.setText((CharSequence) entries.get(position).getLogin());
        tvId.setText((CharSequence)String.valueOf(entries.get(position).getId()));
        return convertView;
    }
}
