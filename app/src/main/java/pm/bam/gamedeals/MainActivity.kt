package pm.bam.gamedeals

import android.os.Bundle
import androidx.activity.compose.setContent
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import pm.bam.gamedeals.base.LoggingBaseActivity
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.navigation.NavGraph
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : LoggingBaseActivity() {

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameDealsTheme {
                NavGraph()
            }
        }
    }
}