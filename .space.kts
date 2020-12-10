
job("Tests and Deploy") {
	val repository = "plony.registry.jetbrains.space/p/democratia/democratia-bot/myimage"
    val tag = "0.1"
	docker {
    	build {}
        push(repository) {
            this.tag = tag
        }
    }

    container("plony/upload") {
        env["SERVER_IP"] = Secrets("server_ip")
        env["AUTHORIZATIONTOKEN"] = Secrets("authorization")
        env["DEMOCRATIATOKEN"] = Secrets("democratia_token")
    }
}