package pokercc.android.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import pokercc.android.ui.views.databinding.ViewsScoreBarFragmentBinding


internal class ScoreBarFragment : Fragment() {
    private var anim = false

    private lateinit var viewsScoreBarFragmentBinding: ViewsScoreBarFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewsScoreBarFragmentBinding =
            ViewsScoreBarFragmentBinding.inflate(inflater, container, false)
        return viewsScoreBarFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewsScoreBarFragmentBinding.apply {

        scoreBar.setProgressTextFormatter { progress: Int ->
            "$progress"
        }
        scoreBar2.setProgressTextFormatter { progress: Int ->
            "$progress%"
        }
        switch1.setOnCheckedChangeListener { _, isChecked -> anim = isChecked }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (anim.not()) {
                    scoreBar.setProgress(progress, false)
                    scoreBar2.setProgress(progress, false)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                if (anim) {
                    scoreBar.setProgress(seekBar.progress, true)
                    scoreBar2.setProgress(seekBar.progress, true)
                }
            }
        })
        }

    }
}
