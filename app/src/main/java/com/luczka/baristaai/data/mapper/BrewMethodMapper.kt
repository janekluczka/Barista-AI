package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.models.BrewMethodDto
import com.luczka.baristaai.domain.model.BrewMethod

fun BrewMethodDto.toDomain(): BrewMethod {
    return BrewMethod(
        id = id,
        name = name,
        slug = slug,
        createdAt = createdAt
    )
}
