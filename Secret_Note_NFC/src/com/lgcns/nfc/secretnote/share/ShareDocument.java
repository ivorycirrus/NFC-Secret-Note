package com.lgcns.nfc.secretnote.share;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lgcns.nfc.secretnote.R;
import com.lgcns.nfc.secretnote.data.DataResolver;
import com.lgcns.nfc.secretnote.data.Label;

/**
 * Show document list for Sharing with other NFC handsets.
 * And click one, go to data send page. 
 */
public class ShareDocument extends Activity {

    private ArrayList<Label> mNoteLabel;
    private DataResolver resolver;
    
    private final int TYPE_TEXT = Label.DATA_TYPE_TEXT;
    private final int TYPE_IMAGE = Label.DATA_TYPE_IMAGE;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list);
        
        resolver = new DataResolver(this);
        mNoteLabel = resolver.getDataList();
        
        ListView lv = (ListView) findViewById(R.id.lv_note);
        NoteAdapter<Label> adapter = new NoteAdapter<Label>(this, 0, mNoteLabel);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(mItemCliclListemer);
        
        Button btn = (Button) findViewById(R.id.btn_newitem);
        btn.setText("Cancel");
        btn.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View v) {
                ShareDocument.this.finish();
            }
        });
    }
    
    private OnItemClickListener mItemCliclListemer = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(ShareDocument.this).setTitle("Share this item?")
            .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(ShareDocument.this,ShareNFC.class);
                    i.putExtra(Label.KEY_SHARE_ITEM, mNoteLabel.get(position).get_id());
                    startActivity(i);
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    };
    
    @SuppressWarnings("hiding")
    private class NoteAdapter<Label> extends ArrayAdapter<Label> {
        public NoteAdapter(Context context, int textViewResourceId, List<Label> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_list, null);
            }
            TextView tv_label = (TextView) v.findViewById(R.id.item_title);
            TextView tv_date = (TextView) v.findViewById(R.id.item_date);
            ImageView tv_image = (ImageView) v.findViewById(R.id.item_type);
            tv_label.setText(mNoteLabel.get(position).getTitle());
            tv_date.setText(mNoteLabel.get(position).getDate());
            if(mNoteLabel.get(position).getType()==TYPE_TEXT)//text
            {
                tv_image.setImageResource(android.R.drawable.ic_menu_edit);
            }
            else if(mNoteLabel.get(position).getType()==TYPE_IMAGE)//image
            {
                tv_image.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            return v;
        }

    }
}
