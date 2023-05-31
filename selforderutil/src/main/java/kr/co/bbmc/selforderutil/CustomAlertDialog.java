package kr.co.bbmc.selforderutil;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertDialog extends DialogFragment {
    private static customAlertBtnClickListener mCallBack = null;
    private Button mSaveBtn = null;
    private Button mCancelBtn = null;
    private TextView mTitleText = null;
    private TextView mBodyText = null;
    private String title = null;
    private String body = null;


    public interface customAlertBtnClickListener {
        public void onSaveClick();
        public void onCancelClick();
    }
    public void setAlertDialogParam(String t, String b, customAlertBtnClickListener callback)
    {
        title = t;
        body = b;
        mCallBack = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_alert_dialog, container, false);
        mTitleText = (TextView)v.findViewById(R.id.alert_dialog_title_id);
        mBodyText = (TextView)v.findViewById(R.id.alert_dialog_text_id);
        mCancelBtn = (Button)v.findViewById(R.id.option_cancel_id);
        mSaveBtn = (Button)v.findViewById(R.id.option_save_id);
        mSaveBtn.setVisibility(View.INVISIBLE);
        mTitleText.setText(title);
        mBodyText.setText(body);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallBack!=null)
                    mCallBack.onCancelClick();
            }
        });
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallBack!=null)
                    mCallBack.onSaveClick();
            }
        });
        return v;
    }
}
