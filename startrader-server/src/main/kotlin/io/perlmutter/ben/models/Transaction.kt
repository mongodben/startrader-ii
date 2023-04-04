package io.perlmutter.ben.models
import org.bson.BsonObjectId
import org.bson.codecs.DateCodec
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
data class Transaction (
    @BsonId val key: Id<Transaction> = newId(),
    @BsonProperty("buyer_user_id") val buyerUserId: BsonObjectId,
    @BsonProperty("seller_user_id") val sellerUserId: BsonObjectId,
    @BsonProperty("sale_date") val saleDate: DateCodec,
    @BsonProperty("sale_price") val salePrice: Float,
)