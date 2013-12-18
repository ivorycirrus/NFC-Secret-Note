package com.lgcns.nfc.secretnote.share;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lgcns.nfc.secretnote.R;
import com.lgcns.nfc.secretnote.data.DataObject;
import com.lgcns.nfc.secretnote.data.DataResolver;
import com.lgcns.nfc.secretnote.data.Label;
import com.lgcns.nfc.secretnote.util.ByteUtils;

/**
 * Send Encrypted Document using NFC feature. 
 */
public class ShareNFC extends Activity {

    private NfcAdapter mAdapter;   
    private NdefMessage mMessage;
    private BroadcastReceiver mPushReceiver;
    private IntentFilter mPushFilter;
    
    private Label mLabel;
    private DataObject mData;
    
    public static final String MIME_TYPE_SAHRE_DATA = "secretnote/data";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_document);
        
        //Check NFC feature
        mAdapter = NfcAdapter.getDefaultAdapter(this);        
        if(mAdapter==null|| !mAdapter.isEnabled())
        {
            Toast.makeText(this, "Sorry. Document sharing uses NFC feature.", Toast.LENGTH_LONG).show();
            finish();
        }
        
        //Check share data
        int label_id = getIntent().getIntExtra(Label.KEY_SHARE_ITEM, -1);
        if(label_id<0)
        {
            Toast.makeText(this, "Sorry. Sharing data is not found.", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            setMessage(label_id);
        }
        
        //Compose NDEF
        byte[] type = ByteUtils.StringToByteArray(MIME_TYPE_SAHRE_DATA);
        byte[] payloadTitle = ByteUtils.StringToByteArray(mLabel.getTitle());
        byte[] payloadText = mData.getTextData();
        byte[] payloadImage = mData.getImageData();
        
        NdefRecord recordTitle = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,type,new byte[0],payloadTitle);
        NdefRecord recordText = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,type,new byte[0],payloadText);       
        
        if(mLabel.getType()==Label.DATA_TYPE_TEXT)
        {
            mMessage = new NdefMessage(new NdefRecord[]{recordTitle,recordText});
        }
        else if(mLabel.getType()==Label.DATA_TYPE_IMAGE)
        {
            NdefRecord recordImage = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,type,new byte[0],payloadImage);
            mMessage = new NdefMessage(new NdefRecord[]{recordTitle,recordText,recordImage});
        }        
        Log.d("--LLCP_SEND--", "records : "+mMessage.getRecords().length+" data : "+mMessage.getRecords()[1].getPayload().length);
        
        //Broadcast Receiver for handling results.
        mPushReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                try{
                if (action.equals("android.nfc.action.NDEF_PUSH_SUCCEEDED")) {
                    Toast.makeText(ShareNFC.this, "Share Success", Toast.LENGTH_LONG).show();                     
                }
                else if (action.equals("android.nfc.action.NDEF_PUSH_FAILED")) {
                    Toast.makeText(ShareNFC.this, "Share Fail!!", Toast.LENGTH_LONG).show();
                }}
                finally
                {
                    ShareNFC.this.finish();
                }
            }
        };
        
        mPushFilter = new IntentFilter();
        mPushFilter.addAction("android.nfc.action.NDEF_PUSH_SUCCEEDED");
        mPushFilter.addAction("android.nfc.action.NDEF_PUSH_FAILED");

    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && mAdapter.isEnabled()) {
                if (mPushReceiver != null && mPushFilter != null)
                    registerReceiver(mPushReceiver, mPushFilter);
                if (mMessage != null)
                    mAdapter.enableForegroundNdefPush(this, mMessage);
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null && mAdapter.isEnabled()) {            
            mAdapter.disableForegroundNdefPush(this);
            if (mPushReceiver != null) 
                unregisterReceiver(mPushReceiver);            
        }
    }
    
    private void setMessage(int id) {
        DataResolver resolver = new DataResolver(this);
        mLabel = resolver.getData(id);
        mData = resolver.readFile(mLabel);        
    }
    
}
