package tattoo.gogo.app.gogo_android;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.UIUtils;
import tattoo.gogo.app.gogo_android.view.GogoVideoView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ArtWork} and makes a call to the
 * specified {@link ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ArtworkRecyclerViewAdapter extends RecyclerView.Adapter<ArtworkRecyclerViewAdapter.VideoViewHolder> {

    private final ArtWork mArtwork;
    private final ArtworkRecyclerViewAdapter.OnArtistArtworkFragmentInteractionListener mListener;
    private final Fragment mFragment;

    public ArtworkRecyclerViewAdapter(Fragment fr, ArtWork artwork,
                                      ArtworkRecyclerViewAdapter.OnArtistArtworkFragmentInteractionListener listener) {
        mArtwork = artwork;
        mListener = listener;
        mFragment = fr;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        int imageCount = mArtwork.getImagesIpfs().size();
        if (position <= imageCount) {
            if (position == mArtwork.getImagesIpfs().size()) {
                holder.hash = mArtwork.getImageIpfs();
            } else {
                holder.hash = mArtwork.getImagesIpfs().get(position);
            }

            //holder.imageLoaderProgressBar.setVisibility(View.GONE);
            holder.videoPlayImageButton.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            mListener.loadThumbnail(new WeakReference<>(mFragment), holder);

        } else {
            holder.hash = GogoConst.IPFS_GATEWAY_URL + mArtwork.getVideosIpfs().get(position - imageCount - 1);

            //holder.imageLoaderProgressBar.setVisibility(View.VISIBLE);
            holder.videoPlayImageButton.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            //holder.ivThumbnail.setVisibility(View.GONE);

            //mListener.loadVideo(new WeakReference<>(mFragment), holder);

        }
//        holder.mView.setOnClickListener(v -> {
//            if (null != mListener) {
//                // Notify the active callbacks interface (the activity, if the
//                // fragment is attached to one) that an item has been selected.
//               // mListener.o(new WeakReference<>(mFragment), mArtistName, mValues, position);
//            }
//        });
    }

    @Override
    public void onViewDetachedFromWindow(VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //
    }

    @Override
    public void onViewRecycled(VideoViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivThumbnail);

        if (holder == currentVideoViewHolder) {
            currentVideoViewHolder = null;
            holder.stopVideo();
        }
        holder.videoView.stopPlayback();
        super.onViewRecycled(holder);
    }

    public void onScrolled(RecyclerView recyclerView) {
        if (currentVideoViewHolder != null) {
            currentVideoViewHolder.onScrolled(recyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return mArtwork.getImagesIpfs().size() + mArtwork.getVideosIpfs().size() + 1;
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

    VideoViewHolder currentVideoViewHolder;

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

    interface OnArtistArtworkFragmentInteractionListener {
        void loadThumbnail(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.VideoViewHolder holder);

        void loadVideo(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.VideoViewHolder holder);
    }
}
