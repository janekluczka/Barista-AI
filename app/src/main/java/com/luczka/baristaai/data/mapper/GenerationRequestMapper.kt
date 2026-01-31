package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.models.CreateGenerationRequestCommand
import com.luczka.baristaai.data.models.CreateGenerationRequestPayload
import com.luczka.baristaai.data.models.GenerationRequestDto
import com.luczka.baristaai.data.models.GenerateRecipesCommand
import com.luczka.baristaai.data.models.UpdateGenerationRequestCommand
import com.luczka.baristaai.domain.model.CreateGenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequest
import com.luczka.baristaai.domain.model.UpdateGenerationRequest

fun GenerationRequestDto.toDomain(): GenerationRequest {
    return GenerationRequest(
        id = id,
        userId = userId,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        canAdjustTemperature = canAdjustTemperature,
        userComment = userComment,
        createdAt = createdAt
    )
}

fun CreateGenerationRequest.toCommand(): CreateGenerationRequestCommand {
    return CreateGenerationRequestCommand(
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        canAdjustTemperature = canAdjustTemperature,
        userComment = userComment
    )
}

fun String.toGenerateRecipesCommand(): GenerateRecipesCommand {
    return GenerateRecipesCommand(
        generationRequestId = this
    )
}

fun CreateGenerationRequest.toPayload(userId: String): CreateGenerationRequestPayload {
    return CreateGenerationRequestPayload(
        userId = userId,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        canAdjustTemperature = canAdjustTemperature,
        userComment = userComment
    )
}

fun UpdateGenerationRequest.toCommand(): UpdateGenerationRequestCommand {
    return UpdateGenerationRequestCommand(
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        canAdjustTemperature = canAdjustTemperature,
        userComment = userComment
    )
}
