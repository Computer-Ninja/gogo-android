package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;

import static android.view.View.GONE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArtistArtworkFragmentInteractionListener}
 * interface.
 */
public class ArtistArtworkFragment extends ArtFragment {

    private static final String ARG_ARTIST_NAME = "artist-name";
    private static final String ARG_ARTWORK_TYPE = "artwork-type";
    static final String ARG_ARTWORK = "artwork";

    private String mArtistName;

    @BindView(R.id.tv_artwork_title) TextView tvTitle;
    @BindView(R.id.ll_artwork_images) LinearLayout llImages;
    @BindView(R.id.tv_artwork_made_date) TextView tvMadeDate;
    @BindView(R.id.tv_artwork_made_published) TextView tvPublishedDate;
    @BindView(R.id.iv_qr_gogotattoo) ImageView ivQRgogo;
    @BindView(R.id.iv_qr_gogogithub) ImageView ivQRgithub;
    @BindView(R.id.tv_gogo_link) TextView tvGogoLink;
    @BindView(R.id.tv_github_link) TextView tvGithubLink;
    @BindView(R.id.ll_artwork_nav) LinearLayout llNav;
    @BindView(R.id.tv_previous) TextView tvPrevious;
    @BindView(R.id.tv_next) TextView tvNext;


    private ArtWork mArtwork;
    private OnArtistArtworkFragmentInteractionListener mListener;
    private List<View> mViews = new ArrayList<>();

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
        try {
            String shortMadeDate = GogoConst.watermarkDateFormat.format(GogoConst.sdf.parse(mArtwork.getMadeDate()));
            tvMadeDate.setText(getString(R.string.tv_made_date, shortMadeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String shortPublishDate = GogoConst.watermarkDateFormat.format(GogoConst.sdf.parse(mArtwork.getDate()));
            tvPublishedDate.setText(getString(R.string.tv_publish_date, shortPublishDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //hideViews();
        updateQRcodes();

        String title = mArtistName.toLowerCase() + "/"
                + ((GogoActivity) getActivity()).mArtworkType.toLowerCase() + "/";
        ((GogoActivity) getActivity()).setGogoTitle(title);

        if (mArtwork.getPrevious() != null && !mArtwork.getPrevious().isEmpty()) {
            llNav.setVisibility(View.VISIBLE);
            tvPrevious.setVisibility(View.VISIBLE);
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
            mListener.sharePhotos(mViews, mArtwork.getLink());
            return false;
        });

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
        void sharePhotos(List<View> views, String text);
        void navigateTo(String artworkName);

        void showLoading();
        void hideLoading();

        void showContextMenu(ImageView iv, String hash, OnImageRefreshListener l);
    }

    public interface OnImageRefreshListener {
        void onImageRefresh(String hash, ImageView iv);
    }


    protected void updateQRcodes() {

        ivQRgogo.setImageResource(R.drawable.progress_animation);
        ivQRgithub.setImageResource(R.drawable.progress_animation);
        ivQRgogo.setVisibility(View.VISIBLE);
        ivQRgithub.setVisibility(View.VISIBLE);
        tvGogoLink.setVisibility(View.VISIBLE);
        tvGithubLink.setVisibility(View.VISIBLE);
        final String gogoTattooLink = makeLink(GogoConst.MAIN_URL);
        final String gogoGithubLink = makeLink(GogoConst.GITHUB_URL);
        tvGogoLink.setText(gogoTattooLink);
        tvGogoLink.setOnClickListener(view -> IntentUtils.opentUrl(getActivity(), gogoTattooLink));
        tvGithubLink.setText(gogoGithubLink);
        tvGithubLink.setOnClickListener(view -> IntentUtils.opentUrl(getActivity(), gogoGithubLink));

        new AsyncTask<Void, Void, Boolean>() {
             Bitmap qrGithubBitmap;
             Bitmap qrGogoBitmap;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    qrGogoBitmap = makeQRcode(gogoTattooLink);
                    qrGithubBitmap = makeQRcode(gogoGithubLink);
                } catch (OutOfMemoryError e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                loadQRviews();

                loadImages();

                if (qrGithubBitmap != null) {
                    mViews.add(ivQRgithub);
                } else if (qrGogoBitmap != null) {
                    mViews.add(ivQRgogo);
                }
            }

            private void loadQRviews() {
                if (qrGogoBitmap != null) {
                    ivQRgogo.setImageBitmap(qrGogoBitmap);
                    ivQRgogo.setOnClickListener(v -> {
                        mListener.sharePhoto(ivQRgogo, mArtwork.getLink());
                    });
                } else {
                    ivQRgogo.setVisibility(GONE);
                }
                if (qrGithubBitmap != null) {
                    ivQRgithub.setImageBitmap(qrGithubBitmap);
                    ivQRgithub.setOnClickListener(v -> mListener.sharePhoto(ivQRgithub, mArtwork.getLink()));
                } else {
                    ivQRgithub.setVisibility(GONE);
                }
            }
        }.execute();
    }

    private void loadImages() {
        for (String hash : mArtwork.getImagesIpfs()) {
            ImageView iv = addImage(hash);
            if (iv != null) {
                mViews.add(iv);
            }
        }
        ImageView iv = addImage(mArtwork.getImageIpfs());
        if (iv != null) {
            mViews.add(iv);
        }
        mListener.hideLoading();
    }

    private ImageView addImage(final String imageIpfs) {
        if (getContext() == null) {
            return null;
        }
        final ImageView iv = new ImageView(getContext());
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setPadding(8, 8, 8, 8);
        llImages.addView(iv);

        loadImage(imageIpfs, iv);
        return iv;

    }

    private void loadImage(final String hash, final ImageView iv) {
        Glide.with(this)
                .load(GogoConst.IPFS_GATEWAY_URL + hash)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                .into(iv);
        iv.setOnLongClickListener(view -> {
            mListener.showContextMenu(iv, hash, this::loadImage);
            return true;
        });
    }

    protected String makeLink(String mainUrl) {
        return mainUrl + mArtistName.toLowerCase() + "/" + ((GogoActivity) getActivity()).mArtworkType.toLowerCase() + "/" + mArtwork.getLink();
    }

}
