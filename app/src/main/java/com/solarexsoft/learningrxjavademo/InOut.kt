package com.solarexsoft.learningrxjavademo

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by houruhou on 2020/5/26/8:55 PM
 * Desc:
 */
// https://www.kotlincn.net/docs/reference/generics.html
// https://kaixue.io/kotlin-generics/
fun main() {
    val adapter:RecyclerView.Adapter<out RecyclerView.ViewHolder> = InOutJava.MyAdapter()
}
