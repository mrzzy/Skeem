package sstinc.prevoir;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class TaskArrayAdapter extends ArrayAdapter<Task> {

//    static class ViewHolder {
//        TextView title;
//        TextView description;
//    }

//    TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
//        super(context, 0, tasks);
//    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Task task = getItem(position);
//
//        ViewHolder viewHolder;
//
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//
//            convertView = LayoutInflater.from(getContext()).inflate(
//                    R.layout.list_task_row, parent, false);
//
//            viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_task_title);
//            viewHolder.description = (TextView) convertView.findViewById(
//                    R.id.list_item_task_description);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        viewHolder.title.setText(task.name);
//        viewHolder.description.setText(task.description);
//
//        return convertView;
//    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Task task = getItem(position);
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(
//                    R.layout.list_task_row, parent, false);
//        }
//
//        TextView title = (TextView) convertView.findViewById(R.id.list_item_task_title);
//        TextView description = (TextView) convertView.findViewById(R.id.list_item_task_description);
//
//        title.setText(task.name);
//        description.setText(task.description);
//
//        return convertView;
//    }

    // List to contain all the tasks
    private final List<Task> list;

    TaskArrayAdapter(Activity context, List<Task> list) {
        super(context, R.layout.list_task_row, list);
        //this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            // Set new convert view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_task_row, parent,
                    false);

            // Make new view holder
            viewHolder = new ViewHolder();

            // Set views for view holder
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_task_title);
            viewHolder.description = (TextView) convertView.findViewById(
                    R.id.list_item_task_description);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.list_item_checkBox);

            // Set on checked change listener for checkbox
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Get position of checkbox
                    int getPosition = (Integer) buttonView.getTag();
                    list.get(getPosition).checked = buttonView.isChecked();
                }
            });

            // Set viewHolder to convertView
            convertView.setTag(viewHolder);
        } else {
            // Get viewHolder from convertView
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Store the position of the checkbox
        viewHolder.checkbox.setTag(position);

        // Set the title
        viewHolder.title.setText(task.name);
        // Set the description
        viewHolder.description.setText(task.description);
        // Set visibility of checkbox
        if (TaskFragment.menu_multi) {
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkbox.setVisibility(View.GONE);
        }
        // Set if the checkbox is checked or not
        viewHolder.checkbox.setChecked(list.get(position).checked);

        return convertView;
    }
}
