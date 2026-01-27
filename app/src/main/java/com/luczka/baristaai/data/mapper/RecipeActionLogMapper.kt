package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.models.CreateRecipeActionLogCommand
import com.luczka.baristaai.data.models.CreateRecipeActionLogPayload
import com.luczka.baristaai.data.models.RecipeAction as RecipeActionDto
import com.luczka.baristaai.data.models.RecipeActionLogDto
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionModel

fun RecipeActionLogDto.toDomain(): RecipeActionLogModel {
    return RecipeActionLogModel(
        id = id,
        userId = userId,
        recipeId = recipeId,
        generationRequestId = generationRequestId,
        action = action.toDomain(),
        createdAt = createdAt
    )
}

fun CreateRecipeActionLogModel.toCommand(): CreateRecipeActionLogCommand {
    return CreateRecipeActionLogCommand(
        recipeId = recipeId,
        generationRequestId = generationRequestId,
        action = action.toDto()
    )
}

fun CreateRecipeActionLogModel.toPayload(userId: String): CreateRecipeActionLogPayload {
    return CreateRecipeActionLogPayload(
        userId = userId,
        recipeId = recipeId,
        generationRequestId = generationRequestId,
        action = action.toDto()
    )
}

private fun RecipeActionDto.toDomain(): RecipeActionModel {
    return when (this) {
        RecipeActionDto.Accepted -> RecipeActionModel.Accepted
        RecipeActionDto.Edited -> RecipeActionModel.Edited
        RecipeActionDto.Rejected -> RecipeActionModel.Rejected
    }
}

fun RecipeActionModel.toDto(): RecipeActionDto {
    return when (this) {
        RecipeActionModel.Accepted -> RecipeActionDto.Accepted
        RecipeActionModel.Edited -> RecipeActionDto.Edited
        RecipeActionModel.Rejected -> RecipeActionDto.Rejected
    }
}
