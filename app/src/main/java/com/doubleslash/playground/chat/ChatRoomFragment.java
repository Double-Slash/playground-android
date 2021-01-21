package com.doubleslash.playground.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.doubleslash.playground.ClientApp;
import com.doubleslash.playground.database.entity.MessageEntity;
import com.doubleslash.playground.database.repository.MessageRepository;
import com.doubleslash.playground.databinding.FragmentChatListBinding;
import com.doubleslash.playground.retrofit.RetrofitClient;
import com.doubleslash.playground.retrofit.dto.ChatRoomDTO;
import com.doubleslash.playground.socket.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;

public class ChatRoomFragment extends Fragment {
    private FragmentChatListBinding binding;
    private ChatRoomAdapter adapter;

    private RetrofitClient retrofitClient;
    private MessageRepository repository;
    private MessageEntity lastMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initUI();

        return view;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        retrofitClient = RetrofitClient.getInstance();
        repository = new MessageRepository(getActivity().getApplication());

        adapter = new ChatRoomAdapter(getContext());

        if (ClientApp.roomInfos.size() == 0) {
            List<ChatRoomDTO> chatRooms = retrofitClient.getChatRoomInfos().getData();
            for (ChatRoomDTO room : chatRooms) {
                // 마지막으로 읽은 메세지, 안 읽은 메세지 수 세팅
                if (ClientApp.RoomMsgQueues.containsKey(room.getName()) && ClientApp.RoomMsgQueues.get(room.getId()).size() != 0) {
                    Queue<Message> queue = ClientApp.RoomMsgQueues.get(room.getId());
                    adapter.addItem(new ChatRoomItem(room.getId(), room.getName(), room.getTeamImageUrl(), queue.peek().getText(), dateConvert(queue.peek().getSendTime()), queue.size()));
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            lastMsg = repository.getLastMessage(room.getId());
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (lastMsg != null) {
                        adapter.addItem(new ChatRoomItem(room.getId(), room.getName(), room.getTeamImageUrl(), lastMsg.getText(), dateConvert(lastMsg.getSendTime()), 0));
                    } else {
                        adapter.addItem(new ChatRoomItem(room.getId(), room.getName(), room.getTeamImageUrl(), "", "", 0));
                    }
                }

                adapter.setOnItemClickListener((holder, view1, position) -> {
                    // 눌렀을 때
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("roomId", adapter.getItem(position).getRoomId());
                    startActivity(intent);
                });
            }
        } else {
            for (String roomId : ClientApp.roomInfos.keySet()) {
                ChatRoomDTO room = ClientApp.roomInfos.get(roomId);

                // 마지막으로 읽은 메세지, 안 읽은 메세지 수 세팅
                if (ClientApp.RoomMsgQueues.containsKey(room.getName()) && ClientApp.RoomMsgQueues.get(roomId).size() != 0) {
                    Queue<Message> queue = ClientApp.RoomMsgQueues.get(roomId);
                    adapter.addItem(new ChatRoomItem(room.getId(), room.getName(), room.getTeamImageUrl(), queue.peek().getText(), dateConvert(queue.peek().getSendTime()), queue.size()));
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            lastMsg = repository.getLastMessage(roomId);
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    adapter.addItem(new ChatRoomItem(room.getId(), room.getName(), room.getTeamImageUrl(), lastMsg.getText(), dateConvert(lastMsg.getSendTime()), 0));
                }

                adapter.setOnItemClickListener((holder, view1, position) -> {
                    // 눌렀을 때
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("roomId", adapter.getItem(position).getRoomId());
                    startActivity(intent);
                });
            }
        }

        binding.recyclerView.setAdapter(adapter);
    }

    // System.currentTimeMillis를 몇시:몇분 am/pm 형태의 문자열로 반환
    private String dateConvert(long currentMiliis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMiliis));
    }
}
