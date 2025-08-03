package edu.kitt.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.auth.parseAuthorizationHeader
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

class JwtConfig (val jwtIssuer: String,
                 val jwtAudience: String,
                 val jwtRealm: String,
                 val jwtSecret: String
) {

    private val CLAIM_USERID = "userId"
    private val CLAIM_USERNAME = "username"

    private val jwtAlgorithm = Algorithm.HMAC512(jwtSecret)
    private val jwtVerifier: JWTVerifier = JWT
        .require(jwtAlgorithm)
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .build()

    fun generateToken(userId: Int, username: String, maxAge: Int): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim(CLAIM_USERID, userId) // Aggiungi l'ID utente come claim
            .withClaim(CLAIM_USERNAME, username) // Aggiungi il username come claim
            .withExpiresAt(Date(System.currentTimeMillis() + maxAge * 1000L))
            .sign(jwtAlgorithm)
    }

    fun configureKtorFeature(config: JWTAuthenticationProvider.Config) = with(config) {
        verifier(jwtVerifier)
        realm = jwtRealm

        authHeader { call ->
            call.request.cookies["jwt-token"]?.let {
                return@authHeader parseAuthorizationHeader("Bearer $it")
            }
            call.request.headers["Authorization"]?.let {
                return@authHeader parseAuthorizationHeader(it)
            }
            return@authHeader null
        }

        validate { credentials ->
            val userId = credentials.payload.getClaim(CLAIM_USERID).asInt()
            val username = credentials.payload.getClaim(CLAIM_USERNAME).asString()

            if (userId != null && username != null) {
                JWTPrincipal(credentials.payload)
            } else {
                null
            }
        }
    }
}