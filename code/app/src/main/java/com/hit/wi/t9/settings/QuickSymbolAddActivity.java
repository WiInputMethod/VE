package com.hit.wi.t9.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.hit.wi.util.CommonFuncs;
import com.hit.wi.util.StringUtil;
import com.hit.wi.t9.Interfaces.QuickSymbolInterface;
import com.hit.wi.t9.R;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.values.QuickSymbolsDataStruct;
import com.hit.wi.t9.viewGroups.QuickSymbolViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/5/16.
 */
public class QuickSymbolAddActivity extends Activity {
    LinearLayout contentLayout;
    Intent intent;
    List<Item> itemList;
    ScrollView scrollView;
    RelativeLayout innerRelativeLayout;
    RelativeLayout.LayoutParams innerLayoutParams;
    RelativeLayout.LayoutParams outerLayoutParams;
    Configure configure;
    ConfirmItem confirmItem;
    SymbolAddItem symbolAddItem;
    QuickSymbolsDataStruct symbolsDataStruct;
    QuickSymbolInterface quickSymbolInterface;
    DialogItem dialog;

    final int chineseflag = 0;
    final int englishflag = 1;
    final int numberflag = 2;

    int currentSymbolFlag;
    List<String> symbols;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quicksymbol_dim);
        intent = getIntent();
        iniInterface();
    }

    @SuppressLint("NewApi")
    private void iniInterface() {
        contentLayout = (LinearLayout) findViewById(R.id.quicksymbol_dim_content);
        innerRelativeLayout = new RelativeLayout(this);
        innerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView = new ScrollView(this);
        itemList = new ArrayList<Item>();
        configure = new Configure();
        currentSymbolFlag = convertCurrentKeyboardToCurentSymbolFlag(Global.currentKeyboard);

        confirmItem = new ConfirmItem();
        confirmItem.create();
        confirmItem.setSwitchButtonTextByFlag(currentSymbolFlag);

        dialog = new DialogItem();
        dialog.create();

        symbolAddItem = new SymbolAddItem();
        symbolAddItem.create(this);

        quickSymbolInterface = new QuickSymbolViewGroup();

        try {
            symbolsDataStruct = quickSymbolInterface.loadSymbolFromFile("default");
        } catch (IOException e) {
            e.printStackTrace();
        }

        symbols = getCurrentSymbolsByFlag(currentSymbolFlag);
        setItems(symbols);

        symbolAddItem.editText.requestFocus();

        contentLayout.addView(symbolAddItem.relativeLayout, symbolAddItem.layoutParams);
        contentLayout.addView(scrollView, outerLayoutParams);

    }

    private List<String> getCurrentSymbolsByFlag(int flag) {
        String[] theSymbols = symbolsDataStruct.chineseSymbols;//default
        switch (flag) {
            case englishflag:
                theSymbols = symbolsDataStruct.englishSymbols;
                break;
            case numberflag:
                theSymbols = symbolsDataStruct.numberSymbols;
                break;
            case chineseflag:
                theSymbols = symbolsDataStruct.chineseSymbols;
                break;
        }
        return StringUtil.convertStringstoList(theSymbols);
    }

    private void setSymbolsDataStruct(int keyboard, String[] theSymbols) {
        switch (keyboard) {
            case Global.KEYBOARD_EN:
                symbolsDataStruct.englishSymbols = theSymbols;
                break;
            case Global.KEYBOARD_NUM:
            case Global.KEYBOARD_SYM:
                symbolsDataStruct.numberSymbols = theSymbols;
                break;
            case Global.KEYBOARD_QK:
            case Global.KEYBOARD_T9:
                symbolsDataStruct.chineseSymbols = theSymbols;
                break;
        }
    }

    /**
     * @param text
     */
    @SuppressLint("NewApi")
    private Item getNewItem(String text) {
        Item item = new Item();
        TextView textViewText = new TextView(this);
        ImageView imageViewDelete = new ImageView(this);
        View viewLine = new View(this);

        textViewText.setText(text);
        textViewText.setGravity(Gravity.FILL);
        textViewText.setTextSize(configure.textSize);
        textViewText.setId(Global.generateViewId());

        imageViewDelete.setImageDrawable(getResources().getDrawable(R.drawable.quicksymbol_dim_delete));
        imageViewDelete.setId(Global.generateViewId());

        viewLine.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams textButtonLayoutParams = new RelativeLayout.LayoutParams(
                configure.textWidth,
                configure.itemHeight
        );
        textButtonLayoutParams.leftMargin = configure.lineLeftMargin;
        RelativeLayout.LayoutParams deleteButtonLayoutParams = new RelativeLayout.LayoutParams(
                configure.deleteWidth,
                configure.deleteHeight
        );
        RelativeLayout.LayoutParams lineLayoutParams = new RelativeLayout.LayoutParams(
                configure.lineWidth,
                configure.lineHeight
        );

        deleteButtonLayoutParams.addRule(
                RelativeLayout.RIGHT_OF,
                textViewText.getId()
        );
        lineLayoutParams.addRule(
                RelativeLayout.BELOW,
                textViewText.getId()
        );
        lineLayoutParams.leftMargin = configure.lineLeftMargin;

        item.textView = textViewText;
        item.imageView = imageViewDelete;
        item.viewLine = viewLine;
        item.textParams = textButtonLayoutParams;
        item.deleteParams = deleteButtonLayoutParams;
        item.lineParams = lineLayoutParams;
        item.relativeLayout.setGravity(Gravity.LEFT);//
        item.relativeLayout.setId(View.generateViewId());

        item.textView.setOnClickListener(item.textOnClick);
        item.textView.setOnLongClickListener(item.textLongOnClick);
        item.imageView.setOnClickListener(item.deleteOnClick);

        item.relativeLayout.addView(textViewText, textButtonLayoutParams);
        item.relativeLayout.addView(imageViewDelete, deleteButtonLayoutParams);
        item.relativeLayout.addView(viewLine, lineLayoutParams);
        return item;
    }

    /**
     *
     * @param texts
     */
    private void setItems(List<String> texts) {
        itemList.clear();
        innerRelativeLayout.removeAllViews();
        scrollView.removeAllViews();

        for (String symbol : texts) {
            Item item = getNewItem(symbol);

            if (itemList.size() != 0)
                item.layoutParams.addRule(RelativeLayout.BELOW, itemList.get(itemList.size() - 1).relativeLayout.getId());

            itemList.add(item);
            innerRelativeLayout.addView(item.relativeLayout, item.layoutParams);
        }

        scrollView.addView(innerRelativeLayout, innerLayoutParams);
    }

    private int convertCurrentKeyboardToCurentSymbolFlag(int keyboard) {
        int flag;
        switch (keyboard) {
            case Global.KEYBOARD_EN:
                flag = englishflag;
                break;
            case Global.KEYBOARD_SYM:
            case Global.KEYBOARD_NUM:
                flag = numberflag;
                break;
            case Global.KEYBOARD_QK:
            case Global.KEYBOARD_T9:
                flag = chineseflag;
                break;
            default:
                flag = chineseflag;
        }
        return flag;
    }

    private class Item {
        private RelativeLayout relativeLayout;
        private RelativeLayout.LayoutParams layoutParams;
        private TextView textView;
        private ImageView imageView;
        private View viewLine;
        private ViewGroup.LayoutParams textParams;
        private ViewGroup.LayoutParams deleteParams;
        private ViewGroup.LayoutParams lineParams;

        Item() {
            relativeLayout = new RelativeLayout(QuickSymbolAddActivity.this);
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private View.OnClickListener deleteOnClick = new View.OnClickListener() {
            @SuppressLint("NewApi")
            public void onClick(View view) {
                int position = itemList.indexOf(Item.this);

                if (itemList.size() != 1)
                    if (position == 0) {
                        itemList.get(position + 1).layoutParams.removeRule(RelativeLayout.BELOW);
                    } else if (position < itemList.size() - 1) {
                        itemList.get(position + 1).layoutParams.removeRule(RelativeLayout.BELOW);
                        if (itemList.size() > 0)
                            itemList.get(position + 1).layoutParams.addRule(RelativeLayout.BELOW, itemList.get(position - 1).relativeLayout.getId());
                    }
                itemList.remove(Item.this);
                innerRelativeLayout.removeView(Item.this.relativeLayout);
            }
        };
        private View.OnClickListener textOnClick = new View.OnClickListener() {
            public void onClick(View view) {
                dialog.showDialogWithText(textView.getText());
                dialog.recordViewId(view.getId());
            }
        };
        private View.OnLongClickListener textLongOnClick = new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                return false;
            }
        };
    }

    private class Configure {
        private int screenWidth;
        private int screenHeight;
        private int itemHeight;
        private int deleteWidth;
        private int deleteHeight;
        private int textWidth;
        private int lineHeight;
        private int lineWidth;
        private int lineLeftMargin;
        private float textSize;
        private int editHeight;
        private int editWidght;
        private int addWidght;

        Configure() {
            computeConfigure();
        }

        private void computeConfigure() {
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
            deleteWidth = Math.min(screenWidth, screenHeight) / 5;
            if (deleteWidth > 100) deleteWidth = Math.min(screenWidth, screenHeight) / 8;
            deleteHeight = deleteWidth;
            itemHeight = deleteHeight;
            textWidth = screenWidth - deleteWidth - deleteWidth / 2;

            lineHeight = itemHeight / 30;
            lineWidth = screenWidth - deleteWidth;
            lineLeftMargin = deleteWidth / 2;
            textSize = deleteWidth / 5;

            editHeight = itemHeight;//
            editWidght = screenWidth - deleteWidth;//
            addWidght = deleteHeight;//
        }

        public void refresh() {
            computeConfigure();
        }
    }

    private class SymbolAddItem {
        private RelativeLayout relativeLayout;
        private RelativeLayout.LayoutParams layoutParams;
        private EditText editText;
        private ImageView imageView;
        private RelativeLayout.LayoutParams editLayoutParams;
        private RelativeLayout.LayoutParams addLayoutParams;

        public void create(Context context) {
            relativeLayout = new RelativeLayout(QuickSymbolAddActivity.this);
            layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            relativeLayout.setId(Global.generateViewId());

            editText = new EditText(context);
            editText.setId(Global.generateViewId());
            editText.setFocusable(true);

            imageView = new ImageView(context);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.quicksymbol_dim_add));
            imageView.setId(Global.generateViewId());
            imageView.setOnClickListener(symbolAddItem.addOnClick);

            editLayoutParams = new RelativeLayout.LayoutParams(
                    configure.editWidght,
                    configure.editHeight
            );
            addLayoutParams = new RelativeLayout.LayoutParams(
                    configure.addWidght,
                    configure.editHeight
            );

            addLayoutParams.addRule(RelativeLayout.RIGHT_OF, editText.getId());
            relativeLayout.setGravity(Gravity.LEFT);
            relativeLayout.addView(editText, editLayoutParams);
            relativeLayout.addView(imageView, addLayoutParams);
        }

        private View.OnClickListener addOnClick = new View.OnClickListener() {
            @SuppressLint("NewApi")
            public void onClick(View view) {
                if (editText.getText().length() != 0) {
                    String text = editText.getText().toString();
                    editText.setText("");

                    Item item = getNewItem(text);
                    if (itemList.size() > 0)
                        item.layoutParams.addRule(RelativeLayout.BELOW, itemList.get(itemList.size() - 1).relativeLayout.getId());

                    itemList.add(item);
                    innerRelativeLayout.addView(item.relativeLayout, item.layoutParams);
                }
            }
        };
    }

    private class ConfirmItem {
        LinearLayout bottomLayout;
        Button cancelButton;
        Button confirmButton;
        Button resetButton;
        Button switchButton;
        int switchCount = 0;
        String[] switchFlagStrings;

        View.OnClickListener cancelButtonOnClick = new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        };

        View.OnClickListener confirmButtonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                List<String> symbols = new ArrayList<String>();
                for (Item item : itemList) {
                    symbols.add((String) item.textView.getText());
                }
                String[] symbolsForUpdate = StringUtil.convertListToString(symbols);
                setSymbolsDataStruct(currentSymbolFlag, symbolsForUpdate);
                try {
                    quickSymbolInterface.writeSymbolsToFile("default", symbolsDataStruct);
                } catch (IOException e) {
                    CommonFuncs.showToast(QuickSymbolAddActivity.this, getResources().getString(R.string.file_write_wrong));
                }
                finish();
            }
        };

        View.OnClickListener resetButtonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                setItems(symbols);
            }
        };

        View.OnClickListener switchButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCount++;
                currentSymbolFlag = switchCount % 3;
                setSwitchButtonTextByFlag(currentSymbolFlag);
                symbols = getCurrentSymbolsByFlag(currentSymbolFlag);
                setItems(symbols);
            }
        };

        public void create() {
            bottomLayout = (LinearLayout) findViewById(R.id.quicksymbol_dim_bottom_bar);

            cancelButton = (Button) findViewById(R.id.quicksymbol_dim_cancelbutton);
            confirmButton = (Button) findViewById(R.id.quicksymbol_dim_confirmbutton);
            resetButton = (Button) findViewById(R.id.quicksymbol_dim_resetbutton);
            switchButton = (Button) findViewById(R.id.quicksymbol_dim_switchbutton);
            switchFlagStrings = getResources().getStringArray(R.array.quicksymbols_dim_flag);

            confirmItem.cancelButton.setOnClickListener(confirmItem.cancelButtonOnClick);
            confirmItem.resetButton.setOnClickListener(confirmItem.resetButtonOnClickListener);
            confirmItem.confirmButton.setOnClickListener(confirmItem.confirmButtonOnClickListener);
            confirmItem.switchButton.setOnClickListener(confirmItem.switchButtonOnClickListener);
        }

        private void setSwitchButtonTextByFlag(int flag) {
            switchCount = flag;
            switchButton.setText(switchFlagStrings[flag]);
        }
    }

    private class DialogItem {

        AlertDialog.Builder dialogBuilder;
        RelativeLayout dialogLayout;
        EditText dialogEditText;
        AlertDialog realdialog;
        int currentTouchTextViewId = 0;

        public void create() {
            LayoutInflater inflater = LayoutInflater.from(QuickSymbolAddActivity.this);
            dialogBuilder = new AlertDialog.Builder(QuickSymbolAddActivity.this);
            dialogLayout = (RelativeLayout) inflater.inflate(R.layout.dim_quicksymbol_dialog, null);
            dialogEditText = (EditText) dialogLayout.findViewById(R.id.quicksymbol_dim_edit_view);
            dialogBuilder.setView(dialogLayout);
            dialog.dialogBuilder.setPositiveButton(getString(R.string.confirm), dialog.dialogConfirmOnClick);
            dialog.dialogBuilder.setNegativeButton(getString(R.string.cancel), dialog.dialogCancelOnClick);
        }

        private DialogInterface.OnClickListener dialogConfirmOnClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TextView textView = (TextView) findViewById(currentTouchTextViewId);
                String content = dialogEditText.getText().toString();
                textView.setText(content);
                dialog.cancel();
                dialog.dismiss();
            }
        };

        private DialogInterface.OnClickListener dialogCancelOnClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        };

        public void showDialogWithText(CharSequence text) {
            dialog.dialogEditText.setText(text);
            if (realdialog == null) realdialog = dialogBuilder.create();
            realdialog.show();
        }

        public void recordViewId(int id) {
            currentTouchTextViewId = id;
        }
    }
}
