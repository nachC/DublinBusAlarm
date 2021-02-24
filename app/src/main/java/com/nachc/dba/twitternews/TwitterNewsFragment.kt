package com.nachc.dba.twitternews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nachc.dba.R

class TwitterNewsFragment : Fragment() {

    /**
     * Might be overloading the main thread, consider async way
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_twitter_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webView)
        val data = "<a class=\"twitter-timeline\" data-lang=\"en\" data-theme=\"light\" href=\"https://twitter.com/dublinbusnews?ref_src=twsrc%5Etfw\">Tweets by dublinbusnews</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>"
        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true

            loadData(data, "text/html", "utf-8")
        }
    }
}