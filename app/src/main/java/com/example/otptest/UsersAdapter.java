package com.example.otptest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.otptest.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    Context context;
    ArrayList<com.example.otptest.User> users;

    public UsersAdapter(Context context, ArrayList<com.example.otptest.User> users){
        this.context = context;
        this.users = users;
    }

    @androidx.annotation.NonNull

    @Override
    public UsersViewHolder onCreateViewHolder(@androidx.annotation.NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull  UsersViewHolder holder, int position) {

        com.example.otptest.User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            String strDateFormat = "EEE, hh:mm a";
                            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                            holder.binding.lastMsg.setText(lastMsg);
                            holder.binding.msgTime.setText(sdf.format(time));
                        }else {
                            holder.binding.lastMsg.setText("Tap to chat");
                            holder.binding.msgTime.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

                    }
                });


        holder.binding.username.setText(user.getName());

        //holder.binding.profile.
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)//if we get no Image
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.example.otptest.ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image", user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        RowConversationBinding binding;

        public UsersViewHolder(  View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
