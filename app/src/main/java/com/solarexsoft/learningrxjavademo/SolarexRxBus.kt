package com.solarexsoft.learningrxjavademo

import android.media.audiofx.DynamicsProcessing
import io.reactivex.Flowable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 08:02/2020/5/11
 *    Desc:
 * </pre>
 */
 
class SolarexRxBus {
    companion object {
        val INSTANCE = SolarexRxBus()
        fun instance(): SolarexRxBus {
            return INSTANCE
        }
    }

    // toSerialized 转成线程安全的操作
    private var mBus:FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    fun post(event: Any) {
        mBus.onNext(event)
    }

    fun toFlowable(): Flowable<Any> {
        return mBus
    }

    fun <T> toFlowable(clz: Class<T>):Flowable<T> {
        return mBus.ofType(clz)
    }
}