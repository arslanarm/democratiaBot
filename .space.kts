
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
        env["AUTHORIZATION"] = Secrets("authorization")
        env["DEMOCRATIA_TOKEN"] = Secrets("democratia_token")
        env["REPOSITORY"] = Secrets("repository")
        env["TAG"] = "v0.1"
        env["NAME"] = "plony/democratia"
        shellScript {
            content = "python /app/upload.py"
        }
    }
}