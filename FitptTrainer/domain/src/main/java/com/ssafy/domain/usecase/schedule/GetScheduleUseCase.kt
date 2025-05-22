package com.ssafy.domain.usecase.schedule

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.domain.repository.schedule.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
){
    suspend operator fun invoke(
        date: String?,
        month: String?,
        trainerId: Long?,
        memberId: Long?
    ): Flow<ResponseStatus<List<Schedule>>> {
        return scheduleRepository.getSchedules(
            date = date,
            month = month,
            trainerId = trainerId,
            memberId = memberId
        )
    }
}