package com.railzapp.tours.model.tour;

public class Tour {
    private long id;
    private String mCode;
    private String mName;
    private String mDuration;
    private String mComplexity;
    private String mShortDesc;
    private String mLongDesc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getComplexity() {
        return mComplexity;
    }

    public void setComplexity(String complexity) {
        this.mComplexity = complexity;
    }

    public String getShortDesc() {
        return mShortDesc;
    }

    public void setShortDesc(String short_desc) {
        this.mShortDesc = short_desc;
    }

    public String getLongDesc() {
        return mLongDesc;
    }

    public void setLongDesc(String long_desc) {
        this.mLongDesc = long_desc;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return mName;
    }
}