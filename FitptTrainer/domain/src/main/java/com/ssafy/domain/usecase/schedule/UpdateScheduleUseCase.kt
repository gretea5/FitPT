package com.ssafy.domain.usecase.schedule

import com.ssafy.domain.repository.schedule.ScheduleRepository
import javax.inject.Inject

class UpdateScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(
        scheduledId: Long,
        memberId: Long? = null,
        trainerId: Long? = null,
        startTime: String? = null,
        endTime: String? = null,
        scheduleContent: String? = null
    ) = scheduleRepository.updateSchedule(
        scheduledId = scheduledId,
        memberId = memberId,
        trainerId = trainerId,
        startTime = startTime,
        endTime = endTime,
        scheduleContent = scheduleContent
    )
}