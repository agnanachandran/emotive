package ca.pluszero.emotive.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.youtube.player.YouTubeIntents;

import java.io.File;
import java.util.List;

import ca.pluszero.emotive.MusicLauncher;
import ca.pluszero.emotive.NetworkManager;
import ca.pluszero.emotive.R;
import ca.pluszero.emotive.adapters.PlacesAutoCompleteAdapter;
import ca.pluszero.emotive.adapters.YouTubeListAdapter;
import ca.pluszero.emotive.clients.YouTubeClient;
import ca.pluszero.emotive.models.PrimaryOption;
import ca.pluszero.emotive.models.YouTubeVideo;

public class MainSectionFragment extends Fragment implements View.OnClickListener, YouTubeClient.OnFinishedListener {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private Button[] primaryButtons;

    private Button bFirstButton;
    private Button bSecondButton;
    private Button bThirdButton;
    private Button bFourthButton;
    private Button bFifthButton;
    private Button bSixthButton;

    private ListView lvQueryResults;

    private SimpleCursorAdapter mAdapter;

    private PrimaryOption mPrimaryOption;

    private AutoCompleteTextView etSearchView;
    private View rootView;
    private TextSwitcher mSwitcher;
    private Animation slideUp;
    private ImageView imgFirstOption;
    private ImageView imgSecondOption;
    private ImageView imgThirdOption;
    private ImageView imgFourthOption;
    private ImageView imgFifthOption;
    private ImageView imgSixthOption;
    private ImageView[] primaryImages;

    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {

            // Create a new TextView
            TextView t = new TextView(getActivity());
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            t.setShadowLayer(3, -3, -3, R.color.text_shadow);
            t.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
            t.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            return t;
        }
    };

    public MainSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        lvQueryResults = (ListView) rootView.findViewById(R.id.lvQueryResults);
        setupAnimations();
        // TODO: can this just be getActivity() instead of also getApplicationContext()?

        mSwitcher.setText("  Good morning, Anojh\n What do you want to do?");

        // tvSearchQuery.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        setupPrimaryButtons(rootView);

        etSearchView = (AutoCompleteTextView) rootView.findViewById(R.id.mainSearchView);
        etSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }

        });

        setHasOptionsMenu(true);
        return rootView;
    }

    private void setupAnimations() {
        mSwitcher = (TextSwitcher) rootView.findViewById(R.id.mainTextview);
        // Set the factory used to create TextViews to switch between.
        mSwitcher.setFactory(mFactory);

                /*
         * Set the in and out animations. Using the fade_in/out animations
         * provided by the framework.
         */
        Animation in = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_out_right);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
    }

    private void setupPrimaryButtons(View rootView) {
        bFirstButton = (Button) rootView.findViewById(R.id.bFirstOption);
        bSecondButton = (Button) rootView.findViewById(R.id.bSecondOption);
        bThirdButton = (Button) rootView.findViewById(R.id.bThirdOption);
        bFourthButton = (Button) rootView.findViewById(R.id.bFourthOption);
        bFifthButton = (Button) rootView.findViewById(R.id.bFifthOption);
        bSixthButton = (Button) rootView.findViewById(R.id.bSixthOption);

        imgFirstOption = (ImageView) rootView.findViewById(R.id.imgFirstOption);
        imgSecondOption = (ImageView) rootView.findViewById(R.id.imgSecondOption);
        imgThirdOption = (ImageView) rootView.findViewById(R.id.imgThirdOption);
        imgFourthOption = (ImageView) rootView.findViewById(R.id.imgFourthOption);
        imgFifthOption = (ImageView) rootView.findViewById(R.id.imgFifthOption);
        imgSixthOption = (ImageView) rootView.findViewById(R.id.imgSixthOption);

        primaryButtons = new Button[]{bFirstButton, bSecondButton, bThirdButton, bFourthButton, bFifthButton, bSixthButton};
        primaryImages = new ImageView[]{imgFirstOption, imgSecondOption, imgThirdOption, imgFourthOption, imgFifthOption, imgSixthOption};
        for (Button b : primaryButtons) {
            b.setOnClickListener(this);
        }

        for (ImageView img : primaryImages) {
            img.setOnClickListener(this);
        }
    }

    private void placeSearchOptions() {
//        setupSecondaryOptions(getResources().getStringArray(R.array.search_options), R.string.search_options_title_label);
        etSearchView.setOnItemClickListener(null); // TODO: do this for other options
    }

    private void performSearch(String query) {
        if (mPrimaryOption == PrimaryOption.FOOD) {
            startMapsSearch(query);
        } else if (mPrimaryOption == PrimaryOption.LISTEN) {
            startMusicSearchDevice(query);
        } else if (mPrimaryOption == PrimaryOption.GOOGLE) {
            startGoogleSearchAnything(query);
        } else if (mPrimaryOption == PrimaryOption.YOUTUBE) {
            startYouTubeSearch(query);
        }
    }

    private void startYouTubeSearch(CharSequence query) {
        if (NetworkManager.isConnected(getActivity())) {
            YouTubeClient.getInstance(this).getYouTubeSearch(query.toString());
        } else {
            // No interwebs; display Toast. TODO
        }
        // Intent intent = new Intent(Intent.ACTION_SEARCH);
        // intent.setPackage("com.google_icon.android.youtube");
        // intent.putExtra("search_query", query);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
    }

    private void startMapsSearch(CharSequence query) {
        String url = "geo:0,0?q=" + query;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void startGoogleSearchAnything(CharSequence query) {
        Intent browserIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        browserIntent.putExtra(SearchManager.QUERY, query.toString());
        startActivity(browserIntent);
    }

    private void startMusicSearchDevice(String query) {
        MusicLauncher musicLauncher = MusicLauncher.getInstance(this);
        musicLauncher.searchMusic(query);
        String[] artistColumns = {MediaStore.Audio.Media.ARTIST};
        int[] mSongListItems = {R.id.tvQueryTitleCard};
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row_card, null, artistColumns,
                mSongListItems);

        bringUpListviewAndDismissKeyboard();
        lvQueryResults.setAdapter(mAdapter);
        lvQueryResults.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mAdapter.getItem(position);
                int index = c.getColumnIndex(MediaStore.Audio.Media.DATA);
                String songPath = c.getString(index);
                Log.d(getTag(), songPath);
                File file = new File(songPath);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });
    }

    private void bringUpListviewAndDismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchView.getWindowToken(), 0);
        LinearLayout queryResultsContainer = (LinearLayout) rootView.findViewById(R.id.ll_panel_container);
        queryResultsContainer.setVisibility(View.VISIBLE);
        queryResultsContainer.setPadding(0, 0, 0, getNavbarHeight());
        rootView.findViewById(R.id.scroll_view_main_container).setVisibility(View.GONE);

