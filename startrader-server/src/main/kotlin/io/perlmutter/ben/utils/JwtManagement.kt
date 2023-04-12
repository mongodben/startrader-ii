package io.perlmutter.ben.utils
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.auth.jwt.*
import java.util.Date

val dotenv = dotenv()
val jwtSecret = dotenv["JWT_SECRET"]

object JwtManagement {
    fun createJwtToken(username: String): String {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtSecret))

    }
    fun validator(credential: JWTCredential): JWTPrincipal? {
        if (credential.payload.getClaim("username").asString() != "") {
            return JWTPrincipal(credential.payload)
        } else {
            return null
        }
    }

    val verifier = JWT
        .require(Algorithm.HMAC256(jwtSecret))
        .build()

}