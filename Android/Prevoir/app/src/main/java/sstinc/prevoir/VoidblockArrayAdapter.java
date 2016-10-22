package sstinc.prevoir;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

class VoidblockArrayAdapter extends ArrayAdapter<Voidblock> {
    // List to contain all the voidblocks
    private final List<Voidblock> list;

    VoidblockArrayAdapter(Activity context, List<Voidblock> list) {
        super(context, R.layout.list_voidblock_row, list);
        this.list = list;
    }

    private static class ViewHolder {
        TextView name;
        TextView fromTime;
        TextView toTime;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Voidblock voidblock = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            // Set new convert view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_voidblock_row,
                    parent, false);

            // Make new view holder
            viewHolder = new ViewHolder();

            // Set views for view holder
            viewHolder.name = (TextView) convertView.findViewById(R.id.list_item_voidblock_name);
            viewHolder.fromTime = (TextView) convertView.findViewById(
                    R.id.list_item_voidblock_start);
            viewHolder.toTime = (TextView) convertView.findViewById(R.id.list_item_voidblock_stop);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(
                    R.id.list_item_voidblock_checkbox);

            // Set on checked change listener for checkbox
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
            viewHolder = (VoidblockArrayAdapter.ViewHolder) convertView.getTag();
        }

        // Store the position of the checkbox
        viewHolder.checkBox.setTag(position);

        // Set the name
        viewHolder.name.setText(voidblock.name);
        // Set the from time
        viewHolder.fromTime.setText(voidblock.from.toFormattedString());
        // Set the to time
        viewHolder.toTime.setText(voidblock.to.toFormattedString());
        // Set visibility of checkbox
//        if (TaskFragment.menu_multi) {
//            viewHolder.checkBox.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.checkBox.setVisibility(View.GONE);
//        }
        // Set if the checkbox is checked or not
        viewHolder.checkBox.setChecked(list.get(position).checked);

        return convertView;
    }
}