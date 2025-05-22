package com.ssafy.domain.usecase.home

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.home.ScheduleInfo
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.repository.home.ScheduleRepository
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetScheduleUsecase @Inject constructor(private val scheduleRepository: ScheduleRepository){
    suspend operator fun invoke(date: String,month: String): Flow<ResponseStatus<List<ScheduleInfo>>> {
        return scheduleRepository.getSchedules(date,month)
    }
}