package com.runops.codepathtodo;

// Adapted from: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

public class DocumentAdapter extends ArrayAdapter<Document> {

    Context context;
    int resource;
    List<Document> items;

    public DocumentAdapter(Context context, int resource, List<Document> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DocumentHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_item_document, parent, false);

            holder = new DocumentHolder();
            holder.title = (TextView) row.findViewById(R.id.itemTitle);
            holder.checkbox = (CheckBox) row.findViewById(R.id.itemCheckbox);
            holder.dueAt = (TextView) row.findViewById(R.id.itemDueAt);

            row.setTag(holder);
        } else {
            holder = (DocumentHolder) row.getTag();
        }

        Document document = items.get(position);
        holder.title.setText((String) document.getProperty("itemText"));
        holder.checkbox.setChecked((boolean) document.getProperty("itemChecked"));

        if (document.getProperty("itemDueAt") == null) {
            holder.dueAt.setText("");
        } else {
            holder.dueAt.setText("DUE " + "");
        }

        return row;
    }

    static class DocumentHolder {
        TextView title;
        CheckBox checkbox;
        TextView dueAt;
    }
}
