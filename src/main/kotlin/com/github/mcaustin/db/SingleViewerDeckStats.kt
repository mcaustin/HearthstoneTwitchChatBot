package com.github.mcaustin.db

import kotlinx.serialization.Serializable

@Serializable
class SingleViewerDeckStats (
    var viewerId: String = "",
    var deckSubmissions: Int = 0,
    var lastDeck: String? = null
)
