package com.solarexsoft.learningrxjavademo

import android.os.Looper
import android.provider.CalendarContract
import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 07:33/2020/5/11
 *    Desc:
 * </pre>
 */
 
class SolarexRxBinding(val view:View) : Observable<Any>() {

    override fun subscribeActual(observer: Observer<in Any>?) {
        val listenerObserver = ListenerObserver(WeakReference(view), observer)
        observer?.onSubscribe(listenerObserver)
        view.setOnClickListener(listenerObserver)
    }

    class ListenerObserver(private val view:WeakReference<View>, private val observer: Observer<in Any>?) : View.OnClickListener,Disposable {
        private val EVENT = Any()
        private val atomicBoolean = AtomicBoolean()
        override fun onClick(v: View?) {
            if (!isDisposed && v == view.get()) {
                observer?.onNext(EVENT)
            }
        }

        override fun isDisposed(): Boolean {
            return atomicBoolean.get()
        }

        override fun dispose() {
            if (view.get() == null) {
                return
            }
            if (atomicBoolean.compareAndSet(false, true)) {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    view.get()!!.setOnClickListener(null)
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect{
                        view.get()!!.setOnClickListener(null)
                    }
                }
            }
        }

    }
}