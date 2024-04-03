package pm.bam.gamedeals.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import pm.bam.gamedeals.logging.Logger
import javax.inject.Inject

@AndroidEntryPoint
abstract class LoggingBaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    @Inject
    lateinit var logger: Logger

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.log(tag = this::class.simpleName) { "onAttach" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.log(tag = this::class.simpleName) { "onCreate" }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logger.log(tag = this::class.simpleName) { "onCreateView" }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.log(tag = this::class.simpleName) { "onViewCreated" }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        logger.log(tag = this::class.simpleName) { "onStart" }
        super.onStart()
    }

    override fun onResume() {
        logger.log(tag = this::class.simpleName) { "onResume" }
        super.onResume()
    }

    override fun onPause() {
        logger.log(tag = this::class.simpleName) { "onPause" }
        super.onPause()
    }

    override fun onStop() {
        logger.log(tag = this::class.simpleName) { "onStop" }
        super.onStop()
    }

    override fun onDestroyView() {
        logger.log(tag = this::class.simpleName) { "onDestroyView" }
        super.onDestroyView()
    }

    override fun onDestroy() {
        logger.log(tag = this::class.simpleName) { "onDestroy" }
        super.onDestroy()
    }

    override fun onDetach() {
        logger.log(tag = this::class.simpleName) { "onDetach" }
        super.onDetach()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        logger.log(tag = this::class.simpleName) { "onConfigurationChanged" }
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        logger.log(tag = this::class.simpleName) { "onSaveInstanceState" }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        logger.log(tag = this::class.simpleName) { "onViewStateRestored" }
        super.onViewStateRestored(savedInstanceState)
    }
}