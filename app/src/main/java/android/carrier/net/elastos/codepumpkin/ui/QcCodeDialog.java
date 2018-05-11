package android.carrier.net.elastos.codepumpkin.ui;

import android.app.Dialog;
import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.R;
import android.carrier.net.elastos.codepumpkin.ui.adapter.DialogAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

/**
 * 用dialog显示二维码
 * */
public class QcCodeDialog extends Dialog {

    private Context context;
    private ImageView ivCode;




    public QcCodeDialog(@NonNull Context context) {
        super(context);
//        WindowManager.LayoutParams lp= this.getWindow().getAttributes();
//        lp.alpha=0.5f;
//        this.getWindow().setAttributes(lp);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.context = context;
        initView();
    }

    public void initView() {
        View view = View.inflate(context,R.layout.dialog_img,null);
        setContentView(view);
//        view.findViewById(R.id.dialog_qcode).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                QcCodeDialog.this.cancel();
//            }
//        });
        this.ivCode = (ImageView)view.findViewById(R.id.dialog_qcode);
       // this.setCanceledOnTouchOutside(false);
    }


    public void showDialog(Bitmap bm){
        ivCode.setImageBitmap(bm);
        this.show();
    }


}
