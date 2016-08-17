package com.gcrj.pulltorefreshnestedscrollingview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gcrj.pulltorefreshnestedscrollingviewlibrary.OnRefreshListener;
import com.gcrj.pulltorefreshnestedscrollingviewlibrary.OnStateChangedListener;
import com.gcrj.pulltorefreshnestedscrollingviewlibrary.PullToRefreshNestedScrollingView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView) findViewById(R.id.tv);
        final PullToRefreshNestedScrollingView pullToRefreshNestedScrollingView = (PullToRefreshNestedScrollingView) findViewById(R.id.pull_to_refresh_nested_scrolling_view);
        pullToRefreshNestedScrollingView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                tv.setText("正在刷新");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshNestedScrollingView.onRefreshComplete();
                        tv.setText("下拉刷新");
                    }
                }, 3000);
            }
        });
        pullToRefreshNestedScrollingView.setOnStateChangedListener(new OnStateChangedListener() {

            @Override
            public void onStateChanged(int state, float scaleOfLayout) {
                if(state == 1){
                    tv.setText("下拉刷新");
                }else if(state == 2){
                    tv.setText("释放刷新");
                }
            }

        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                return new VH(new TextView(MainActivity.this));
            }


            @Override
            public void onBindViewHolder(VH holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });

    }

    class VH extends RecyclerView.ViewHolder {

        private TextView tv;

        public VH(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(10, 10, 10, 10);
        }

        public void bind(int position) {
            tv.setText(position + "");
        }
    }
}
