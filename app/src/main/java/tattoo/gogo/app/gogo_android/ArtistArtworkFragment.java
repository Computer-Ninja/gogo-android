package tattoo.gogo.app.gogo_android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;
import tattoo.gogo.app.gogo_android.utils.NavUtil;
import tattoo.gogo.app.gogo_android.view.SimpleDividerItemDecoration;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArtistArtworkFragmentInteractionListener}
 * interface.
 */
public class ArtistArtworkFragment extends ArtFragment  {

    private static final String TAG = "ArtistArtworkFragment";

    private static final String ARG_ARTIST_NAME = "artist-name";
    private static final String ARG_ARTWORK_TYPE = "artwork-type";
    static final String ARG_ARTWORK = "artwork";


    @BindView(R.id.tv_artwork_title)
    TextView tvTitle;
    @BindView(R.id.ll_artwork_nav)
    LinearLayout llNav;
    @BindView(R.id.list)
    RecyclerView rvList;
    @BindView(R.id.tv_previous)
    TextView tvPrevious;
    @BindView(R.id.tv_next)
    TextView tvNext;


    private String mArtistName;
    private ArtWork mArtwork;
    private String mArtworkType;
    private OnArtistArtworkFragmentInteractionListener mListener;
    private ArtworkRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistArtworkFragment() {
    }

    public static ArtistArtworkFragment newInstance(String artistName, ArtWork artWork, String artWorkType) {
        ArtistArtworkFragment fragment = new ArtistArtworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_NAME, artistName);
        args.putParcelable(ARG_ARTWORK, artWork);
        args.putString(ARG_ARTWORK_TYPE, artWorkType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mArtistName = getArguments().getString(ARG_ARTIST_NAME, "gogo");
            mArtwork = getArguments().getParcelable(ARG_ARTWORK);
            mArtworkType = getArguments().getString(ARG_ARTWORK_TYPE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_artwork;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvTitle.setText(mArtwork.getTitle());


        //hideViews();
        mArtwork.setLink(mArtistName.toLowerCase() + "/" + ((GogoActivity) getActivity()).mArtworkType.toLowerCase() + "/" + mArtwork.getLink());
        mAdapter = new ArtworkRecyclerViewAdapter(ArtistArtworkFragment.this, mArtwork, mListener);

        rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvList.setHasFixedSize(true);
        rvList.setAdapter(mAdapter);
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.onScrolled(recyclerView);
                }
            }

        });

        //updateFooter();

        String title = mArtistName.toLowerCase() + "/"
                + ((GogoActivity) getActivity()).mArtworkType.toLowerCase() + "/";
        ((GogoActivity) getActivity()).setGogoTitle(title);

        if (mArtwork.getPrevious() != null && !mArtwork.getPrevious().isEmpty()) {
            llNav.setVisibility(VISIBLE);
            tvPrevious.setVisibility(VISIBLE);
            tvPrevious.setOnClickListener(v -> {
                mListener.navigateTo(mArtwork.getPrevious());
            });
        } else {
            llNav.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        menu.add(R.string.share_all).setOnMenuItemClickListener(menuItem -> {
//            for (int i = 0; i < mArtwork.getImagesIpfs().size(); i++) {
//                if (rvList.getLayoutManager().findViewByPosition(i) != null)
//                    mViews.add(rvList.getLayoutManager().findViewByPosition(i).findViewById(R.id.iv_thumbnail));
//            }

            NavUtil.shareArtwork(getContext(), mArtistName, mArtwork, mArtworkType);
            return false;
        });

        menu.add(R.string.copy_link).setOnMenuItemClickListener(menuItem -> {
            copyLink(makeLink(GogoConst.MAIN_URL));
            return false;
        });

        menu.add(R.string.copy_link_github).setOnMenuItemClickListener(menuItem -> {
            copyLink(makeLink(GogoConst.GITHUB_URL));
            return false;
        });

        if (!mArtwork.getVideosIpfs().isEmpty()) {
            menu.add(R.string.watch_process_video).setOnMenuItemClickListener(menuItem -> {
                copyLink(GogoConst.IPFS_GATEWAY_URL + mArtwork.getVideosIpfs().get(0));
                return false;
            });

        }
        if (mArtwork.getBlockchain() != null) {
            if (mArtwork.getBlockchain().getSteem() != null)
                menu.add(R.string.copy_link_steem).setOnMenuItemClickListener(menuItem -> {
                    copyLink(GogoConst.STEEMIT_URL + mArtwork.getBlockchain().getSteem());
                    return false;
                });

            if (mArtwork.getBlockchain().getGolos() != null)
                menu.add(R.string.copy_link_golos).setOnMenuItemClickListener(menuItem -> {
                    copyLink(GogoConst.GOLOS_URL + mArtwork.getBlockchain().getGolos());
                    return false;
                });
        }
    }

    private void copyLink(String link) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("app.gogo.tattoo", link);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(mToolbar, link, Snackbar.LENGTH_LONG)
                .setAction(R.string.open_url, v -> IntentUtils.opentUrl(getContext(), link))
                .show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener) {
            mListener = (OnArtistArtworkFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewWorkFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        void savePhoto(String hash);

        void shareOriginalPhoto(String hash);

        void sharePhoto(View view, String text);

        void navigateTo(String artworkName);

        void showLoading();

        void hideLoading();

        void showContextMenu(ImageView iv, String hash, OnImageRefreshListener l);

        void loadThumbnail(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.ImageViewHolder holder);

        void onListFragmentInteraction(WeakReference<Fragment> tWeakReference, String mArtistName, List<ArtWork> mValues, int position);
    }

    public interface OnImageRefreshListener {
        void onImageRefresh(String hash, ImageView iv);
    }

    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;

    private VideoView addVideo(final String hash) {
        if (getContext() == null) {
            return null;
        }
        final VideoView vv = new VideoView(getContext());
        //iv.setAdjustViewBounds(true);
        //iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2500);
        ll.setMargins(8, 8, 8, 8);
        vv.setLayoutParams(ll);
        vv.setPadding(8, 8, 8, 8);
        //llVideos.addView(vv);

        vv.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    isOnClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isOnClick) {
                        copyLink(GogoConst.IPFS_GATEWAY_URL + hash);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                        isOnClick = false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        });
        vv.setOnClickListener(v -> IntentUtils.opentUrl(getContext(), GogoConst.IPFS_GATEWAY_URL + hash));
        vv.setOnPreparedListener(mp -> mp.setLooping(true));
        vv.setVideoURI(Uri.parse(GogoConst.IPFS_GATEWAY_URL + hash));
        vv.start();
//        vv.setImageResource(R.drawable.doge);

        return vv;

    }

    protected String makeLink(String mainUrl) {
        return mainUrl + mArtistName.toLowerCase() + "/" + ((GogoActivity) getActivity()).mArtworkType.toLowerCase() + "/" + mArtwork.getLink();
    }

}
