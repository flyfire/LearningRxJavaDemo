package com.solarexsoft.learningrxjavademo;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by houruhou on 2020/5/26/8:55 PM
 * Desc:
 */
public class InOutJava {
    public static void main(String[] args) {
        RecyclerView.Adapter<? extends RecyclerView.ViewHolder> hi = new MyAdapter();
        List<? super String> test = new ArrayList<Object>();
    }
    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
