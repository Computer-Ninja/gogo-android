package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArtistArtworkFragmentInteractionListener}
 * interface.
 */
public class ArtistArtworkFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ARTIST_NAME = "artist-name";
    private static final String ARG_ARTWORK_TYPE = "artwork-type";

    public static final int ARTWORK_TYPE_TATTOO = 0;
    public static final int ARTWORK_TYPE_DESIGN = 1;
    public static final int ARTWORK_TYPE_HENNA = 2;
    public static final int ARTWORK_TYPE_PIERCING = 3;

    private int mColumnCount = 1;
    private OnArtistArtworkFragmentInteractionListener mListener;
    private List<ArtWork> mWorks = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String mArtistMame;
    private ImageView ivLoading;
    private TextView tvNothingHere;
    private int mArtworkType;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistArtworkFragment() {
    }

    @SuppressWarnings("unused")
    public static ArtistArtworkFragment newInstance(int columnCount, String artistName, int artType) {
        ArtistArtworkFragment fragment = new ArtistArtworkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_ARTIST_NAME, artistName);
        args.putInt(ARG_ARTWORK_TYPE, artType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mArtistMame = getArguments().getString(ARG_ARTIST_NAME, "gogo");
            mArtworkType = getArguments().getInt(ARG_ARTWORK_TYPE, ARTWORK_TYPE_TATTOO);
        }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                for (ArtWork tat : response.body()) {
                    if (!tat.getImageIpfs().isEmpty()) {
                        mWorks.add(tat);
                    }
                }
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setItemViewCacheSize(20);
                mRecyclerView.setDrawingCacheEnabled(true);
                mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                mRecyclerView.setAdapter(new ArtworkRecyclerViewAdapter(mWorks, mListener));
            }

            @Override
            public void onFailure(Call<List<ArtWork>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);

            }
        };
        switch (mArtworkType) {
            case ARTWORK_TYPE_TATTOO:
                GogoApi.getApi().tattoo(mArtistMame).enqueue(callback);
                break;
            case ARTWORK_TYPE_HENNA:
                GogoApi.getApi().henna(mArtistMame).enqueue(callback);
                break;
            case ARTWORK_TYPE_PIERCING:
                GogoApi.getApi().piercing(mArtistMame).enqueue(callback);
                break;
            case ARTWORK_TYPE_DESIGN:
                GogoApi.getApi().design(mArtistMame).enqueue(callback);
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener) {
            mListener = (ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener) context;
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
        void onListFragmentInteraction(ArtWork item);

        void loadThumbnail(ImageView iv, ArtWork mItem);
    }
}
