package com.stanislavdumchykov.audioplayer.data.repository

import com.stanislavdumchykov.audioplayer.data.ContentResolverHelper
import com.stanislavdumchykov.audioplayer.data.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val contentResolverHelper: ContentResolverHelper
) {
    suspend fun getAudioData():List<Audio> = withContext(Dispatchers.IO){
        contentResolverHelper.getAudioData()
    }
}