package com.github.mcaustin.deck

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Card(
    val cardClass: String? = "UNKNOWN",
    val collectible: Boolean? = null,
    val dbfId: Int,
    val health: Int? = null,
    val id: String? = "UNKNOWN",
    val name: String? = "UNKNOWN",
    val rarity: String? = "UNKNOWN",
    val set: String? = "UNKNOWN",
    val type: String? = "UNKNOWN",
    val attack: Int? = null,
    val cost: Int? = null,
    val faction: String? = "UNKNOWN",
    val artist: String? = "UNKNOWN"
)

