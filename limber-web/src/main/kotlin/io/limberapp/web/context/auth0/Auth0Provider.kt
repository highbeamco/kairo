package io.limberapp.web.context.auth0

import io.limberapp.web.context.ProviderValue
import io.limberapp.web.util.AppState
import io.limberapp.web.util.Auth0Client
import io.limberapp.web.util.async
import io.limberapp.web.util.createAuth0Client
import io.limberapp.web.util.process
import kotlinext.js.asJsObject
import kotlinx.coroutines.await
import react.RBuilder
import react.RHandler
import react.RProps
import react.children
import react.createContext
import react.createElement
import react.functionalComponent
import react.useContext
import react.useEffect
import react.useState
import kotlin.browser.window

private val auth0Context = createContext<Auth0Context>()
internal fun useAuth() = useContext(auth0Context)

internal data class Props(val onRedirectCallback: (AppState?) -> Unit) : RProps

private val auth0Provider = functionalComponent<Props> { props ->

    val (isLoading, setIsLoading) = useState(true)
    val (auth0Client, setAuth0Client) = useState<Auth0Client?>(null)
    val (isAuthenticated, setIsAuthenticated) = useState(false)

    useEffect(emptyList()) {
        async {
            println(window.location)
            val config = Auth0ConfigImpl(
                domain = process.env.AUTH0_DOMAIN,
                client_id = "eXqVXnBUsRkvDv2nTv9hURTA2IHzNWDa",
                redirect_uri = "http://localhost:8080"
            )
            val client = createAuth0Client(config.asJsObject()).await()
            setAuth0Client(client)
            if (window.location.search.contains("code=")) {
                val appState = client.handleRedirectCallback().await().appState
                (props.onRedirectCallback)(appState)
            }
            setIsAuthenticated(client.isAuthenticated().await())
            setIsLoading(false)
        }
    }

    val configObject = ProviderValue(
        Auth0Context(
            isLoading = isLoading,
            isAuthenticated = isAuthenticated,
            login = { checkNotNull(auth0Client).loginWithRedirect() },
            getJwt = { checkNotNull(auth0Client).getTokenSilently().await() },
            logout = { checkNotNull(auth0Client).logout() }
        )
    )
    child(createElement(auth0Context.Provider, configObject, props.children))
}

internal fun RBuilder.auth0Provider(onRedirectCallback: (AppState?) -> Unit, children: RHandler<Props>) {
    child(auth0Provider, Props(onRedirectCallback), handler = children)
}

