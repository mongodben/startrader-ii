package io.perlmutter.ben.models

import org.bson.BsonObjectId
import org.bson.codecs.DateCodec
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
data class Starship (
    @BsonId val key: Id<Starship> = newId(),
    @BsonProperty("ship_type_id") val shipTypeId: BsonObjectId,
    @BsonProperty("custom_name") val customName: String,
    @BsonProperty("sale_price") val salePrice: Float,
    @BsonProperty("light_years_traveled") val lightYearsTraveled: Int,
    @BsonProperty("owner_user_id") val ownerUserId: BsonObjectId,
    @BsonProperty("for_sale") val forSale: Boolean,
    @BsonProperty("seller_comment") val sellerComment: Boolean,
    @BsonProperty("post_date") val postDate: Boolean,
)