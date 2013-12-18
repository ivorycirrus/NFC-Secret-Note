package com.lgcns.nfc.secretnote.data;

/**
 * Document Label data.
 * 
 * This dataStored on Database with no encrypting. 
 * 
 * This data included below.
 *  1. Document type
 *  2. Document title
 *  3. Data file name
 *  4. Last modified date
 */

public class Label {
    
    public static final int DATA_TYPE_TEXT = 1;
    public static final int DATA_TYPE_IMAGE = 2;
    public static final String KEY_EDIT_ITEM = "edit_item"; 
    public static final String KEY_SHARE_ITEM = "share_item";
    
    private int _id;
    private int type;
    private String title;
    private String filename;
    private String date;
    
    public Label(int type, String title, String date) {
        super();
        this.type = type;
        this.title = title;
        this.date = date;
    }

    public Label(int id, int type, String title, String filename, String date) {
        super();
        _id = id;
        this.type = type;
        this.title = title;
        this.filename = filename;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int get_id() {
        return _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
