package tattoo.gogo.app.gogo_android.view;

import android.content.Context;
import android.util.AttributeSet;

import com.sprylab.android.widget.TextureVideoView;

public class GogoVideoView extends TextureVideoView {
    String videoPath;
    boolean isPrepared;

    public GogoVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GogoVideoView(Context context) {
        super(context);
    }

    public GogoVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setVideoPath(String path) {
        super.setVideoPath(path);
        videoPath = path;
    }


    public void setIsPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public String getVideoPath() {
        return videoPath;
    }
}