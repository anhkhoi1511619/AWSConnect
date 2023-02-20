/*
 * Creator: Khoi_Nguyen
 * Date: 2022/12/16
 */

package com.app.awsconnect.view;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;


/**
 * ONS表示用
 */
public class LoadingDialogFragment extends DialogFragment {
    public static FragmentManager fragmentManager = null;

    //------------------------------------------
    // Dialogの表示内容設定
    //------------------------------------------

    /**
     * コンテンツビュー設定 (設定するとCustomDialogとして動作する)
     *
     * @param contentView レイアウトID
     * @return thisオブジェクト
     */
    public LoadingDialogFragment setContentView(@LayoutRes int contentView) {
        this.contentView = contentView;
        return this;
    }

    int contentView = -1;


    /**
     * メッセージ設定
     *
     * @param messageId メッセージ文言リソース番号
     * @return thisオブジェクト
     */
    public LoadingDialogFragment setMessage(@StringRes int messageId) {
        this.messageId = messageId;
        return this;
    }

    int messageId = 0;



    /**
     * プログレス表示有無設定
     *
     * @param isNeedProgress プログレス表示有無
     * @return thisオブジェクト
     */
    public LoadingDialogFragment setNeedProgress(boolean isNeedProgress) {
        this.isNeedProgress = isNeedProgress;
        return this;
    }

    boolean isNeedProgress = false;



    //------------------------------------------
    // Dialog生成 (当クラス使用者側は意識しない)
    //------------------------------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        logd("dialog:onCreateDialog:" + getTag());

        if(savedInstanceState!=null) {
            //Nullチェック
            logd("dialog:savedata");
            savedInstanceState.getInt("ContentView",contentView);
            savedInstanceState.getInt("messageId",messageId);
        }

        return initCustomDialog();
    }

    private Dialog initCustomDialog() {

        Dialog dialog = new Dialog(getActivity());
        logd("dialog:initCustomDialog");
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        // カスタムレイアウト設定
        if (contentView == -1) return dialog;
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);

        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View v = dialog.getWindow().getDecorView();
        int uiOption = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        v.setSystemUiVisibility(uiOption);

        return dialog;
    }
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logd("dialog:onActivityCreated");

        Dialog dialog = getDialog();
        //Dialog内容確認
        if (messageId == 0) {
            //権限禁止→Dialogが再起動された場合
            logd("Dialog内容がないため、表示しない");
            if (dialog != null) {
                dialog.dismiss();
            }
        }

        if (contentView == -1) {
            //カスタムダイアログではない場合、何もしない
            return;
        }

    }

    //------------------------------------------
    // Util系 機能
    //------------------------------------------

    /**
     * TAGを指定してのダイアログ非表示処理
     *
     * @param
     * @param tag             非表示対象のダイアログタグ
     * @return TRUE:非表示成功、FALSE:非表示失敗
     */
    public static boolean dismiss(String tag) {
        try {
            if (fragmentManager != null) {
                DialogFragment dialog = (DialogFragment) fragmentManager.findFragmentByTag(tag);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
            return true;
        } catch (Exception e) {
            //すでに非表示済みなどなどのエラーをここで捕まえる
            return false;
        }
    }

    /**
     * 表示。同じTAGのダイアログが表示されていたら表示しない。
     *
     * @param manager 　マネージャー
     * @param tag     タグ
     */
    @Override
    public void show(FragmentManager manager, String tag) {

        DialogFragment dialog = (DialogFragment) manager.findFragmentByTag(tag);
        if (dialog == null) {
            fragmentManager = manager;
            super.show(manager, tag);
            logd("dialog:show:"+tag);
        }

    }



    /**
     * デバッグログ出力
     *
     * @param msg ログ出力するデバッグメッセージ
     */
    private void logd(String msg) {
        if (true) {
            Log.d(TAG, msg);
        }
    }
}
