package pokercc.android.ui.coordinatelayout.videoscroll

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children

class ToolbarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        children.forEach {
            if (it is ViewGroup) {
                it.layout(l, t, r, b)
            }
        }
    }
}