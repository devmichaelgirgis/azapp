package com.exceedgulf.alainzoo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.BaseActivity;


/**
 * Created by Paras Ghasadiya on 19/12/17.
 */
public class DisplayDialog {
    private static DisplayDialog ourInstance;
    /**
     * A Progress Dialog object
     */
    private static AlertDialog mDialog;

    private DisplayDialog() {
    }

    public static DisplayDialog getInstance() {
        if (ourInstance == null) {
            ourInstance = new DisplayDialog();
        }
        return ourInstance;
    }
    //    private static ProgressDialog mDialog;

    /**
     * Displays the progress dialog on activity.
     * This method will generate progress dialog and displays it on screen if its not currently showing,
     * If the progressbar dialog already been showing than it will not generate new dialog and return old generated dialog.
     *
     * @param mContext     requires to create ProgressDialog in Application
     * @param message      displays the message on Progress Dialog
     * @param isCancelable Set cancelable property of progress dialog
     * @return Returns the object of Progress dialog that currently generated or previously generated.
     */
    public AlertDialog showProgressDialog(final Context mContext, final String message, boolean isCancelable) {
        if (mDialog == null) {
            // mDialog = new ProgressDialog(mContext, R.style.progress_bar_style);
            mDialog = dialogLoading(mContext);
        }
        if (!mDialog.isShowing()) {
            // mDialog = new ProgressDialog(mContext, R.style.progress_bar_style);
            mDialog = dialogLoading(mContext);
            mDialog.show();
        }

        mDialog.setCancelable(isCancelable);
        mDialog.setCanceledOnTouchOutside(false);
        //mDialog.setMessage(message);
        return mDialog;
    }

    private AlertDialog dialogLoading(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false);
        final LayoutInflater inflater = ((BaseActivity) context).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        final AppCompatImageView imgloading = dialogView.findViewById(R.id.imgloading);
        Glide.with(context).asGif().load(R.drawable.zoo_loading).into(imgloading);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    /**
     * Dismiss Progress dialog if it is showing
     */
    public void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


}
