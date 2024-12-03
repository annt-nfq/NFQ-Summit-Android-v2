package com.nfq.data.network.handler

import android.accounts.NetworkErrorException
import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.BaseResponse
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import retrofit2.Response

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

            else -> Either.Left(
                DataException.Api(
                    title = ERROR_TITLE_GENERAL,
                    message = ERROR_DATA_NOT_FOUNT,
                    errorCode = -1
                )
            )
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


