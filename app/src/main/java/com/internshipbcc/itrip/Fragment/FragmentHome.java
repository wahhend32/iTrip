package com.internshipbcc.itrip.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internshipbcc.itrip.R;
import com.internshipbcc.itrip.Util.DataHelper;
import com.internshipbcc.itrip.Util.ItemSuggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sena on 10/03/2018.
 */

public class FragmentHome extends Fragment {
    public final static int VOICE_SEARCH_CODE = 1019;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 300;
    String mLastQuery;

    private FloatingSearchView fsv;
    private SliderLayout sliderLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        fsv = rootView.findViewById(R.id.floating_search_view);
        sliderLayout = rootView.findViewById(R.id.slider);
        setupSearch();
        setupSlider();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }
    private void setupSearch(){
        fsv.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    fsv.clearSuggestions();
                } else {
                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    fsv.showProgress();
                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<ItemSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    fsv.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    fsv.hideProgress();
                                }
                            });
                }
            }
        });
        fsv.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                mLastQuery = searchSuggestion.getBody();
                fsv.setSearchText(mLastQuery);
                fsv.clearSuggestions();
                fsv.clearFocus();
            }
            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

            }
        });

        fsv.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                List<ItemSuggestion> history = DataHelper.getHistory(getActivity(), 3);
                if(history!=null)
                    fsv.swapSuggestions(history);
            }
            @Override
            public void onFocusCleared() {
                //fsv.setSearchBarTitle(mLastQuery);
            }
        });

        fsv.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                ItemSuggestion colorSuggestion = (ItemSuggestion) item;
                String textColor = "#000000";
                if (colorSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = colorSuggestion.getBody()
                        .replaceFirst(fsv.getQuery(),
                                "<font color=\"#555\">" + fsv.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        fsv.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.voice_search:
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                        try{
                            getActivity().startActivityForResult(intent, VOICE_SEARCH_CODE);
                        }catch (ActivityNotFoundException e){
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }

    private void setupSlider() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("/slideshow");
        final HashMap<String, String> slideShow = new HashMap<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Kosongkan semua
                slideShow.clear();
                sliderLayout.removeAllSliders();
                //Mendapatkan semua child dari /slideshow dan dimasukkan ke hashmap
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String title = child.child("title").getValue(String.class);
                    String image = child.child("image").getValue(String.class);
                    slideShow.put(title, image);
                }
                for(String name : slideShow.keySet()){
                    TextSliderView textSliderView = new TextSliderView(getContext());
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(slideShow.get(name))
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);

                    sliderLayout.addSlider(textSliderView);
                }
                sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                sliderLayout.setCustomAnimation(new DescriptionAnimation());
                sliderLayout.setDuration(10000);
                sliderLayout.startAutoCycle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Importan Method

    private void performSearch(String query){

    }

    //Support Method
    public void setSearchQuery(String query){
        fsv.setSearchText(query);
        mLastQuery = query;
    }
}
