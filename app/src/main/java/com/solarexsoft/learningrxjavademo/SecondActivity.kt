package com.solarexsoft.learningrxjavademo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

/**
 * Created by houruhou on 2020/5/21/4:17 PM
 * Desc:
 */
class SecondActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SecondActivity"
        val KEY_SUBTITLE = "key_subtitle"
        fun start(context: Context, subtitles: List<Subtitle>) {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra(KEY_SUBTITLE, subtitles as Serializable)
            context.startActivity(intent)
            (context as AppCompatActivity).overridePendingTransition(R.anim.bottom_up, R.anim.keep)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        if (intent != null) {
            val subtitles = intent.getSerializableExtra(KEY_SUBTITLE) as List<Subtitle>
            Log.d(TAG, "subtitles = $subtitles")
        }
    }
}

data class Subtitle(
    val seekStart:Long,
    val seekEnd:Long,
    val subtitleId:Int,
    @Transient val name:String
):Serializable