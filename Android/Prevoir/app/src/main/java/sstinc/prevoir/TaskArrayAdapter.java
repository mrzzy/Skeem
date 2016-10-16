package sstinc.prevoir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class TaskArrayAdapter extends ArrayAdapter<Task> {

    static class ViewHolder {
        TextView title;
        TextView description;
    }

    TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_task_row, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_task_title);
            viewHolder.description = (TextView) convertView.findViewById(
                    R.id.list_item_task_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(task.name);
        viewHolder.description.setText(task.description);

        return convertView;
    }
}
