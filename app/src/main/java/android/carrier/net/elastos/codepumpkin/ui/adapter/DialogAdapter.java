package android.carrier.net.elastos.codepumpkin.ui.adapter;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.R;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DialogAdapter extends BaseAdapter {

    private List<Action> actionList;
    private Context context;

    private int currentIndex = 0;


    class ViewHolder {
        TextView tvAction;
        TextView tvValue;
    }

    public DialogAdapter(Context context, List<Action> actionList) {
        this.context = context;
        setData(actionList);
    }

    public void setData(List<Action> actionList) {
        if (actionList == null) {
            actionList = new ArrayList<>();
        }
        this.actionList = actionList;
    }

    public void setCurrentIndex(int p){
        this.currentIndex = p;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return actionList.size();
    }

    @Override
    public Action getItem(int position) {
        return actionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.dialog_item, null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
            holder.tvAction = (TextView) convertView.findViewById(R.id.dialog_item_action);
            holder.tvValue = (TextView) convertView.findViewById(R.id.dialog_item_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Action action = getItem(position);
        if (action.getType() == 1) {
            holder.tvAction.setText("move");
            holder.tvValue.setText(action.getValue() + "");


        } else {
            holder.tvAction.setText("turn");
            holder.tvValue.setText(action.getValue() > 0 ? "right" : "left");

        }

        if(currentIndex == position){
            holder.tvAction.setTextColor(Color.RED);
            holder.tvValue.setTextColor(Color.RED);
        }else{
            holder.tvAction.setTextColor(Color.BLACK);
            holder.tvValue.setTextColor(Color.BLACK);
        }


        return convertView;
    }
}
