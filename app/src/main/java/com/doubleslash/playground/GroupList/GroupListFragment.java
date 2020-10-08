package com.doubleslash.playground.GroupList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doubleslash.playground.R;


public class GroupListFragment extends Fragment {
    RecyclerView recyclerView;
    GroupAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_group_list, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupAdapter();
        addItems(); // 리사이클러뷰에 들어갈 객체들
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnGroupItemClickListener() {
            @Override
            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                Group item = adapter.getItem(position);
                Toast.makeText(getContext(), "아이템 선택됨: "+item.getSubject(), Toast.LENGTH_LONG).show();  //그냥 클릭했을 때 잘 되는지 확인용..
            }
        });
        initUI(rootView);
        return rootView;
    }
    private void initUI(ViewGroup rootView){

    }
    private void addItems(){
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "자소서 스터디",
                "자기소개서 스터디 하실 분 구합니다:)", R.drawable.img_join));
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "C++ 스터디",
                "열심히 코딩 공부하실 분들 오세여", R.drawable.img_join));
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "자소서 스터디",
                "자소서 서로 첨삭해주는 스터디", R.drawable.group_vio));
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "자소서 스터디",
                "자소서 서로 첨삭해주는 스터디", R.drawable.img_join));
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "자소서 스터디",
                "자소서 서로 첨삭해주는 스터디", R.drawable.group_vio));
        adapter.addItem(new Group("서울 송파", "스터디", "1", "4", "자소서 스터디",
                "자소서 서로 첨삭해주는 스터디", R.drawable.group_vio));
    }
}