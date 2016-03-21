package com.hit.wi.t9.view;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.hit.wi.t9.R;

/**
 * Define Space Perference
 */
public class DefSpacePreference extends DialogPreference {

    public DefSpacePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {

        super.onBindDialogView(view);
        zhEditText = (EditText) view.findViewById(R.id.zh_space_text);
        enEditText = (EditText) view.findViewById(R.id.en_space_text);
        zhEditText.setText(getSharedPreferences().getString("ZH_SPACE_TEXT", "空格"));
        enEditText.setText(getSharedPreferences().getString("EN_SPACE_TEXT", "SPACE"));
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                getSharedPreferences().edit().putString("ZH_SPACE_TEXT", zhEditText.getText().toString()).commit();
                getSharedPreferences().edit().putString("EN_SPACE_TEXT", enEditText.getText().toString()).commit();
                break;
        }
        super.onClick(dialog, which);
    }

    private EditText zhEditText;
    private EditText enEditText;


}
