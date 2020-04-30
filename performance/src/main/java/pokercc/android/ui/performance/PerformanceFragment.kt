package pokercc.android.ui.performance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pokercc.android.fragment.getNavigator
import pokercc.android.ui.performance.databinding.PerformanceFragmentBinding

/**
 * A simple [Fragment] subclass.
 */
class PerformanceFragment : Fragment() {
    private lateinit var performanceFragmentBinding: PerformanceFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        performanceFragmentBinding = PerformanceFragmentBinding.inflate(inflater, container, false)
        return performanceFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performanceFragmentBinding.startSampleFragmentButton.setOnClickListener {
            SampleFragment.start(getNavigator())
        }
        performanceFragmentBinding.startSampleActivityButton.setOnClickListener {
            SampleActivity.start(it.context)
        }
    }

}
