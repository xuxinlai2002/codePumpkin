package android.carrier.net.elastos.codepumpkin.ui;

import android.app.Dialog;
import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.R;
import android.carrier.net.elastos.codepumpkin.ui.adapter.DialogAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

/**
 * 用dialog显示步数提示
 * */
public class StepDialog extends Dialog {

    private ListView lvAction;
    private DialogAdapter adapter;
    private Context context;




    public StepDialog(@NonNull Context context, DialogAdapter adapter) {
        super(context);
//        WindowManager.LayoutParams lp= this.getWindow().getAttributes();
//        lp.alpha=0.5f;
//        this.getWindow().setAttributes(lp);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.context = context;
        this.adapter = adapter;
        initView();
    }

    public void initView() {
        View view = View.inflate(context,R.layout.dialog_content,null);
        setContentView(view);
        lvAction = (ListView) view.findViewById(R.id.dialog_listview);
        view.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepDialog.this.cancel();
            }
        });
        lvAction.setAdapter(adapter);
        this.setCanceledOnTouchOutside(false);
    }


    public void showDialog(List<Action> list){
        this.adapter.setData(list);
        this.adapter.notifyDataSetChanged();
        this.show();
    }


}
