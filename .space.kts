
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
        shellScript {
            content = """curl --silent --show-error --fail -X POST ${Secrets("server_ip")} -H 'Authorization: ${Secrets("authorization")}' -H 'Content-Type: application/json' -d '{"repository": "$repository","tag": "$tag","env": {"DEMOCRATIA_TOKEN": "${Secrets("democratia_token")}"}}'"""
        }
    }
}