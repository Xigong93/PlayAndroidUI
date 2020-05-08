package pokercc.android.ui.coordinatelayout.videoscroll

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.AppBarLayout

import pokercc.android.ui.coordinatelayout.R
import pokercc.android.ui.coordinatelayout.databinding.CooVideoScrollFragmentBinding
import kotlin.math.absoluteValue

/**
 * A simple [Fragment] subclass.
 */
class VideoScrollFragment : Fragment() {

    private lateinit var cooVideoScrollFragmentBinding: CooVideoScrollFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cooVideoScrollFragmentBinding =
            CooVideoScrollFragmentBinding.inflate(inflater, container, false)
        return cooVideoScrollFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cooVideoScrollFragmentBinding.apply {
            val videoViewWidth = resources.displayMetrics.widthPixels
            val videoViewHeight =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics)
            appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
                val percent = verticalOffset.toFloat().absoluteValue / appBarLayout.totalScrollRange
                val scale = (1 - percent) * 0.5f + 0.5f
                videoView.scaleX = scale
                videoView.scaleY = scale
                videoView.translationX = -percent * videoViewWidth / 4
                videoView.translationY = -percent * videoViewHeight / 4
            })
        }
    }

}
