package pokercc.android.ui.views.rotate3d

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.live_lottery_win.view.*
import pokercc.android.ui.views.R

sealed class LiveLotteryState {
    object Running : LiveLotteryState()
    object NotWin : LiveLotteryState()
    class Win(val code: String) : LiveLotteryState()
}

/**
 * 直播抽奖对话框
 * @author pokercc
 * @date 2020-3-13 13:37:01
 */
class LiveLotteryDialog(
    private val appCompatActivity: Activity
) : Dialog(appCompatActivity, false, null) {
    private val depthZ = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels
        )
    }

    private var lotteryState: LiveLotteryState? = null
    /**
     * 显示抽奖状态
     */
    fun show(lotteryState: LiveLotteryState) {
        this.lotteryState = lotteryState
        when (lotteryState) {
            is LiveLotteryState.Running -> showLotteryRunning()
            is LiveLotteryState.Win -> showWin(lotteryState.code)
            is LiveLotteryState.NotWin -> showNotWin()
        }
        super.show()
    }

    override fun onBackPressed() {
        if (lotteryState == LiveLotteryState.Running) {
            appCompatActivity.onBackPressed()
        }
        super.onBackPressed()
    }

    private fun showNotWin() {
        val frameLayout = View.inflate(context, R.layout.live_lottery_notwin, null)
        frameLayout.findViewById<View>(R.id.ic_close).setOnClickListener {
            dismiss()
        }
        setContentView(frameLayout)
        start3dAnim(frameLayout.findViewById(R.id.anim_image))
    }

    private fun showWin(code: String) {
        val frameLayout = View.inflate(context, R.layout.live_lottery_win, null)
        frameLayout.findViewById<View>(R.id.ic_close).setOnClickListener {
            dismiss()
        }
        // 加点空格,看起来比较好看
        frameLayout.lottery_code.text =
            if (code.length > 8) code else code.toCharArray().joinToString(" ")
        setContentView(frameLayout)
        start3dAnim(frameLayout.findViewById(R.id.anim_image))

    }

    private fun start3dAnim(contentView: View) {

        contentView.post {
            val animationSet = AnimationSet(false)
            val centerX = contentView.width * 0.5f
            val centerY = contentView.height * 0.5f
            animationSet.addAnimation(
                ScaleAnimation(
                    0.5f,
                    1.0f,
                    0.5f,
                    1.0f,
                    centerX,
                    centerY
                ).apply {
                    duration = 200
                    interpolator =
                        AccelerateInterpolator()
                })
            animationSet.addAnimation(
                Rotate3dAnimation(
                    context,
                    0f,
                    180f * 2,
                    centerX,
                    centerY,
                    depthZ,
                    true
                ).apply {
                    duration = 1200
                    fillAfter = true
                    interpolator =
                        OvershootInterpolator(2f)
                }
            )
            contentView.startAnimation(animationSet)
        }
    }

    private fun showLotteryRunning() {
        setContentView(R.layout.live_lottery_running)
    }

    @Deprecated(
        "请使用show(LiveLotteryState)方法",
        replaceWith = ReplaceWith("show( lotteryState: LiveLotteryState)")
    )
    override fun show() {
        super.show()
    }
}