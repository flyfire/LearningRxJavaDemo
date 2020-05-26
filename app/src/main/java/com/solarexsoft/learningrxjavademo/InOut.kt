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
    val test0 = Test<Human>()
    val test1: Test<Human2> = test0

    val test2 = Test2<Human3>(Human3())
    val test3: Test2<Human> = test2
}

data class Person(
    val name:String
)

open class Human {
    fun test() {

    }
}

open class Human2:Human() {
    fun test2() {

    }
}

open class Human3:Human2() {
    fun test3() {

    }
}


class Test<in T:Human>(/*val human: T*/){
    fun set(t: T){

    }

//    fun get(): T?{
//        return human
//    }
}

class Test2<out T: Human>(val human: T){
//    fun set(t: T) {
//
//    }

    fun get(): T? {
        return human
    }
}