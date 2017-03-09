package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;

import static android.content.ContentValues.TAG;
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
    private static final String ARG_ARTWORK = "artwork";

    private List<ArtWork> mWorks = new ArrayList<>();
    private String mArtistName;
    private String mArtworkType;

    @BindView(R.id.tv_artwork_title)
    TextView tvTitle;
    @BindView(R.id.ll_artwork_images)
    LinearLayout llImages;
    @BindView(R.id.tv_artwork_made_date)
    TextView tvMadeDate;
    @BindView(R.id.tv_artwork_made_published)
    TextView tvPublishedDate;
    @BindView(R.id.iv_qr_gogotattoo)
    ImageView ivQRgogo;
    @BindView(R.id.iv_qr_gogogithub)
    ImageView ivQRgithub;
    @BindView(R.id.tv_gogo_link)
    TextView tvGogoLink;
    @BindView(R.id.tv_github_link)
    TextView tvGithubLink;


    private ArtWork mArtwork;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistArtworkFragment() {
    }

    public static ArtistArtworkFragment newInstance(String artistName, ArtWork artWork) {
        ArtistArtworkFragment fragment = new ArtistArtworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_NAME, artistName);
        args.putParcelable(ARG_ARTWORK, artWork);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mArtistName = getArguments().getString(ARG_ARTIST_NAME, "gogo");
            mArtworkType = getArguments().getString(ARG_ARTWORK_TYPE, ArtistArtworkListFragment.ARTWORK_TYPE_TATTOO);
            mArtwork = getArguments().getParcelable(ARG_ARTWORK);
        }
//
//        String title = mArtwork.getLink().replace("http://gogo.tattoo", "");
//        ((MainActivity) getActivity()).setActionBarTitle(title);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.bind(this, view);
        return view;
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


        updateQRcodes();


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        void loadImage(Fragment fr, ImageView iv, String ipfsHash);
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
        tvGithubLink.setText(gogoGithubLink);

        new AsyncTask<Void, Void, Boolean>() {
            public Bitmap qrGithubBitmap;
            public Bitmap qrGogoBitmap;

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
                if (qrGogoBitmap != null) {
                    ivQRgogo.setImageBitmap(qrGogoBitmap);
                } else {
                    ivQRgogo.setVisibility(GONE);
                }
                if (qrGithubBitmap != null) {
                    ivQRgithub.setImageBitmap(qrGithubBitmap);
                } else {
                    ivQRgithub.setVisibility(GONE);
                }

                loadImages();
            }
        }.execute();

//        ivQRgithub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.setType("images/png");
//
//                Uri uri = FileProvider.getUriForFile(getContext(), "tattoo.gogo.app.gogo_android", makeQRcodeFile(gogoGithubLink));
//                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
//            }
//        });

    }

    private void loadImages() {
        for (String hash : mArtwork.getImagesIpfs()) {
            addImage(hash);
        }
        addImage(mArtwork.getImageIpfs());
    }

    private void addImage(final String imageIpfs) {
        if (getContext() == null) {
            return;
        }
        final ImageView iv = new ImageView(getContext());
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setPadding(0, 20, 0, 20);
        llImages.addView(iv);

        Glide.with(this)
                .load(GogoConst.IPFS_GATEWAY_URL + imageIpfs)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                .into(iv);
//
//        iv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                Glide.with(ArtistArtworkFragment.this)
//                        .load(GogoConst.IPFS_GATEWAY_URL + imageIpfs)
//                        .placeholder(R.drawable.progress_animation)
//                        .error(R.drawable.doge)
//                        .into(iv);
//                return true;
//            }
//        });
    }

    protected Bitmap makeQRcode(String link) throws OutOfMemoryError {
        return QRCode.from(link).withSize(1024, 1024).bitmap();
    }

    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mArtwork.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mArtistName.toLowerCase() + "/" + mArtwork.getShortName().toLowerCase() + "/" + tattooTitleLinkified;
    }
}
