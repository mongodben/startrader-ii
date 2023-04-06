package io.perlmutter.ben.models

import kotlinx.serialization.Serializable

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
data class Species(val species_type: String)
