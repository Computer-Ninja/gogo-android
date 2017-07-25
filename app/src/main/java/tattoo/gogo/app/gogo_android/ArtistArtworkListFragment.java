package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a list of Artist's Artworks.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArtistArtworkListFragmentInteractionListener}
 * interface.
 */
public class ArtistArtworkListFragment extends ArtFragment {

    private int mColumnCount = 1;
    private OnArtistArtworkListFragmentInteractionListener mListener;
    private ArrayList<ArtWork> mWorks = new ArrayList<>();
    private String mArtistName;
    private String mArtworkType;
    private List<ArtWork> mAllWorks = new ArrayList<>();
    private Bundle savedState = null;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    @BindView(R.id.iv_loading) ImageView ivLoading;
    @BindView(R.id.tv_nothing_here_yet) TextView tvNothingHere;

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

        getActivity().setTitle(GogoConst.GOGO_TATTOO + "/" + mArtistName + "/" + mArtworkType);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_tattoo_list;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        if (savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle(PARAM_BUNDLE);
        }
        if (savedState != null) {
            mWorks = savedState.getParcelableArrayList(PARAM_WORKS);
            setupRecyclerView();
        }
        savedState = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mWorks == null || mWorks.isEmpty()) {
            loadList();
        } else {
            ivLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState(); /* vstup defined here for sure */
    }

    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelableArrayList(PARAM_WORKS, mWorks);
        return state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        outState.putBundle(PARAM_BUNDLE, (savedState != null) ? savedState : saveState());
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

            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
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
                        tat.setType(mArtworkType);
                        mWorks.add(tat);
                        count++;
                    }
                }
                if (mWorks.isEmpty()) {
                    tvNothingHere.setVisibility(View.VISIBLE);
                } else {
                    setupRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<ArtWork>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
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
            case ARTWORK_TYPE_DREADLOCKS:
                GogoApi.getApi().dreadlocks(mArtistName).enqueue(callback);
                break;
        }
    }

    private void setupRecyclerView() {
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        //mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setAdapter(new ArtworkListRecyclerViewAdapter(ArtistArtworkListFragment.this, mWorks, mListener, mArtistName));
        final LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    // mWorks.add(mAllWorks.remove(0));
                    //mRecyclerView.invalidate();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArtistArtworkListFragment.OnArtistArtworkListFragmentInteractionListener) {
            mListener = (OnArtistArtworkListFragmentInteractionListener) context;
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
    public interface OnArtistArtworkListFragmentInteractionListener {

        void loadThumbnail(WeakReference<Fragment> fr, ArtworkListRecyclerViewAdapter.ViewHolder holder);

        void onListFragmentInteraction(WeakReference<Fragment> tWeakReference, String mArtistName, List<ArtWork> mValues, int position);
    }

}
