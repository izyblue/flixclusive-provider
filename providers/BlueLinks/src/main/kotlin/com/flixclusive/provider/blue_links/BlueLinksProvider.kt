package com.flixclusive.provider.blue_links

import android.content.Context
import androidx.compose.runtime.Composable
import com.flixclusive.provider.FlixclusiveProvider
import com.flixclusive.provider.Provider
import com.flixclusive.provider.ProviderApi
import okhttp3.OkHttpClient


@FlixclusiveProvider
class BlueLinksProvider : Provider() {
    @Composable
    override fun SettingsScreen() {
        // Create a custom component for code readability
        SettingsScreen(resources = resources)
    }

    override fun getApi(
        context: Context,
        client: OkHttpClient
    ): ProviderApi {
        return BlueLinksProviderApi(
            client = client,
            provider = this
        )
    }

    override fun onUnload(context: Context?) {
        super.onUnload(context)
    }
}
