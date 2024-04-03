package pm.bam.gamedeals.feature.webview.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.common.ui.theme.fullscreenSemiTransparentBackground
import pm.bam.gamedeals.feature.webview.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebView(
    gameTitle: String,
    url: String,
    onBack: () -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current

    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.fullscreenSemiTransparentBackground()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = gameTitle, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        navigationIcon = {
                            IconButton(onClick = { onBack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.webview_screen_navigation_back_button)
                                )
                            }
                        },
                        actions = {
                            if (loading) {
                                CircularProgressIndicator()
                            }
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(id = R.string.webview_screen_navigation_toolbar_overflow)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.webview_screen_navigation_open_in_browser)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Share,
                                            contentDescription = stringResource(id = R.string.webview_screen_navigation_open_in_browser)
                                        )
                                    },
                                    onClick = { uriHandler.openUri(url) }
                                )
                            }
                        }
                    )
                }
            ) { contentPadding: PaddingValues ->
                AndroidView(
                    modifier = Modifier.padding(contentPadding),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            this.webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    loading = true
                                    return super.shouldOverrideUrlLoading(view, request)
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    loading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    loading = false
                                }
                            }
                        }
                    },
                    update = { webView ->
                        loading = true
                        webView.loadUrl(url)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FiltersPreview() {
    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.fullscreenSemiTransparentBackground()) {
            WebView(gameTitle = "Game Title", url = "www.google.com", onBack = {})
        }
    }
}