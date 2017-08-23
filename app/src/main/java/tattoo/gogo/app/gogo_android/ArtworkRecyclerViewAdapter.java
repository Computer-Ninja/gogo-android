package tattoo.gogo.app.gogo_android;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.glxn.qrgen.android.QRCode;

import java.lang.ref.WeakReference;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;
import tattoo.gogo.app.gogo_android.utils.UIUtils;
import tattoo.gogo.app.gogo_android.view.GogoVideoView;

import static android.view.View.GONE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ArtWork} and makes a call to the
 * specified {@link ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener}.
 */
public class ArtworkRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_FOOTER = 3;

    private final ArtWork mArtwork;
    private final ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener mListener;
    private final Fragment mFragment;

    public ArtworkRecyclerViewAdapter(Fragment fr, ArtWork artwork,
                                      ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener listener) {
        mArtwork = artwork;
        mListener = listener;
        mFragment = fr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.artwork_footer, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.art_item_video, parent, false));
        } else {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.art_item_image, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position <= mArtwork.getImagesIpfs().size()) {
            return TYPE_IMAGE;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_VIDEO;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder hldr, int position) {
        int imageCount = mArtwork.getImagesIpfs().size();
        if (hldr instanceof ImageViewHolder) {
            ImageViewHolder holder = (ImageViewHolder) hldr;
            if (position == mArtwork.getImagesIpfs().size()) {
                holder.hash = mArtwork.getImageIpfs();
            } else {
                holder.hash = mArtwork.getImagesIpfs().get(position);
            }
            mListener.loadThumbnail(new WeakReference<>(mFragment), holder);

        } else if (hldr instanceof VideoViewHolder){
            VideoViewHolder holder = (VideoViewHolder) hldr;
            holder.hash = GogoConst.IPFS_GATEWAY_URL + mArtwork.getVideosIpfs().get(position - imageCount - 1);

            //mListener.loadVideo(new WeakReference<>(mFragment), holder);

        } else {
            updateFooter((FooterViewHolder) hldr);

        }
//        holder.mView.setOnClickListener(v -> {
//            if (null != mListener) {
//                // Notify the active callbacks interface (the activity, if the
//                // fragment is attached to one) that an item has been selected.
//               // mListener.o(new WeakReference<>(mFragment), mArtistName, mValues, position);
//            }
//        });
    }


    protected void updateFooter(FooterViewHolder holder) {

        holder.ivQRgogo.setImageResource(R.drawable.progress_animation);
        holder.ivQRgithub.setImageResource(R.drawable.progress_animation);
        holder.ivQRgogo.setVisibility(View.VISIBLE);
        holder.ivQRgithub.setVisibility(View.VISIBLE);
        holder.tvGogoLink.setVisibility(View.VISIBLE);
        holder.tvGithubLink.setVisibility(View.VISIBLE);
        final String gogoTattooLink = GogoConst.MAIN_URL + mArtwork.getLink();
        final String gogoGithubLink = GogoConst.GITHUB_URL + mArtwork.getLink();
        holder.tvGogoLink.setText(gogoTattooLink);
        holder.tvGogoLink.setOnClickListener(view -> IntentUtils.opentUrl(mFragment.getContext(), gogoTattooLink));
        holder.tvGithubLink.setText(gogoGithubLink);
        holder.tvGithubLink.setOnClickListener(view -> IntentUtils.opentUrl(mFragment.getContext(), gogoGithubLink));
        try {
            String shortMadeDate = GogoConst.watermarkDateFormat.format(GogoConst.sdf.parse(mArtwork.getMadeDate()));
            holder.tvMadeDate.setText(mFragment.getString(R.string.tv_made_date, shortMadeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String shortPublishDate = GogoConst.watermarkDateFormat.format(GogoConst.sdf.parse(mArtwork.getDate()));
            holder.tvPublishedDate.setText(mFragment.getString(R.string.tv_publish_date, shortPublishDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Boolean>() {
            Bitmap qrGithubBitmap;
            Bitmap qrGogoBitmap;


            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    qrGogoBitmap = UIUtils.makeQRcode(gogoTattooLink);
                    qrGithubBitmap = UIUtils.makeQRcode(gogoGithubLink);
                } catch (OutOfMemoryError e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                loadQRviews();

                //mListener.hideLoading();

//                if (qrGithubBitmap != null) {
//                    mViews.add(ivQRgithub);
//                } else if (qrGogoBitmap != null) {
//                    mViews.add(ivQRgogo);
//                }
            }

            private void loadQRviews() {
                if (qrGogoBitmap != null) {
                    holder.ivQRgogo.setImageBitmap(qrGogoBitmap);
                    holder.ivQRgogo.setOnClickListener(v -> {
                        mListener.sharePhoto(holder.ivQRgogo, mArtwork.getLink());
                    });
                } else {
                    holder.ivQRgogo.setVisibility(GONE);
                }
                if (qrGithubBitmap != null) {
                    holder.ivQRgithub.setImageBitmap(qrGithubBitmap);
                    holder.ivQRgithub.setOnClickListener(v -> mListener.sharePhoto(holder.ivQRgithub, mArtwork.getLink()));
                } else {
                    holder.ivQRgithub.setVisibility(GONE);
                }
            }
        }.execute();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ImageViewHolder) {
            Glide.clear(((ImageViewHolder) holder).ivThumbnail);
        } else if (holder instanceof VideoViewHolder) {
            if (holder == currentVideoViewHolder) {
                currentVideoViewHolder = null;
                ((VideoViewHolder) holder).stopVideo();
            }
            ((VideoViewHolder) holder).videoView.stopPlayback();
        }
        super.onViewRecycled(holder);
    }

    public void onScrolled(RecyclerView recyclerView) {
        if (currentVideoViewHolder != null) {
            currentVideoViewHolder.onScrolled(recyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return mArtwork.getImagesIpfs().size() + mArtwork.getVideosIpfs().size() + 2;
        //    return mArtwork.getVideosIpfs().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final GogoVideoView mVideoView;
        final ImageView ivThumbnail;

        String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mVideoView = (GogoVideoView) view.findViewById(R.id.gvv_video);
            ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
        }

    }

    private VideoViewHolder currentVideoViewHolder;

    class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_thumbnail)
        ImageView ivThumbnail;

        String hash;

        public ImageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_play_btn)
        ImageView videoPlayImageButton;
        @BindView(R.id.pb_loading)
        ProgressBar imageLoaderProgressBar;
        @BindView(R.id.gvv_video)
        GogoVideoView videoView;
        @BindView(R.id.iv_thumbnail)
        ImageView ivThumbnail;
        View mView;

        String hash;

        public String getHash() {
            return hash;
        }

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
            videoView.setOnPreparedListener(mp -> {
                Log.v("Video", "onPrepared" + videoView.getVideoPath());
                int width = mp.getVideoWidth();
                int height = mp.getVideoHeight();
                videoView.setIsPrepared(true);
                UIUtils.resizeView(videoView, UIUtils.getScreenWidth(mFragment.getActivity()), UIUtils.getScreenWidth(mFragment.getActivity()) * height / width);
                if (currentVideoViewHolder == VideoViewHolder.this) {
                    ivThumbnail.setVisibility(View.GONE);
                    imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.seekTo(0);
                    videoView.start();
                }
            });
            videoView.setOnFocusChangeListener((v, hasFocus) -> {
                Log.v("Video", "onFocusChange" + hasFocus);
                if (!hasFocus && currentVideoViewHolder == VideoViewHolder.this) {
                    stopVideo();
                }

            });
            videoView.setOnInfoListener((mp, what, extra) -> {
                Log.v("Video", "onInfo" + what + " " + extra);

                return false;
            });
            videoView.setOnCompletionListener(mp -> {
                Log.v("Video", "onCompletion");

                ivThumbnail.setVisibility(View.VISIBLE);
                videoPlayImageButton.setVisibility(View.VISIBLE);

                if (videoView.getVisibility() == View.VISIBLE)
                    videoView.setVisibility(View.INVISIBLE);


                imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                currentVideoViewHolder = null;
            });
            videoPlayImageButton.setOnClickListener(v -> {
                if (currentVideoViewHolder != null && currentVideoViewHolder != VideoViewHolder.this) {
                    currentVideoViewHolder.videoView.pause();
                    currentVideoViewHolder.ivThumbnail.setVisibility(View.INVISIBLE);
                    currentVideoViewHolder.videoPlayImageButton.setVisibility(View.VISIBLE);
                    currentVideoViewHolder.imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                    if (currentVideoViewHolder.videoView.getVisibility() == View.VISIBLE)
                        currentVideoViewHolder.videoView.setVisibility(View.INVISIBLE);


                    currentVideoViewHolder = null;
                }

                currentVideoViewHolder = VideoViewHolder.this;

                videoPlayImageButton.setVisibility(View.INVISIBLE);
                imageLoaderProgressBar.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.VISIBLE);
                ivThumbnail.setVisibility(View.INVISIBLE);
                if (!getHash().equals(videoView.getVideoPath())) {
                    videoView.setIsPrepared(false);
                    videoView.setVideoPath(getHash());
                    videoView.requestFocus();
                } else {
                    if (videoView.isPrepared()) {
                        imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                    } else {
                        imageLoaderProgressBar.setVisibility(View.VISIBLE);
                    }
                    videoView.requestFocus();
                    videoView.seekTo(0);
                    videoView.start();
                }
            });

        }

        public void stopVideo() {
            Log.v("Video", "stopVideo");

            //imageView is within the visible window
            videoView.pause();
            if (videoView.getVisibility() == View.VISIBLE) {
                videoView.setVisibility(View.INVISIBLE);
            }
            ivThumbnail.setVisibility(View.VISIBLE);
            videoPlayImageButton.setVisibility(View.VISIBLE);
            imageLoaderProgressBar.setVisibility(View.INVISIBLE);
            currentVideoViewHolder = null;
        }

        public void onScrolled(RecyclerView recyclerView) {
            if (isViewNotVisible(videoPlayImageButton, recyclerView) || isViewNotVisible(imageLoaderProgressBar, recyclerView)) {
                //imageView is within the visible window
                stopVideo();
            }
        }

        public boolean isViewNotVisible(View view, RecyclerView recyclerView) {
            Rect scrollBounds = new Rect();
            recyclerView.getHitRect(scrollBounds);
            return view.getVisibility() == View.VISIBLE && !view.getLocalVisibleRect(scrollBounds);
        }
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {

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


        public FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
