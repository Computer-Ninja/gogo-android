package tattoo.gogo.app.gogo_android.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.HashSet;
import java.util.Set;

import tattoo.gogo.app.gogo_android.R;

public final class GlideImageGetter implements Drawable.Callback {

    private final Context mContext;

    private final TextView mTextView;

    private static final Set<ImageGetterViewTarget> mTargets = new HashSet<>();

    public GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;

        clear(); // Cancel all previous request
        mTextView.setTag(R.id.iv_thumbnail, this);
    }

    public static GlideImageGetter get(View view) {
        return (GlideImageGetter)view.getTag(R.id.iv_thumbnail);
    }

    public void clear() {
        GlideImageGetter prev = get(mTextView);
        if (prev == null) return;

        for (ImageGetterViewTarget target : prev.mTargets) {
            System.out.println("Cleared!");
            Glide.clear(target);
        }
    }

    public Drawable getDrawable(String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();

        System.out.println("Downloading from: " + url);
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageGetterViewTarget(mTextView, urlDrawable));

        return urlDrawable;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private static class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;
        private Request request;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);
            mTargets.add(this); // Add ViewTarget into Set
            this.mDrawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
             // Resize partial_images - Scale image proportionally to fit TextView width
             float width;
             float height;
             if (resource.getIntrinsicWidth() >= getView().getWidth()) {
                 float downScale = (float) resource.getIntrinsicWidth() / getView().getWidth();
                 width = (float) resource.getIntrinsicWidth() / (float) downScale;
                 height = (float) resource.getIntrinsicHeight() / (float) downScale;
             } else {
                 float multiplier = (float) getView().getWidth() / resource.getIntrinsicWidth();
                 width = (float) resource.getIntrinsicWidth() * (float) multiplier;
                 height = (float) resource.getIntrinsicHeight() * (float) multiplier;
             }
            Rect rect = new Rect(0, 0, Math.round(width), Math.round(height));

            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource.isAnimated()) {
                // set callback to drawable in order to
                // signal its container to be redrawn
                // to show the animated GIF

                mDrawable.setCallback(get(getView()));    
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }
}