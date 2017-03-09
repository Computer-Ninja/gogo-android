package tattoo.gogo.app.gogo_android;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tattoo.gogo.app.gogo_android.model.ArtWork;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ArtWork} and makes a call to the
 * specified {@link ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ArtworkRecyclerViewAdapter extends RecyclerView.Adapter<ArtworkRecyclerViewAdapter.ViewHolder> {

    private final List<ArtWork> mValues;
    private final ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener mListener;
    private final Fragment mFragment;
    private final String mArtistName;

    public ArtworkRecyclerViewAdapter(Fragment fr, List<ArtWork> items,
                                      ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener listener,
                                      String artistName) {
        mValues = items;
        mListener = listener;
        mFragment = fr;
        mArtistName = artistName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tattoo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.tvHeaderMonth.setText(mValues.get(position).());
        holder.mContentView.setText(mValues.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mFragment, mArtistName, holder.mItem);
                }
            }
        });
        mListener.loadThumbnail(mFragment, holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Glide.clear(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvHeaderMonth;
        public final TextView mContentView;
        public ImageView ivThumbnail;

        public ArtWork mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvHeaderMonth = (TextView) view.findViewById(R.id.tv_header_month);
            mContentView = (TextView) view.findViewById(R.id.tv_artwork_title);
            ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
