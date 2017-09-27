package com.rair.flyball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rair.flyball.model.GameScore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "Rair";
    //榜单数据
    private List<GameScore> scoreList;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //注册EventBus
        EventBus.getDefault().register(this);
        //初始化试图
        XRecyclerView xrv = (XRecyclerView) findViewById(R.id.xrv);
        //初始化分数集合
        scoreList = new ArrayList<>();
        //不刷新
        xrv.setLoadingMoreEnabled(false);
        xrv.setPullRefreshEnabled(false);
        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        xrv.setLayoutManager(layoutManager);
        //适配器
        adapter = new MyAdapter();
        xrv.setAdapter(adapter);
        //查询
        listScore();
    }

    /**
     * 查询所有分数
     */
    private void listScore() {
        BmobQuery<GameScore> query = new BmobQuery<>();
        query.order("-score");
        query.findObjects(new FindListener<GameScore>() {
            @Override
            public void done(List<GameScore> list, BmobException e) {
                if (e == null) {
                    EventBus.getDefault().post(list);
                } else {
                    Toast.makeText(ListActivity.this, "获取失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe
    public void getList(List<GameScore> list) {
        scoreList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends XRecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.layout_item_view, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tvOrder.setText((position + 1) + "");
            holder.tvName.setText(scoreList.get(position).getPlayerName());
            holder.tvScore.setText(scoreList.get(position).getScore() + "");
        }

        @Override
        public int getItemCount() {
            return scoreList.size();
        }

        class MyViewHolder extends XRecyclerView.ViewHolder {

            TextView tvOrder, tvName, tvScore;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvOrder = (TextView) itemView.findViewById(R.id.tv_order);
                tvName = (TextView) itemView.findViewById(R.id.tv_name);
                tvScore = (TextView) itemView.findViewById(R.id.tv_score);
            }
        }
    }
}
