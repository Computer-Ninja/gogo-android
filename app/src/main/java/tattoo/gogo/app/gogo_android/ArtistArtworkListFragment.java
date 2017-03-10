package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Piercing;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a list of Artist's Artworks.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArtistArtworkFragmentInteractionListener}
 * interface.
 */
public class ArtistArtworkListFragment extends ArtFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ARTIST_NAME = "artist-name";
    private static final String ARG_ARTWORK_TYPE = "artwork-type";

    public static final String ARTWORK_TYPE_TATTOO = "tattoo";
    public static final String ARTWORK_TYPE_DESIGN = "design";
    public static final String ARTWORK_TYPE_HENNA = "henna";
    public static final String ARTWORK_TYPE_PIERCING = "piercing";

    private int mColumnCount = 1;
    private OnArtistArtworkFragmentInteractionListener mListener;
    private List<ArtWork> mWorks = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String mArtistName;
    private ImageView ivLoading;
    private TextView tvNothingHere;
    private String mArtworkType;
    private List<ArtWork> mAllWorks = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistArtworkListFragment() {
    }

    @SuppressWarnings("unused")
    public static ArtistArtworkListFragment newInstance(int columnCount, String artistName, String artType) {
        ArtistArtworkListFragment fragment = new ArtistArtworkListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_ARTIST_NAME, artistName);
        args.putString(ARG_ARTWORK_TYPE, artType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mArtistName = getArguments().getString(ARG_ARTIST_NAME, "gogo");
            mArtworkType = getArguments().getString(ARG_ARTWORK_TYPE, ARTWORK_TYPE_TATTOO);
        }

        getActivity().setTitle(GogoConst.GOGO_TATTOO + "/"  + mArtistName + "/" + mArtworkType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tattoo_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        ivLoading = (ImageView) view.findViewById(R.id.iv_loading);
        tvNothingHere = (TextView) view.findViewById(R.id.tv_nothing_here_yet);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        loadList();
    }

    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
        }

        public abstract void onHide();
        public abstract void onShow();

    }

    private void loadList() {
        mWorks.clear();
        mRecyclerView.invalidate();
        ivLoading.setVisibility(View.VISIBLE);
        Callback callback = new Callback<List<ArtWork>>() {

            @Override
            public void onResponse(Call<List<ArtWork>> call, Response<List<ArtWork>> response) {
                ivLoading.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    tvNothingHere.setVisibility(View.VISIBLE);
                    return;
                }
                mAllWorks.addAll(response.body());
                int count = 0;
                for (ArtWork tat : response.body()) {
                    if (!tat.getImageIpfs().isEmpty()) {
                        //if (count > 5) {
                        //    break;
                        //}
                        mWorks.add(tat);
                        count ++;
                    }
                }
                //mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setItemViewCacheSize(20);
                mRecyclerView.setDrawingCacheEnabled(true);
                //mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                mRecyclerView.setAdapter(new ArtworkRecyclerViewAdapter(ArtistArtworkListFragment.this, mWorks, mListener, mArtistName));
                final LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        int visibleItemCount = lm.getChildCount();
                        int totalItemCount = lm.getItemCount();
                        int pastVisibleItems = lm.findFirstVisibleItemPosition();
                        if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                            mWorks.add(mAllWorks.remove(0));
                            mRecyclerView.invalidate();
                        }
                    }
                });
                mRecyclerView.addOnScrollListener(new HidingScrollListener() {
                    @Override
                    public void onHide() {
                        hideViews();
                    }

                    @Override
                    public void onShow() {
                        showViews();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ArtWork>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);

                getFragmentManager().popBackStack();

            }
        };
        switch (mArtworkType) {
            case ARTWORK_TYPE_TATTOO:
                GogoApi.getApi().tattoo(mArtistName).enqueue(callback);
                break;
            case ARTWORK_TYPE_HENNA:
                GogoApi.getApi().henna(mArtistName).enqueue(callback);
                break;
            case ARTWORK_TYPE_PIERCING:
                GogoApi.getApi().piercing(mArtistName).enqueue(callback);
                break;
            case ARTWORK_TYPE_DESIGN:
                GogoApi.getApi().design(mArtistName).enqueue(callback);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!mArtworkType.equals(ARTWORK_TYPE_TATTOO))
        addMenuItem(menu, R.string.tattoo, ArtistArtworkListFragment.ARTWORK_TYPE_TATTOO);
        if (!mArtworkType.equals(ARTWORK_TYPE_DESIGN))
        addMenuItem(menu, R.string.design, ArtistArtworkListFragment.ARTWORK_TYPE_DESIGN);
        if (!mArtworkType.equals(ARTWORK_TYPE_HENNA))
        addMenuItem(menu, R.string.henna, ArtistArtworkListFragment.ARTWORK_TYPE_HENNA);
        if (!mArtworkType.equals(ARTWORK_TYPE_PIERCING))
        addMenuItem(menu, R.string.piercing, ArtistArtworkListFragment.ARTWORK_TYPE_PIERCING);

    }

    private void addMenuItem(Menu menu, @StringRes int textResId, final String artworkTypeTattoo) {

        menu.add(textResId).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String tag = mArtistName + "/" + artworkTypeTattoo;
                getFragmentManager().beginTransaction()
                        .hide(ArtistArtworkListFragment.this)
                        .add(R.id.fragment_container, ArtistArtworkListFragment.newInstance(1,
                                mArtistName, artworkTypeTattoo), tag)
                        .addToBackStack(tag)
                        .commit();
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener) {
            mListener = (ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArtistTattooFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnArtistArtworkFragmentInteractionListener {
        void onListFragmentInteraction(WeakReference<Fragment> fr, String artistName, ArtWork item);

        void loadThumbnail(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.ViewHolder holder);
    }

}
