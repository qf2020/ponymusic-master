package cjk.design.music.ScrollPicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cjk.design.music.ScrollPicker.IPickerViewOperation;
import cjk.design.music.ScrollPicker.IViewProvider;
import cjk.design.music.ScrollPicker.provider.DefaultItemViewProvider;

/* ckj */
public class ScrollPickerAdapter<T> extends RecyclerView.Adapter<ScrollPickerAdapter.ScrollPickerAdapterHolder> implements IPickerViewOperation {
    private List<T> mDataList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private OnScrollListener mOnScrollListener;
    private Boolean correct;
    private int mSelectedItemOffset;
    private int mVisibleItemNum = 3;
    private IViewProvider mViewProvider;
    private int mLineColor;
    private int maxItemH = 0;
    private int maxItemW = 0;
    private int width=0;
    private int height=0;

    private ScrollPickerAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
        correct = false;
    }

    @NonNull
    @Override
    public ScrollPickerAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mViewProvider == null){
            mViewProvider = new DefaultItemViewProvider();
        }
        return new ScrollPickerAdapterHolder(LayoutInflater.from(mContext).inflate(mViewProvider.resLayout(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollPickerAdapterHolder holder, int position) {

        mViewProvider.onBindView(holder.itemView, mDataList.get(position),mOnItemClickListener,position,width,height);
       // System.out.println(position+"位置"+mDataList.size());

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getSelectedItemOffset() {
        return mSelectedItemOffset;
    }

    @Override
    public int getVisibleItemNumber() {
        return mVisibleItemNum;
    }

    @Override
    public int getLineColor() {
        return mLineColor;
    }

    @Override
    public Boolean getCorrect(){
        return correct;
    }

    @Override
    public void updateView(View itemView, boolean isSelected) {
        if (this.correct){
            mViewProvider.updateView(itemView, isSelected);
        }
        adaptiveItemViewSize(itemView);
        if (isSelected && mOnScrollListener != null) {
            mOnScrollListener.onScrolled(itemView);
        }
    }

    private void adaptiveItemViewSize(View itemView) {
        int h = itemView.getHeight();
        if (h > maxItemH) {
            maxItemH = h;
        }

        int w = itemView.getWidth();
        if (w > maxItemW) {
            maxItemW = w;
        }

        itemView.setMinimumHeight(maxItemH);
        itemView.setMinimumWidth(maxItemW);
    }

    static class ScrollPickerAdapterHolder extends RecyclerView.ViewHolder {
        private View itemView;

        private ScrollPickerAdapterHolder(@NonNull View view) {
            super(view);
            itemView = view;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v,int position);
    }

    public interface OnScrollListener {
        void onScrolled(View currentItemView);
    }

    public static class ScrollPickerAdapterBuilder<T> {
        private ScrollPickerAdapter mAdapter;

        public ScrollPickerAdapterBuilder(Context context) {
            mAdapter = new ScrollPickerAdapter<T>(context);
        }

        public ScrollPickerAdapterBuilder<T> selectedItemOffset(int offset) {
            mAdapter.mSelectedItemOffset = offset;
            return this;
        }


        public ScrollPickerAdapterBuilder<T> setDataList(List<T> list) {
            mAdapter.mDataList.clear();
            mAdapter.mDataList.addAll(list);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setCorrect(Boolean correct1) {
            mAdapter.correct = correct1;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setSize(int width,int height) {
            mAdapter.width = width;
            mAdapter.height = height;
            return this;
        }
        public ScrollPickerAdapterBuilder<T> setOnClickListener(OnItemClickListener listener) {
            mAdapter.mOnItemClickListener = listener;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> visibleItemNumber(int num) {
            mAdapter.mVisibleItemNum = num;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setItemViewProvider(IViewProvider viewProvider) {
            mAdapter.mViewProvider = viewProvider;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setDivideLineColor(String colorString) {
            mAdapter.mLineColor = Color.parseColor(colorString);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setOnScrolledListener(OnScrollListener listener) {
            mAdapter.mOnScrollListener = listener;
            return this;
        }

        public ScrollPickerAdapter build() {
            adaptiveData(mAdapter.mDataList);
            mAdapter.notifyDataSetChanged();
            return mAdapter;
        }

        private void adaptiveData(List list) {
            int visibleItemNum = mAdapter.mVisibleItemNum;
            int selectedItemOffset = mAdapter.mSelectedItemOffset;
            for (int i = 0; i < mAdapter.mSelectedItemOffset; i++) {
                list.add(0, null);
            }

            for (int i = 0; i < visibleItemNum - selectedItemOffset - 1; i++) {
                list.add(null);
            }
        }
    }
}
