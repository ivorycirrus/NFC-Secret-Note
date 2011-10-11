package com.lgcns.nfc.secretnote.share;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.Toast;

import com.lgcns.nfc.secretnote.data.DataObject;
import com.lgcns.nfc.secretnote.data.DataResolver;
import com.lgcns.nfc.secretnote.data.Label;
import com.lgcns.nfc.secretnote.drawer.NoteList;
import com.lgcns.nfc.secretnote.util.ByteUtils;

/**
 * Process received Encrypted document.
 */
public class ReceiveDocument extends Activity{
    
    private Label mLabel;
    private DataObject mData;
    
    private DataResolver mResolver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy . MM . dd");
        String date = "Modified : " + formatter.format(new Date());
        mResolver = new DataResolver(this);
        
        NdefMessage msg = (NdefMessage) this.getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
        NdefRecord[] records = msg.getRecords();
        if(records==null||records.length<2||!ShareNFC.MIME_TYPE_SAHRE_DATA.equals(ByteUtils.ByteArrayToString(records[0].getType())))
        {
            Toast.makeText(this, "Unknown data.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(records.length==2)
        {
            mLabel = new Label(Label.DATA_TYPE_TEXT, ByteUtils.ByteArrayToString(records[0].getPayload()), date);
            mData = new DataObject(Label.DATA_TYPE_TEXT);
            mData.setTextData(records[1].getPayload());            
        }
        else if (records.length==3)
        {
            mLabel = new Label(Label.DATA_TYPE_IMAGE, ByteUtils.ByteArrayToString(records[0].getPayload()), date);
            mData = new DataObject(Label.DATA_TYPE_IMAGE);
            mData.setTextData(records[1].getPayload());
            mData.setImageData(records[2].getPayload());
        }
        else
        {
            Toast.makeText(this, "Unknown data.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mResolver.saveDataList(mLabel, mData);
        startActivity(new Intent(this,NoteList.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

}
