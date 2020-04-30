package pokercc.android.ui.play

import android.app.Application
import androidx.fragment.app.FragmentManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FragmentManager.enableDebugLogging(true)
    }
}