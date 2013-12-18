
package com.lgcns.nfc.secretnote.drawer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lgcns.nfc.secretnote.R;
import com.lgcns.nfc.secretnote.data.DataObject;
import com.lgcns.nfc.secretnote.data.DataResolver;
import com.lgcns.nfc.secretnote.data.Label;
import com.lgcns.nfc.secretnote.util.BitmapUtils;
import com.lgcns.nfc.secretnote.util.ByteUtils;
import com.lgcns.nfc.secretnote.util.Encryptor;
/**
 * Document Editor.
 * 
 * 1. If incoming intent has incomming_item_id, 
 *    it shows Edit mode , and the other case Create Mode.
 * 
 * 2. It has password data When store document, the document will encrypted.
 * 
 * 3. Image data is exists, the document type will be DATA_TYPE_IMAGE.
 *    and image is not exists, the document type will auto-selected DATA_TYPE_TEXT. 
 */
public class CreateNewDocument extends Activity implements OnClickListener {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private int incomming_item_id = -1;
    private String mPassword = "";
    private ProgressDialog pd;

    private Uri mImageCaptureUri;
    private Bitmap mImage;

    private ImageView mImageView;
    private Button mInsertImage;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_image);

        mInsertImage = (Button) findViewById(R.id.ni_getimage);
        mImageView = (ImageView) findViewById(R.id.ni_image);
        mInsertImage.setOnClickListener(this);

        // If incoming intent has incomming_item_id, shows Edit Mode.
        incomming_item_id = getIntent().getIntExtra(Label.KEY_EDIT_ITEM, -1);
        if (incomming_item_id >= 0) {
            inputPassword();
        }

        findViewById(R.id.ni_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Show progress dialog while data progressing/
        findViewById(R.id.ni_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(CreateNewDocument.this);
                pd.setMessage("Please wait...");
                Thread savethread = new Thread(saveMessage);
                savethread.start();
                pd.show();
                try {
                    savethread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    pd.dismiss();
                    CreateNewDocument.this.setResult(RESULT_OK);
                    CreateNewDocument.this.finish();
                }
            }
        });

    }

    // Show password input dialog for Edit Mode
    private void inputPassword() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.password_dialog, (ViewGroup) findViewById(R.id.layout_root));

        AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
        aDialog.setTitle("Input Password!");
        aDialog.setView(layout);

        aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mPassword = ((EditText) layout.findViewById(R.id.image)).getText().toString();
                setMessage();
            }
        });
        aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CreateNewDocument.this.finish();
            }
        });
                
        AlertDialog ad = aDialog.create();        
        ad.show();
    }

    // Load and decrypt stored document.
    private void setMessage() {
        DataResolver resolver = new DataResolver(this);
        Label label = resolver.getData(incomming_item_id);
        DataObject data = resolver.readFile(label);        
        ((EditText) findViewById(R.id.ni_title)).setText(label.getTitle());
        ((EditText) findViewById(R.id.ni_text)).setText(Encryptor.decrypt(data.getTextData(), mPassword));
        ((EditText) findViewById(R.id.ni_password)).setText(mPassword);
        if(data.getType()==Label.DATA_TYPE_IMAGE){
            mImage = BitmapUtils.byteArrayToBitmap(Encryptor.decrypt(data.getImageData(), ByteUtils.StringToByteArray(mPassword)));        
            ((ImageView) findViewById(R.id.ni_image)).setImageBitmap(mImage);
        }
    }

    // Encrypt and Store document
    private Runnable saveMessage = new Runnable() {
        @Override
        public void run() {            
            String title = ((EditText) findViewById(R.id.ni_title)).getText().toString();
            if (title == null || title.equals("")) title = "<No title>";

            String text = ((EditText) findViewById(R.id.ni_text)).getText().toString();
            if(text==null||text.length()==0) text = " ";
            String password = ((EditText) findViewById(R.id.ni_password)).getText().toString();
            byte[] bitmapData = BitmapUtils.bitmapToByteArray(mImage);
            
            int type = Label.DATA_TYPE_TEXT;
            if(mImage!=null)type = Label.DATA_TYPE_IMAGE;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy . MM . dd");
            String date = "Modified : " + formatter.format(new Date());

            DataResolver resolver = new DataResolver(CreateNewDocument.this);
            if (incomming_item_id == -1) {
                Label label = new Label(type, title, date);
                DataObject data = new DataObject(Label.DATA_TYPE_IMAGE);
                data.setTextData(Encryptor.encrypt(text, password));
                if(mImage!=null) data.setImageData(Encryptor.decrypt(bitmapData, ByteUtils.StringToByteArray(password)));
                resolver.saveDataList(label, data);
            } else {
                Label label = new Label(type, title, date);
                DataObject data = new DataObject(Label.DATA_TYPE_IMAGE);
                data.setTextData(Encryptor.encrypt(text, password));
                if(mImage!=null) data.setImageData(Encryptor.decrypt(bitmapData, ByteUtils.StringToByteArray(password)));
                resolver.updateDataList(label, data);
            }
        }
    };

    // Get Image from Camera
    private void doTakePhotoAction() {        
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // temporary files setting
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // Get image from gallery
    private void doTakeAlbumAction() {
        // Call gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // Image processing
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // Receive cropped image and delete temporary files after processing.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    mImage = extras.getParcelable("data");
                    mImageView.setImageBitmap(mImage);
                }

                // delete temporary files
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 480);
                intent.putExtra("outputY", 320);
                intent.putExtra("aspectX", 1.5);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this).setTitle("Choose Source").setPositiveButton("Camera", cameraListener).setNeutralButton("Gallery", albumListener)
                .setNegativeButton("Cancel", cancelListener).show();
    }
}
