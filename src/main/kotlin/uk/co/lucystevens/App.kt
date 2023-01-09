package uk.co.lucystevens

import uk.co.lucystevens.cli.AppRunner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uk.co.lucystevens.api.RouteController

class App : KoinComponent {
    private val routeController by inject<RouteController>()

    private val logger = logger<AppRunner>()

    fun run(){
        logger.info("Starting app")
        routeController.start()
    }
}