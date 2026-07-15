package com.example.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.usecase.article.UpdateArticlesForAllTopicsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val updateArticlesForAllTopicsUseCase: UpdateArticlesForAllTopicsUseCase
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        updateArticlesForAllTopicsUseCase()

        return Result.success()
    }
}