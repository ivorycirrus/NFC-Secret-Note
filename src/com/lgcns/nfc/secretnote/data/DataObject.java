package com.lgcns.nfc.secretnote.data;

/**
 * Encrypted Document Data
 *
 * This data is stored encrypted files on the internal memory.  
 */
public class DataObject {
    
    private int mType;
    private byte[] mTextData = null;
    private byte[] mImageData = null;
    
    public DataObject(int Type) {
        mType = Type;
    }

    public byte[] getTextData() {
        return mTextData;
    }

    public void setTextData(byte[] textData) {
        this.mTextData = textData;
    }

    public byte[] getImageData() {
        return mImageData;
    }

    public void setImageData(byte[] imageData) {
        this.mImageData = imageData;
    }
    
    public int getType()
    {
        return mType;
    }
}
