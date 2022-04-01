package com.github.mcaustin.db

import kotlinx.serialization.Serializable

@Serializable
class GlobalViewerDeckStats (val cardSubmissionCount: Map<Int, Int> = mutableMapOf())



