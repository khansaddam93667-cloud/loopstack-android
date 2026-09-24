package com.loopstack.domain.usecase
import com.loopstack.domain.repository.ServerStatusRepository

import com.loopstack.domain.model.LoopbackStatus
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.flow.Flow

class MockServerStatusRepository(private val statusToReturn: LoopbackStatus) : ServerStatusRepository {
    override fun observeServerStatus(): Flow<LoopbackStatus> = flowOf(statusToReturn)
}

class ObserveLoopbackHealthUseCaseTest {

    @Test
    fun `health check returns Active when status is OK`() = runBlocking {
        val repo = MockServerStatusRepository(LoopbackStatus.Active)
        val useCase = ObserveLoopbackHealthUseCase(repo)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Active, status)
    }

    @Test
    fun `health check returns Degraded when status is not OK`() = runBlocking {
        val repo = MockServerStatusRepository(LoopbackStatus.Degraded)
        val useCase = ObserveLoopbackHealthUseCase(repo)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Degraded, status)
    }

    @Test
    fun `health check returns Inactive on exception`() = runBlocking {
        val repo = MockServerStatusRepository(LoopbackStatus.Inactive)
        val useCase = ObserveLoopbackHealthUseCase(repo)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Inactive, status)
    }
}
