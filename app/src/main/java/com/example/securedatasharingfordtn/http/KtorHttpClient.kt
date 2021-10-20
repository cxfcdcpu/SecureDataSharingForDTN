package com.example.securedatasharingfordtn.http

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*

object KtorHttpClient {
    val BASE_URL = "http://131.151.90.204:8081/"
    val APIName = "ReVo_webtest/"
    val KtorClient = HttpClient(Android) {
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }
        install(JsonFeature){
            serializer = GsonSerializer()
        }
    }
}