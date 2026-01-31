package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<SessionStatus> {
        return repository.observeAuthState()
    }
}
