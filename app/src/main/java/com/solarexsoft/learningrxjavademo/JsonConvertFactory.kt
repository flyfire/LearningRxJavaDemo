/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.solarexsoft.learningrxjavademo

import android.util.Log
import androidx.annotation.Keep
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.reflect.Type

/**
 * Json 解析城 JsonResponse 和 Response<T> 两种形式
 */
class JsonConvertFactory private constructor(mapper: ObjectMapper) : Converter.Factory() {

    companion object {
        fun create() = JsonConvertFactory(
                JacksonMapper.json()
                        .failOnUnknownProperties(false)
                        .allowUnquotedControlChars(true)
                        .build()
                        .objectMapper
        )
    }

    private val defaultConverter = JacksonConverterFactory.create(mapper)

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return defaultConverter.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        /*
        return when (type) {
            JsonResponse::class.java -> JsonResponseBodyConverter()
            else -> defaultConverter.responseBodyConverter(type, annotations, retrofit)
        }
         */
        return null
    }

}

class JsonResponseBodyConverter : Converter<ResponseBody, JsonResponse> {
    override fun convert(value: ResponseBody): JsonResponse {
        try {
            val content = IOUtils.toString(value.byteStream(), Charsets.UTF_8)
            val json = JSONObject(content)
            val errorNode = json.getJSONObject("error")
            val code = errorNode.optInt("code")
            val message = errorNode.optString("message")
            val data = json.optJSONObject("data")?: JSONObject()
            return JsonResponse(Error(code, message), data)
        } catch (e: JSONException) {
            Log.e("JsonRespBodyConverter", "Failed to convert json")
        } finally {
            IOUtils.closeQuietly(value)
        }
        return JsonResponse(Error(ServerException.CODE_JACKSON, "服务器出错，请稍后重试"))
    }
}

@Keep
data class Error(val code: Int = 0, val message: String = "")

@Keep
data class JsonResponse(val error: Error = Error(), val data: JSONObject? = null)

@Keep
data class Response<out T>(val error: Error = Error(), val data: T? = null)

class ServerException(val code: Int, message: String, cause: Throwable? = null) : Exception(message, cause) {

    companion object {
        const val CODE_OFFLINE = -1000
        const val CODE_JACKSON = -1002
        const val CODE_NOT_LOGIN = 1
    }
}