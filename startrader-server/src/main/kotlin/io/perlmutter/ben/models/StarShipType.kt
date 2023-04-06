package io.perlmutter.ben.models
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class StarshipType(
    @BsonId val key: Id<StarshipType> = newId(),
    val type_name: String,
    val starship_class: String,
    val manufacturer: String,
    val model: String,
    val hyperdrive_rating: Float,
    val mglt: Int,
    val length: Int,
    val crew: Int,
    val passenger: Int,
    val cargo: Int,
    val consumables: String,
    val cost_credits: Int,
    val ship_image: String,
    val unique: Boolean
)