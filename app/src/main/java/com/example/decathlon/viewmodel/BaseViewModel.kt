package com.example.decathlon.viewmodel

import androidx.lifecycle.ViewModel
import com.example.decathlon.network.model.RepoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

open class BaseViewModel: ViewModel() {
    open fun <T> apiResponseCallback(response: Call<T>, responseListener: ResponseListener<T>) {
        response.enqueue(object : retrofit2.Callback<T> {
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                if(response.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        responseListener.onResponseReceived(RepoResult(data = response.body(), statusCode = response.code()))
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        responseListener.onResponseReceived(RepoResult(errorMessage = response.message(), statusCode = response.code()))
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                CoroutineScope(Dispatchers.IO).launch {
                    responseListener.onResponseReceived(RepoResult(failureMessage = t.message))
                }
            }
        })
    }

    interface ResponseListener <T> {
        suspend fun onResponseReceived(response: RepoResult<T>)
    }
}