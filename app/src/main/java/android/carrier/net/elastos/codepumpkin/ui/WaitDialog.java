package android.carrier.net.elastos.codepumpkin.ui;

import android.app.Dialog;
import android.carrier.net.elastos.codepumpkin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


/**循环加载的对话框*/
public class WaitDialog {

	private ImageView image;
	private TextView tvContent;
	private Dialog dialog;
	private Context mContext;
	private Animation mAnimation;
	


	public WaitDialog(Context context) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.wait_dialog_style);
		dialog.setContentView(R.layout.loading);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0f;
		window.setAttributes(lp);
		image = (ImageView) dialog.findViewById(R.id.loading_progress);
		tvContent = (TextView) dialog.findViewById(R.id.loading_hint);
		mAnimation = AnimationUtils
				.loadAnimation(mContext, R.anim.loading_anim);
	}

	public void show(String text) {
		tvContent.setText(text);
		image.startAnimation(mAnimation);
		dialog.show();
	}

	public void dismiss() {
		if (dialog.isShowing()) {
			dialog.cancel();
		}
	}

	public void setCancelListener(OnCancelListener listener) {
		dialog.setOnCancelListener(listener);
	}

	public boolean isShowing() {
		return dialog.isShowing();
	}

	public void setOnDismissListener(
			DialogInterface.OnDismissListener dismissListener) {
		dialog.setOnDismissListener(dismissListener);
	}
}
