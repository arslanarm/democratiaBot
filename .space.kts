@file:DependsOn("com.squareup.okhttp3:okhttp:4.9.0")

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


val JSON: MediaType = MediaType.get("application/json; charset=utf-8")

var client: OkHttpClient = OkHttpClient()

fun post(url: String, json: String): String? {
    val body: RequestBody = RequestBody.create(JSON, json)
    val request: Request = Request.Builder()
        .url(url)
        .post(body)
        .build()
    return client.newCall(request).execute().use { response -> response.body()?.string() }
}

job("Tests and Deploy") {
    val repository = "plony.registry.jetbrains.space/p/democratia/democratia-bot/myimage"
    val tag = "0.1"
	docker {
    	build {}
        push(repository) {
            this.tag = tag
        }
        val response = post(Secrets("server_ip").toString(), """
            {
                "repository": "$repository",
                "tag": "$tag",
                "env": {
                    "DEMOCRATIA_TOKEN": "${Secrets("democratia_token")}"
                }
            }
        """.trimIndent())
        println(response)
    }
}