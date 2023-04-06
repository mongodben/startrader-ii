package io.perlmutter.ben.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

@Serializable
data class Starship (
    @BsonId val key: Id<Starship> = newId(),
    val ship_type_id: Id<StarshipType>,
    val custom_name: String?,
    val sale_price: Float,
    val light_years_traveled: Int,
    val owner_user_id: Id<User>,
    val for_sale: Boolean,
    val seller_comment: String,
    @Contextual val post_date: Date,
)
