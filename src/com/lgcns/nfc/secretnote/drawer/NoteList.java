
package com.lgcns.nfc.secretnote.drawer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.lgcns.nfc.secretnote.R;
import com.lgcns.nfc.secretnote.data.DataResolver;
import com.lgcns.nfc.secretnote.data.Label;

/**
 * Show Document list.
 */
public class NoteList extends Activity {

    private ArrayList<Label> mNoteLabel;
    private DataResolver resolver;
    private static final String TAG = "NoteList";

    //private static final int REQUEST_CODE_NEW_TEXT = 0;
    private static final int REQUEST_CODE_NEW_IMAGE = 1;
    private static final int REQUEST_CODE_EDIT = 10;
    private static final int REQUEST_CODE_DELETE = 100;

    private NoteAdapter<Label> adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list);

        init();
    }

    // Load Document title and initialize listeners on ListView.
    private void init() {
        resolver = new DataResolver(this);
        mNoteLabel = resolver.getDataList();
        Log.d(TAG, "Note List init (# of note item = " + mNoteLabel.size() + " )");

        lv = (ListView) findViewById(R.id.lv_note);
        adapter = new NoteAdapter<Label>(this, 0, mNoteLabel);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(mItemCliclListemer);
        lv.setOnItemLongClickListener(mItemLongClickListener);

        findViewById(R.id.btn_newitem).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NoteList.this, CreateNewDocument.class), REQUEST_CODE_NEW_IMAGE);                   
            }
        });
    }

    // Refresh document list 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult...");
        mNoteLabel = resolver.getDataList();
        lv.setAdapter(null);
        adapter = null;
        adapter = new NoteAdapter<Label>(this, 0, mNoteLabel);
        lv.setAdapter(adapter);        
    }

    // ArrayAdapter for Document list
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
            if(mNoteLabel.get(position).getType()==1)//text
            {
                tv_image.setImageResource(android.R.drawable.ic_menu_edit);
            }
            else if(mNoteLabel.get(position).getType()==2)//image
            {
                tv_image.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            return v;
        }

    }

    // [Item Click] Edit a selected item
    private OnItemClickListener mItemCliclListemer = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Label label = mNoteLabel.get(position);
            Intent i = null;
            switch(label.getType())
            {                
                case Label.DATA_TYPE_IMAGE:
                case Label.DATA_TYPE_TEXT:
                    i = new Intent(NoteList.this,CreateNewDocument.class);
                    i.putExtra(Label.KEY_EDIT_ITEM, label.get_id());
                    startActivityForResult(i, REQUEST_CODE_EDIT);
                    break;
            }
        }
    };

    // [Item Long Click] Delete a selected item
    private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(NoteList.this).setTitle("Delete a item?")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resolver.deleteData(mNoteLabel.get(position));
                    onActivityResult(REQUEST_CODE_DELETE, RESULT_OK, null);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            return true;
        }
    };

}
