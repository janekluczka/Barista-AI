package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.models.CreateRecipeCommand
import com.luczka.baristaai.data.models.CreateRecipePayload
import com.luczka.baristaai.data.models.FindSimilarRecipesCommand
import com.luczka.baristaai.data.models.RecipeDto
import com.luczka.baristaai.data.models.RecipeStatus as RecipeStatusDto
import com.luczka.baristaai.data.models.UpdateRecipeCommand
import com.luczka.baristaai.domain.model.CreateRecipe
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.SimilarRecipeQuery
import com.luczka.baristaai.domain.model.UpdateRecipe

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        userId = userId,
        generationRequestId = generationRequestId,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        waterAmount = waterAmount,
        ratioCoffee = ratioCoffee,
        ratioWater = ratioWater,
        temperature = temperature,
        assistantTip = assistantTip,
        status = status.toDomain(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun CreateRecipe.toCommand(): CreateRecipeCommand {
    return CreateRecipeCommand(
        generationRequestId = generationRequestId,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        waterAmount = waterAmount,
        ratioCoffee = ratioCoffee,
        ratioWater = ratioWater,
        temperature = temperature,
        assistantTip = assistantTip,
        status = status.toDto()
    )
}

fun CreateRecipe.toPayload(userId: String): CreateRecipePayload {
    return CreateRecipePayload(
        userId = userId,
        generationRequestId = generationRequestId,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        waterAmount = waterAmount,
        ratioCoffee = ratioCoffee,
        ratioWater = ratioWater,
        temperature = temperature,
        assistantTip = assistantTip,
        status = status.toDto()
    )
}

fun UpdateRecipe.toCommand(id: String): UpdateRecipeCommand {
    return UpdateRecipeCommand(
        id = id,
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        waterAmount = waterAmount,
        ratioCoffee = ratioCoffee,
        ratioWater = ratioWater,
        temperature = temperature,
        assistantTip = assistantTip,
        status = status?.toDto()
    )
}

fun SimilarRecipeQuery.toCommand(): FindSimilarRecipesCommand {
    return FindSimilarRecipesCommand(
        brewMethodId = brewMethodId,
        coffeeAmount = coffeeAmount,
        ratioCoffee = ratioCoffee,
        ratioWater = ratioWater,
        temperature = temperature
    )
}

private fun RecipeStatusDto.toDomain(): RecipeStatus {
    return when (this) {
        RecipeStatusDto.Draft -> RecipeStatus.Draft
        RecipeStatusDto.Saved -> RecipeStatus.Saved
        RecipeStatusDto.Edited -> RecipeStatus.Edited
        RecipeStatusDto.Rejected -> RecipeStatus.Rejected
        RecipeStatusDto.Deleted -> RecipeStatus.Deleted
    }
}

fun RecipeStatus.toDto(): RecipeStatusDto {
    return when (this) {
        RecipeStatus.Draft -> RecipeStatusDto.Draft
        RecipeStatus.Saved -> RecipeStatusDto.Saved
        RecipeStatus.Edited -> RecipeStatusDto.Edited
        RecipeStatus.Rejected -> RecipeStatusDto.Rejected
        RecipeStatus.Deleted -> RecipeStatusDto.Deleted
    }
}
