package com.ssafy.domain.usecase.schedule

import com.ssafy.domain.repository.schedule.ScheduleRepository
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(scheduleId: Long) = scheduleRepository.deleteSchedule(scheduleId)
}