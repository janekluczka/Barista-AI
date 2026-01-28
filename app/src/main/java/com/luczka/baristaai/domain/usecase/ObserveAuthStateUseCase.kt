package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.model.AuthUser
import com.luczka.baristaai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> {
        return repository.observeAuthState()
    }
}
