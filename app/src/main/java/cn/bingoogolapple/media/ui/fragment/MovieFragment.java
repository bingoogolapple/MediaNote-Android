package cn.bingoogolapple.media.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.ui.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午5:18
 * 描述:
 */
public class MovieFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mDataRv;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_movie);
        mDataRv = getViewById(R.id.rv_movie_data);
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

        ArrayList<String> datas = new ArrayList<>();
        datas.add("视频1");
        datas.add("视频2");
        datas.add("视频3");
        mMovieAdapter.setDatas(datas);
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {

    }

    private final class MovieAdapter extends BGARecyclerViewAdapter<String> {

        public MovieAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_movie);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            helper.setText(R.id.tv_item_movie_name, model);
        }
    }
}