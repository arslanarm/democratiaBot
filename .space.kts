/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/
job("Tests and Deploy") {
    container("gradle:jdk11") {
        resources {
            memory = 8192
        }

        kotlinScript { api ->
            api.gradle("test")
        }
    }
    
	docker {
    	build {}
        push("plony.registry.jetbrains.space/p/democratia/democratia-bot/myimage") {
            tag = "$JB_SPACE_GIT_BRANCH"
        }
    }
}