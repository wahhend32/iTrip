package com.internshipbcc.itrip.Util;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sena on 10/03/2018.
 */

public class DataHelper {


    public static List<ItemSuggestion> data = new ArrayList<>(
            Arrays.asList(
                    new ItemSuggestion("Malang"),
                    new ItemSuggestion("Sidoarjo"),
                    new ItemSuggestion("Surabaya"),
                    new ItemSuggestion("Paralayang"),
                    new ItemSuggestion("Gunung Bromo"),
                    new ItemSuggestion("Kampung Warna Warni"),
                    new ItemSuggestion("Ijen")
            )
    );

    public interface OnFindSuggestionsListener {
        void onResults(List<ItemSuggestion> results);
    }

    private OnFindSuggestionsListener listener;

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        data.get(0).setIsHistory(true);
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<ItemSuggestion> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {
                    for (ItemSuggestion suggestion : data) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<ItemSuggestion>() {
                    @Override
                    public int compare(ItemSuggestion lhs, ItemSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<ItemSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static List<ItemSuggestion> getHistory(Context context, int limit){
        List<ItemSuggestion> history = new ArrayList<>();
        for(ItemSuggestion item : data){
            if(item.getIsHistory())
                history.add(item);
        }
        if(history.isEmpty())
            return null;
        return history;
    }
}
