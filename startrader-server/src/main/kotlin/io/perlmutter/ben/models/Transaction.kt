package io.perlmutter.ben.models
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

@Serializable
data class Transaction (
    @BsonId val key: Id<Transaction> = newId(),
    val buyer_user_id: Id<User>,
    val seller_user_id: Id<User>,
    @Contextual val sale_date: Date,
    val sale_price: Float,
)