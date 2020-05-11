package com.solarexsoft.learningrxjavademo;

import androidx.lifecycle.Observer;

public class Lifecycle {
    Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {

        }
    };
}
