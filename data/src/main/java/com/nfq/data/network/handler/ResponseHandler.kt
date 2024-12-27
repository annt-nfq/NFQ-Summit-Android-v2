package com.nfq.data.network.handler

import android.accounts.NetworkErrorException
import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.BaseAttendeeResponse
import com.nfq.data.remote.model.BaseResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import org.json.JSONObject
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

typealias ApiCall<T> = suspend () -> Response<T>

const val ERROR_MESSAGE_GENERAL = "Something went wrong. Please try again."
const val ERROR_JSON_CONVERSION = "Error json conversion. Please try again."
const val ERROR_TITLE_GENERAL = "Error"
const val ERROR_DATA_NOT_FOUNT = "Data not fount. Please try again."

suspend fun <T, R> handleCall(
    apiCall: ApiCall<BaseResponse<T>>,
    mapper: suspend (T, String?) -> R,
    onDataNull: (BaseResponse<T>?) -> Either<DataException, R> = {
        Either.Left(
            DataException.Api(
                title = ERROR_TITLE_GENERAL,
                message = it?.message ?: ERROR_MESSAGE_GENERAL,
                errorCode = -1
            )
        )
    }
): Either<DataException, R> = try {
    apiCall().let {
        val body = it.body()
        when {
            body?.success == true -> {
                if (body.data != null) {
                    Either.Right(
                        mapper(
                            body.data,
                            body.message
                        )
                    )
                } else {
                    onDataNull(body)
                }
            }

            else -> errorResponseHandler(it)
        }
    }
} catch (e: Exception) {
    when (e) {
        is NetworkErrorException, is UnknownHostException -> Either.Left(DataException.Network)
        is SerializationException -> {
            Either.Left(
                DataException.Api(
                    message = e.message ?: ERROR_JSON_CONVERSION,
                    title = ERROR_TITLE_GENERAL,
                    errorCode = -1
                )
            )
        }

        else -> Either.Left(
            DataException.Api(
                message = e.message ?: ERROR_MESSAGE_GENERAL,
                title = ERROR_TITLE_GENERAL,
                errorCode = -1
            )
        )
    }
}


suspend fun <T, R> handleCall(
    apiCall: ApiCall<BaseAttendeeResponse<T>>,
    mapper: suspend (T, String?, String?) -> R,
    onDataNull: (BaseAttendeeResponse<T>?) -> Either<DataException, R> = {
        Either.Left(
            DataException.Api(
                title = ERROR_TITLE_GENERAL,
                message = it?.message ?: ERROR_MESSAGE_GENERAL,
                errorCode = -1
            )
        )
    }
): Either<DataException, R> = try {
    apiCall().let {
        val body = it.body()
        when {
            body?.success == true -> {
                if (body.data != null) {
                    Either.Right(
                        mapper(
                            body.data,
                            body.message,
                            body.token
                        )
                    )
                } else {
                    onDataNull(body)
                }
            }

            else -> errorResponseHandler(it)
        }
    }
} catch (e: Exception) {
    when (e) {
        is NetworkErrorException, is UnknownHostException -> Either.Left(DataException.Network)
        is SerializationException -> {
            Either.Left(
                DataException.Api(
                    message = e.message ?: ERROR_JSON_CONVERSION,
                    title = ERROR_TITLE_GENERAL,
                    errorCode = -1
                )
            )
        }

        else -> Either.Left(
            DataException.Api(
                message = e.message ?: ERROR_MESSAGE_GENERAL,
                title = ERROR_TITLE_GENERAL,
                errorCode = -1
            )
        )
    }
}

suspend fun <T, R> errorResponseHandler(
    it: Response<T>,
): Either<DataException, R> {

    val jsonObject: JSONObject? = try {
        suspendCancellableCoroutine { cont ->
            runCatching {
                cont.resume(it.errorBody()?.string())
            }.recover { throwable ->
                cont.resumeWithException(throwable)
            }
        }?.let { JSONObject(it) }
    } catch (e: Exception) {
        null
    }


    val errorMessage = try {
        jsonObject?.getString("message")
    } catch (e: Exception) {
        null
    }

    return Either.Left(
        DataException.Api(
            message = errorMessage ?: ERROR_MESSAGE_GENERAL,
            title = ERROR_TITLE_GENERAL,
            errorCode = it.code()
        )
    )
}

