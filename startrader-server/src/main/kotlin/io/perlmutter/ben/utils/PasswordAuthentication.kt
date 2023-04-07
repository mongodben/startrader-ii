// Adapted from https://stackoverflow.com/a/2861125/17093063
// and then Kotlin-ified using ChatGPT...let's see if it works.
package io.perlmutter.ben.utils

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.Arrays
import java.util.Base64
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordAuthentication(cost: Int = DEFAULT_COST) {

    private val random: SecureRandom = SecureRandom() // Random number generator
    private val cost: Int // Cost factor for the hashing function

    init {
        iterations(cost) // Validate cost
        this.cost = cost
    }

    // Hash a password for storage
    fun hash(password: String): String {
        val chars = password.toCharArray() // Convert password to char array
        val salt = ByteArray(SIZE / 8) // Generate salt
        random.nextBytes(salt)
        val dk = pbkdf2(chars, salt, 1 shl cost) // Derive key using PBKDF2
        val hash = ByteArray(salt.size + dk.size) // Combine salt and derived key

        System.arraycopy(salt, 0, hash, 0, salt.size) // Copy salt to hash
        System.arraycopy(dk, 0, hash, salt.size, dk.size) // Copy derived key to hash

        val enc = Base64.getUrlEncoder().withoutPadding() // Base64 encoder
        return ID + cost + '$' + enc.encodeToString(hash) // Return formatted hash string
    }

    // Authenticate with a password and a stored password token
    fun authenticate(password: String, token: String): Boolean {
        val chars = password.toCharArray()
        val m = layout.matcher(token) // Match token against pattern
        if (!m.matches()) throw IllegalArgumentException("Invalid token format")

        val iterations = iterations(m.group(1).toInt()) // Get iteration count from token
        val hash = Base64.getUrlDecoder().decode(m.group(2)) // Decode stored hash
        val salt = Arrays.copyOfRange(hash, 0, SIZE / 8) // Extract salt from hash
        val check = pbkdf2(chars, salt, iterations) // Derive key from input password

        // Compare derived key with stored key
        var zero = 0
        for (idx in check.indices) zero = zero or (hash[salt.size + idx].toInt() xor check[idx].toInt())

        return zero == 0 // Return true if keys match, false otherwise
    }

    // Perform PBKDF2 key derivation
    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
        val spec: KeySpec = PBEKeySpec(password, salt, iterations, SIZE)
        return try {
            val f = SecretKeyFactory.getInstance(ALGORITHM)
            f.generateSecret(spec).encoded
        } catch (ex: NoSuchAlgorithmException) {
            throw IllegalStateException("Missing algorithm: $ALGORITHM", ex)
        } catch (ex: InvalidKeySpecException) {
            throw IllegalStateException("Invalid SecretKeyFactory", ex)
        }
    }

    // Calculate the number of iterations based on the cost factor
    private fun iterations(cost: Int): Int {
        require(cost in 0..30) { "cost: $cost" }
        return 1 shl cost
    }

    companion object {
        const val ID = "\$31$" // Identifier for the hash format
        const val DEFAULT_COST = 16 // Default cost factor
        private const val ALGORITHM = "PBKDF2WithHmacSHA1" // Key derivation algorithm
        private const val SIZE = 128 // Size of the derived key
        private val layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})")
    }
}