package sstinc.prevoir;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * This class handles the schedule's array view using {@link ArrayAdapter}.
 * The array adapter values split into two categories: tasks and voidblocks.
 * Both of them are displayed differently.
 *
 * @see Schedulable
 * @see ArrayAdapter
 */
class ScheduleArrayAdapter extends ArrayAdapter<Schedulable> {

    // Basic constructor, call super
    ScheduleArrayAdapter(Activity context, List<Schedulable> list) {
        super(context, R.layout.list_schedule_row, list);
    }

    /**
     * This class contains all the previously retrieved views.
     */
    private static class ViewHolder {
        TextView task_name;
        TextView task_description;
        TextView task_subject;
        RelativeLayout task_layout;
        TextView task_start_time;

        RelativeLayout voidblock_layout;
        TextView voidblock_time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // How each item in the array should be displayed in the view
        ViewHolder viewHolder;
        // Get the schdulable item
        Schedulable schedulable = getItem(position);

        if (convertView == null) {
            // Create new convert view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_schedule_row,
                    parent, false);

            // Make new view holder
            viewHolder = new ViewHolder();

            // Set views for view holder
            viewHolder.task_layout = (RelativeLayout) convertView.findViewById(
                    R.id.list_item_schedule_layout_task);
            viewHolder.task_name = (TextView) convertView.findViewById(
                    R.id.list_item_schedule_name);
            viewHolder.task_description = (TextView) convertView.findViewById(
                    R.id.list_item_schedule_description);
            viewHolder.task_subject = (TextView) convertView.findViewById(
                    R.id.list_item_schedule_subject);
            viewHolder.task_start_time = (TextView) convertView.findViewById(
                    R.id.list_item_schedule_start_time);

            viewHolder.voidblock_layout = (RelativeLayout) convertView.findViewById(
                    R.id.list_item_schedule_layout_voidblock);
            viewHolder.voidblock_time = (TextView) convertView.findViewById(
                    R.id.list_item_schedule_voidblock_time);

            // Set viewHolder to convertView
            convertView.setTag(viewHolder);
        } else {
            // Get viewHolder from convertView
            viewHolder = (ScheduleArrayAdapter.ViewHolder) convertView.getTag();
        }

        if (schedulable instanceof Task) {
            // If it is a task
            Calendar cal = Calendar.getInstance();
            Task task = (Task) schedulable;

            viewHolder.task_name.setText(task.getName());
            viewHolder.task_description.setText(task.getDescription());
            viewHolder.task_subject.setText(task.getSubject());

            // Show date only if it is not today
            String task_start_time = task.getScheduledStart().toFormattedString();
            int current_day = cal.get(Calendar.DAY_OF_MONTH);
            if (task.getScheduledStart().getDay() == current_day) {
                task_start_time = task_start_time.substring(0, 5);
            }
            viewHolder.task_start_time.setText(task_start_time);

            viewHolder.task_layout.setVisibility(View.VISIBLE);
        } else {
            // If it is a voidblock
            Voidblock voidblock = (Voidblock) schedulable;

            viewHolder.voidblock_time.setText(voidblock.getScheduledStart().toFormattedString() +
                    " - " + voidblock.getScheduledStop().toFormattedString());

            viewHolder.voidblock_layout.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
