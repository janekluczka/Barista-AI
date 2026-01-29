package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel
import com.luczka.baristaai.domain.repository.RecipeActionLogsRepository
import javax.inject.Inject

class CreateRecipeActionLogUseCase @Inject constructor(
    private val repository: RecipeActionLogsRepository
) {
    suspend operator fun invoke(
        input: CreateRecipeActionLogModel
    ): RepositoryResult<RecipeActionLogModel> {
        return repository.createRecipeActionLog(input)
    }
}
