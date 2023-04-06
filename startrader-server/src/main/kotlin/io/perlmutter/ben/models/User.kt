package io.perlmutter.ben.models
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId


@Serializable
data class User (
    @BsonId val key: Id<User> = newId(),
    val name: String,
    val email: String,
    val password: String,
    val species: String, // TODO: figure out how to correlate this only with the SpeciesType `species` values
    val bio: String
)