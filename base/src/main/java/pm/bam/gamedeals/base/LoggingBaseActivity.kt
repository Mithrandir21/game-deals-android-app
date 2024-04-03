package pm.bam.gamedeals.base

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import pm.bam.gamedeals.logging.Logger
import javax.inject.Inject

@AndroidEntryPoint
abstract class LoggingBaseActivity : ComponentActivity() {

    @Inject
    lateinit var logger: Logger


    override fun onConfigurationChanged(newConfig: Configuration) {
        logger.log(tag = this::class.simpleName) { "onConfigurationChanged" }
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.log(tag = this::class.simpleName) { "onCreate #1 (savedInstanceState != null) = ${savedInstanceState != null}" }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        logger.log(tag = this::class.simpleName) { "onCreate #2 (savedInstanceState != null) = ${savedInstanceState != null}" }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        logger.log(tag = this::class.simpleName) { "onPostCreate #1 (savedInstanceState != null) = ${savedInstanceState != null}" }
        super.onPostCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        logger.log(tag = this::class.simpleName) { "onPostCreate #2 (savedInstanceState != null) = ${savedInstanceState != null}" }
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        logger.log(tag = this::class.simpleName) { "onStart" }
        super.onStart()
    }

    override fun onResume() {
        logger.log(tag = this::class.simpleName) { "onResume" }
        super.onResume()
    }

    override fun onPostResume() {
        logger.log(tag = this::class.simpleName) { "onPostResume" }
        super.onPostResume()
    }

    override fun onPause() {
        logger.log(tag = this::class.simpleName) { "onPause" }
        super.onPause()
    }

    override fun onStop() {
        logger.log(tag = this::class.simpleName) { "onStop" }
        super.onStop()
    }

    override fun onDestroy() {
        logger.log(tag = this::class.simpleName) { "onDestroy" }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        logger.log(tag = this::class.simpleName) { "onSaveInstanceState" }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        logger.log(tag = this::class.simpleName) { "onRestoreInstanceState" }
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun finish() {
        logger.log(tag = this::class.simpleName) { "finish" }
        super.finish()
    }

    override fun finishAffinity() {
        logger.log(tag = this::class.simpleName) { "finishAffinity" }
        super.finishAffinity()
    }

    override fun finishAndRemoveTask() {
        logger.log(tag = this::class.simpleName) { "finishAndRemoveTask" }
        super.finishAndRemoveTask()
    }
}