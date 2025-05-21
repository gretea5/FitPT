package com.ssafy.domain.usecase.schedule

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(
        memberId: Long?,
        startTime: String?,
        endTime: String?,
        scheduleContent: String?,
        trainerId: Long?
    ) : Flow<ResponseStatus<Long>> {
        return scheduleRepository.createSchedule(
            memberId = memberId,
            startTime = startTime,
            endTime = endTime,
            scheduleContent = scheduleContent,
            trainerId = trainerId
        )
    }
}