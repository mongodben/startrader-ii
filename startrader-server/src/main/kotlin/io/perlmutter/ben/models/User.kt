package io.perlmutter.ben.models
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

enum class SpeciesType(val species: String) {
    HUMAN("Human"),
    DROID("Driod"),
    WOOKIEE("Wookie"),
    RODIAN("Rodian"),
    HUTT("Hutt"),
    YODAS_SPECIES("Yoda's Species"),
    TRANDOSHAN("Trandoshan"),
    MON_CALAMARI("Mon Calamari"),
    EWOK("Ewok"),
    SULLUSTAN("Sullustan"),
    NEIMODIAN("Neimodian"),
    GUNGAN("Gungan"),
    TOYDARIAN("Toydarian"),
    DUG("Dug"),
    TWILEK("Twi'lek"),
    ALEENA("Aleena"),
    VULPTEREEN("Vulptereen"),
    XEXTOS("Xexto"),
    TOONG("Toong"),
    CEREAN("Cerean"),
    NAUTOLAN("Nautolan"),
    ZABRAK("Zabrak"),
    THOLOTHIAN("Tholothian"),
    IKTOTCHI("Iktotchi"),
    QUERMIAN("Quermian"),
    KELDOR("Kel Dor"),
    CHAGRIAN("Chagrian"),
    GEONOSIAN("Geonosian"),
    MIRIALAN("Mirialan"),
    CLAWDITE("Clawdite"),
    BESALISK("Besalisk"),
    KAMINOAN("Kaminoan"),
    SKAKOAN("Skakoan"),
    MUUN("Muun"),
    TOGRUTA("Togruta"),
    KALEESH("Kaleesh"),
    PAUAN("Pau'an")
}


@Serializable
data class User (
    @BsonId val key: Id<User> = newId(),
    val name: SpeciesType,
    val email: String,
    val password: String,
    val species: String, // TODO: figure out how to correlate this only with the SpeciesType `species` values
    val bio: String
)