//        rootView.findViewById(R.id.ll_panel_container).startAnimation(slideUp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bFirstOption:
            case R.id.imgFirstOption:
                mPrimaryOption = PrimaryOption.FOOD;
                setupFoodOptions();
                break;
            case R.id.bSecondOption:
            case R.id.imgSecondOption:
                mPrimaryOption = PrimaryOption.LISTEN;
                setupListenOptions();
                break;
            case R.id.bThirdOption:
            case R.id.imgThirdOption:
                mPrimaryOption = PrimaryOption.GOOGLE;
                setupGoogleOptions();
                break;
            case R.id.bFourthOption:
            case R.id.imgFourthOption:
                mPrimaryOption = PrimaryOption.FIND;
                setupFindOptions();
                break;
            case R.id.bFifthOption:
            case R.id.imgFifthOption:
                mPrimaryOption = PrimaryOption.YOUTUBE;
                setupYoutubeOptions();
                break;
            case R.id.bSixthOption:
            case R.id.imgSixthOption:
                mPrimaryOption = PrimaryOption.NOTE;
                setupNoteOptions();
                break;
        }
    }

    private void setupFindOptions() {
        setupButton(PrimaryOption.FIND);

    }

    private void setupYoutubeOptions() {
        setupButton(PrimaryOption.YOUTUBE);

    }

    private void setupNoteOptions() {
        setupButton(PrimaryOption.NOTE);

    }

    private void setupGoogleOptions() {
        setupButton(PrimaryOption.GOOGLE);

    }

    private void setupListenOptions() {
        setupButton(PrimaryOption.LISTEN);

    }

    private void setupFoodOptions() {
//        setupSecondaryOptions(getResources().getStringArray(R.array.find_me_options), R.string.find_options_title_label);
        setupButton(PrimaryOption.FOOD);
        etSearchView.setAdapter(new PlacesAutoCompleteAdapter(
                getActivity(), R.layout.simple_list_item));
        etSearchView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                etSearchView.setText((String) adapterView.getItemAtPosition(position));
            }
        });
    }

    private void setupButton(PrimaryOption option) {
        rootView.findViewById(R.id.ll_primary_container).setVisibility(View.GONE);
        mSwitcher.setText(option.getTitle());
        etSearchView.setHint(option.getMainInfo());
        etSearchView.setFocusableInTouchMode(true);
        etSearchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearchView, InputMethodManager.SHOW_IMPLICIT);
        LinearLayout searchContainer = (LinearLayout) rootView.findViewById(R.id.ll_search_container);
        searchContainer.setVisibility(View.VISIBLE);
        searchContainer.startAnimation(slideUp);
    }

    private int getNavbarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public SimpleCursorAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onYoutubeQueryFinished(List<YouTubeVideo> videos) {
        bringUpListviewAndDismissKeyboard();
        lvQueryResults.setAdapter(new YouTubeListAdapter(
                getActivity(), videos));
        lvQueryResults
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view,
                            int position, long id) {
                        String videoId = ((YouTubeVideo) lvQueryResults
                                .getItemAtPosition(position))
                                .getId();
                        Intent intent = YouTubeIntents
                                .createPlayVideoIntent(
                                        getActivity(), videoId);
                        startActivity(intent);
                    }
                });
    }

}
