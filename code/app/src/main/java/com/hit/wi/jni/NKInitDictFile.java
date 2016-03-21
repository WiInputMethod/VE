package com.hit.wi.jni;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;
import com.hit.wi.t9.R;
import com.hit.wi.define.FilePath;
import com.hit.wi.util.VersionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class NKInitDictFile {
    private static final void FileWrite(final InputStream in, final String name) {
        try {
            final byte[] buffer = new byte[10240];
            int length = 0;
            final FileOutputStream fos = new FileOutputStream(
                    FilePath.ROOT_DIR + name);
            while ((length = in.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static final void FileWriteAppend(final InputStream in,
                                              final String name) {
        try {
            final int length = in.available();
            final byte[] buffer = new byte[length];
            in.read(buffer);
            final FileOutputStream fos = new FileOutputStream(
                    FilePath.ROOT_DIR + name, true);
            fos.write(buffer);
            fos.close();
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static final void InitFileOK(final Context context) {
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        final int versionCode = VersionUtil.getVersionCode(context);
        final Editor edit = sp.edit();
        edit.putInt("INIT_FILE_UNDER", versionCode);
        edit.apply();
    }

    private static final boolean IsInitFileOk(final Context context) {
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        final int nowVersionCode = VersionUtil.getVersionCode(context);
        int isUnder = sp.getInt("INIT_FILE_UNDER", 0);
        return isUnder == nowVersionCode;

    }

    public static final void NKInitWiDict(final Context context) {
        if (IsInitFileOk(context))
            return;

        final File dictfile = new File(FilePath.ROOT_DIR);
        if (dictfile.exists() != true)
            dictfile.mkdir();
        final File emoji = new File(FilePath.ROOT_DIR + "emoji");
        if (emoji.exists() != true)
            emoji.mkdir();
        final File shuangpin = new File(FilePath.ROOT_DIR + "shuangpin");
        if (shuangpin.exists() != true)
            shuangpin.mkdir();
        final File pymap = new File(FilePath.ROOT_DIR + "PinYinDict");
        if (pymap.exists() != true)
            pymap.mkdir();

        final File jjmap = new File(FilePath.ROOT_DIR + "PinYinDict/JiuJianMap");
        if (jjmap.exists() != true)
            jjmap.mkdir();

        final File sdmap = new File(FilePath.ROOT_DIR + "PinYinDict/SegmentDict");
        if (sdmap.exists() != true)
            sdmap.mkdir();

        final AssetManager assetManager = context.getAssets();
        try {
            String filename[] = assetManager.list("dict");
            for (int i = 0; i < filename.length; i++) {
                if (filename[i].equals("PinYinDict")
                        || filename[i].equals("shuangpin")
                        || filename[i].equals("emoji")
                        || filename[i].equals("bigram")
                        || filename[i].equals("word"))
                    continue;
                String path = "dict/" + filename[i];
                InputStream in = assetManager.open(path);
                FileWrite(in, filename[i]);
            }
            filename = null;
            filename = assetManager.list("dict/emoji");
            for (int i = 0; i < filename.length; i++) {
                String path = "dict/emoji/" + filename[i];
                InputStream in = assetManager.open(path);
                FileWrite(in, "emoji/" + filename[i]);
            }
            filename = null;
            filename = assetManager.list("dict/shuangpin");
            for (int i = 0; i < filename.length; i++) {
                String path = "dict/shuangpin/" + filename[i];
                InputStream in = assetManager.open(path);
                FileWrite(in, "shuangpin/" + filename[i]);
            }
            filename = null;
            filename = assetManager.list("dict/bigram");
            for (int i = 0; i < filename.length; i++) {
                final String path = "dict/bigram/" + filename[i];
                final InputStream in = assetManager.open(path);
                if (i == 0) {
                    FileWrite(in, "bigram.dict");
                } else {
                    FileWriteAppend(in, "bigram.dict");
                }
            }
            filename = null;
            filename = assetManager.list("dict/word");
            for (int i = 0; i < filename.length; i++) {
                final String path = "dict/word/" + filename[i];
                final InputStream in = assetManager.open(path);
                if (i == 0) {
                    FileWrite(in, "word.dict");
                } else {
                    FileWriteAppend(in, "word.dict");
                }
            }
            filename = null;
            filename = assetManager.list("dict/PinYinDict/JiuJianMap");
            for (int i = 0; i < filename.length; i++) {
                String path = "dict/PinYinDict/JiuJianMap/" + filename[i];
                InputStream in = assetManager.open(path);
                FileWrite(in, "PinYinDict/JiuJianMap/" + filename[i]);
            }

            filename = null;
            filename = assetManager.list("dict/PinYinDict/SegmentDict");
            for (int i = 0; i < filename.length; i++) {
                String path = "dict/PinYinDict/SegmentDict/" + filename[i];
                InputStream in = assetManager.open(path);
                if (i == 0) {
                    FileWrite(in, "PinYinDict/SegmentDict/segment.bin");
                } else {
                    FileWriteAppend(in, "PinYinDict/SegmentDict/segment.bin");
                }
            }
        } catch (final IOException e) {
            Log.d("WIVE","DIct wrong:"+e.toString());
            e.printStackTrace();
        }
        InitFileOK(context);
    }

    /**
     * 导入联系人或清空联系人
     *
     * @param context
     * @return
     */
    public final static int InitContactList(final Context context) {
        int counter = 0;
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sp.getBoolean("import_contacts", false)) {
            /** To get the Contacts info from the database */
            final ContentResolver content = context.getContentResolver();
            final Cursor cursor = content.query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    null);

            if (cursor == null) {
                Toast.makeText(context, "导入联系人失败，请检查权限", Toast.LENGTH_SHORT).show();
                return 0;
            }
            while (cursor.moveToNext()) {
                final int nameIndex = cursor
                        .getColumnIndex(PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameIndex);
                counter++;
                DictManager.InsertContactList(name);
            }
            cursor.close();

            final CharSequence message_import = context.getResources()
                    .getString(R.string.success_import_contact)
                    + counter
                    + context.getResources().getString(R.string.num);
            final Toast t = Toast.makeText(context, message_import,
                    Toast.LENGTH_SHORT);
            t.show();
        } else {
            DictManager.CleanUserlessContactList();
            final Toast t = Toast.makeText(context,
                    R.string.clear_contact_dict, Toast.LENGTH_SHORT);
            t.show();
        }
        return counter;
    }
}
