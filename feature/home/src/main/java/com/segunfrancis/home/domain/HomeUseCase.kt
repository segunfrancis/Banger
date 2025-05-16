package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotoOrientation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class HomeUseCase(private val homeRepository: HomeRepository, private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(): List<Result<Pair<String, List<HomePhoto>>>> {
        return supervisorScope {
            val popularDef = async(dispatcher) {
                homeRepository.getRandomPhotos(
                    orientation = PhotoOrientation.Portrait,
                    query = "popular"
                )
            }
            val natureDef = async(dispatcher) {
                homeRepository.getRandomPhotos(
                    orientation = PhotoOrientation.Portrait,
                    query = "nature"
                )
            }
            val randomDef = async(dispatcher) {
                homeRepository.getRandomPhotos(
                    orientation = PhotoOrientation.Portrait,
                    query = "random"
                )
            }
            listOf(popularDef, natureDef, randomDef).awaitAll()
        }
    }
}
