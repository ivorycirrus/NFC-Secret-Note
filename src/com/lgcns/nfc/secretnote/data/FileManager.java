
package com.lgcns.nfc.secretnote.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

/**
 * File Management class for encrypted data.
 */
/*package*/final class FileManager {

    private static final String TAG = "FileManager";
    private Context context;

    FileManager(Context c) {
        this.context = c;
    }

    final boolean writeToFile(String filename , byte[] out) {        
        FileOutputStream fos = null;
        try {
            Log.d(TAG, "Write file...(filename : "+filename+" / size : "+out.length+"bytes )");
            fos = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
            fos.write(out);
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {    
                    Log.d(TAG, "Write error!");
                    e.printStackTrace();
                }
            }
        }

    }

    final byte[] readFromFile(String filename) {
        Log.d(TAG, "Read From File : "+filename);
        byte[] data = null;
        File outputfile = new File(context.getFilesDir(),filename);
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename); 
            Log.d(TAG, "FIS : "+fis);
            data = new byte[(int) outputfile.length()];
            Log.d(TAG, "data : "+data.length);
            while (fis.read(data) != -1) {
                ;
            }
            Log.d(TAG, "Read Size : "+(data==null?data:data.length));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }        
    }
    
    final boolean deleteFile(String filename)
    {
        File file = null;
        try {
            file = context.getFileStreamPath(filename);
            file.delete();
        } catch (Exception e) {
            return false;
        } 
        return true;
    }

}
