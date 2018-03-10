package com.internshipbcc.itrip.Util;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Sena on 10/03/2018.
 */


@SuppressLint("ParcelCreator")
public class ItemSuggestion implements SearchSuggestion {

    private String body;
    private boolean mIsHistory = false;

    ItemSuggestion(String body){
        this.body = body;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
