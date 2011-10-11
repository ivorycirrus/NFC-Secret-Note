
package com.lgcns.nfc.secretnote;

import com.lgcns.nfc.secretnote.drawer.NoteList;
import com.lgcns.nfc.secretnote.share.ShareDocument;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Create main menu
 */
public class SceretNote extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // 'Open Drawer' button
        findViewById(R.id.btn_drawer).setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SceretNote.this, NoteList.class));
            }
        });
        // 'Share Document' Button
        findViewById(R.id.btn_exchange).setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SceretNote.this, ShareDocument.class));
            }
        });
        // 'Exit' Button
        findViewById(R.id.btn_exit).setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
