package pokercc.android.ui.performance

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pokercc.android.fragment.FragmentNavigator
import pokercc.android.ui.performance.databinding.SampleFragmentBinding


class SampleFragment : Fragment() {

    companion object {
        private var startTime = 0L
        private var startDuration = 0L
        private fun trackStart() {
            startTime = SystemClock.uptimeMillis()
        }

        private fun traceEnd() {
            startDuration = SystemClock.uptimeMillis() - startTime
        }

        fun start(navigator: FragmentNavigator) {
            trackStart()
            navigator.navigateTo(SampleFragment())
        }
    }

    private lateinit var sampleFragmentBinding: SampleFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sampleFragmentBinding = SampleFragmentBinding.inflate(inflater, container, false)
        return sampleFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sampleFragmentBinding.root.post {
            traceEnd()
            sampleFragmentBinding.startDuration.text = "耗时:${startDuration}ms"
        }
    }


}
