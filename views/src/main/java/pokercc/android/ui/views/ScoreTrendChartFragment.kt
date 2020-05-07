package pokercc.android.ui.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import pokercc.android.ui.views.databinding.ViewsScoreTrendChartFragmentBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class ScoreTrendChartFragment : Fragment() {

    private lateinit var viewsScoreTrendChartFragmentBinding: ViewsScoreTrendChartFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewsScoreTrendChartFragmentBinding =
            ViewsScoreTrendChartFragmentBinding.inflate(inflater, container, false)
        return viewsScoreTrendChartFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewsScoreTrendChartFragmentBinding.apply {
            val recorders = (0 until 20).map {
                val index = Random.Default.nextInt(STAGES.size)
                TestRecorder(
                    id = "111",
                    stageName = STAGES[index],
                    wordCount = arrayOf(
                        "2500-",
                        "2500+",
                        "3500+",
                        "5000+",
                        "6000+",
                        "7000+",
                        "8000+"
                    )[index],
                    date = arrayOf("2019.12.12", "2019.9.19", "2019.9.9").random()

                )
            }.toList()
            Log.d("recorders", recorders.toString())
            chart.setTestRecorders(recorders, object : OnLabelClickListener {
                override fun onLabelClick(view: View, testRecorder: TestRecorder, position: Int) {
                    Toast.makeText(view.context, "点击了:${testRecorder}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
