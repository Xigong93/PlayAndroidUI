package pokercc.android.ui.views.rotate3d

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pokercc.android.ui.views.databinding.ViewsLotteryFragmentBinding

class LotteryFragment : Fragment() {

    private lateinit var viewsLotteryFragmentBinding: ViewsLotteryFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewsLotteryFragmentBinding =
            ViewsLotteryFragmentBinding.inflate(inflater, container, false)
        return viewsLotteryFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewsLotteryFragmentBinding.lotteryRunningButton.setOnClickListener {
            val dialog = LiveLotteryDialog(requireActivity())
            dialog.show(LiveLotteryState.Running)
            view.postDelayed({
                dialog.dismiss()
            }, 3000)
        }
        viewsLotteryFragmentBinding.winButton.setOnClickListener {
            LiveLotteryDialog(requireActivity()).show(LiveLotteryState.Win("8989"))
        }
        viewsLotteryFragmentBinding.notWinButton.setOnClickListener {
            LiveLotteryDialog(requireActivity()).show(LiveLotteryState.NotWin)
        }
    }

}
