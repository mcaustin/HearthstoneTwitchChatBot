package com.github.mcaustin.db

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class ViewerDeckRequest(
    var partitionKey: String = UUID.randomUUID().toString(),

    var viewerId: String = "",

    var deckCode: String? = null,

    var submissionDate: String? = Instant.now().toString(),

    var heroClass: String? = "",

    var tags: Set<String>? = null,

    var format: String? = ""
)
