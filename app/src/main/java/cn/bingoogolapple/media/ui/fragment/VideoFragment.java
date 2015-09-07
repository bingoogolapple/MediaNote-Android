package cn.bingoogolapple.media.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.engine.MediaScanner;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.ui.widget.Divider;
import cn.bingoogolapple.media.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午5:18
 * 描述:
 */
public class VideoFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mDataRv;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video);
        mDataRv = getViewById(R.id.rv_video_data);
    }

    @Override
    protected void setListener() {
        mMovieAdapter = new MovieAdapter(mDataRv);
        mMovieAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.addItemDecoration(new Divider(mActivity));
        mDataRv.setAdapter(mMovieAdapter);

        reloadData();
    }

    private void reloadData() {
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                final List<MediaFile> mediaFiles = MediaScanner.scanVideo();
                ThreadUtil.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mMovieAdapter.setDatas(mediaFiles);
                    }
                });
            }
        });
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {

    }

    private final class MovieAdapter extends BGARecyclerViewAdapter<MediaFile> {

        public MovieAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_movie);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, MediaFile model) {
            helper.setText(R.id.tv_item_movie_name, model.name);
        }
    }
}