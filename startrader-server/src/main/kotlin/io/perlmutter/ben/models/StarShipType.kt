package io.perlmutter.ben.models
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
data class StarshipType(
    @BsonId val key: Id<StarshipType> = newId(),
    @BsonProperty("type_name") val typeName: String,
    @BsonProperty("starship_class") val starshipClass: String,
    val manufacturer: String,
    val model: String,
    @BsonProperty("hyperdrive_rating") val hyperdriveRating: Float,
    val mglt: Int,
    val length: Int,
    val crew: Int,
    val passenger: Int,
    val cargo: Int,
    val consumables: String,
    @BsonProperty("cost_credits") val costCredits: Int,
    @BsonProperty("ship_image") val shipImage: String,
    val unique: Boolean
)