
job("Tests and Deploy") {
	val repository = "plony.registry.jetbrains.space/p/democratia/democratia-bot/myimage"
    val tag = "0.1"
	docker {
    	build {}
        push(repository) {
            this.tag = tag
        }
    }

    container("curlimages/curl") {
        env["SERVER_IP"] = Secrets("server_ip")
        env["AUTHORIZATIONTOKEN"] = Secrets("authorization")
        env["DEMOCRATIATOKEN"] = Secrets("democratia_token")

        shellScript {

            content = """curl --silent --show-error --fail -X POST ${'$'}SERVER_IP -H "Authorization: ${'$'}{AUTHORIZATIONTOKEN"} -H 'Content-Type: application/json' -d '{"repository": "$repository","tag": "$tag","env": {"DEMOCRATIA_TOKEN": "${'$'}{DEMOCRATIATOKEN}"}}'"""
        }
    }
}