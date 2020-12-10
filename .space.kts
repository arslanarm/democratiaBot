
job("Tests and Deploy") {
	docker {
    	build {}
        push("plony.registry.jetbrains.space/p/democratia/democratia-bot/myimage") {
            this.tag = "0.1"
        }
    }
}