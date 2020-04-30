package pokercc.android.ui.performance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import pokercc.android.ui.performance.databinding.ActivitySampleBinding

class SampleActivity : AppCompatActivity() {
    companion object {
        private var startTime = 0L
        private var startDuration = 0L
        private fun trackStart() {
            startTime = SystemClock.uptimeMillis()
        }

        private fun traceEnd() {
            startDuration = SystemClock.uptimeMillis() - startTime
        }

        fun start(context: Context) {
            trackStart()
            context.startActivity(Intent(context, SampleActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activitySampleBinding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(activitySampleBinding.root)
        activitySampleBinding.root.post {
            traceEnd()
            activitySampleBinding.startDuration.text = "耗时:${startDuration}ms"
        }
    }
}
