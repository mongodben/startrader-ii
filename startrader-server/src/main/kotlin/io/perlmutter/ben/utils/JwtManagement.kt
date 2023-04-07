package io.perlmutter.ben.utils
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
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
}