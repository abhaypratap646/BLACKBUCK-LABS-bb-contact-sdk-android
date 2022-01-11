package com.example.myapplication.datalayer

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {
    private var instance: OkHttpClient? = null

    fun get(): OkHttpClient {
        return instance ?: synchronized(this) {
            val loggingInterceptor = HttpLoggingInterceptor()
//            if (BuildConfig.DEBUG) {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//            } else {
//                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
//            }
            val builder = OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                val headers = originalRequest.headers
                val headersBuilder: Headers.Builder =
                    RequestHeader.get()
                for (name in headers.names()) {
                    if (headersBuilder[name] == null) {
                        headers[name]?.let { headersBuilder.add(name, it) }
                    } else {
                        headersBuilder[name] = headers[name].toString()
                    }
                }
                val authorizedRequest = originalRequest.newBuilder()
                    .headers(headersBuilder.build())
                    .build()
                chain.proceed(authorizedRequest)
            }
//            if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(StethoInterceptor()).build()
//            }
//            if (BuildConfig.DEBUG && !ActivityUtils.isThisMimicApp()) {
//                builder.addInterceptor(MockingInterceptor())
//            }
//            if (ActivityUtils.isThisMimicApp()) {
//                builder.addInterceptor(BlockingInterceptor())
//            }
            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//            } else {
//                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
//            }
            val temp = builder.build()
            instance = temp
            temp
        }
    }

    /**
     * Blocks API requests
     * e.g. Used for Mimic App to block POST requests
     */
//    class BlockingInterceptor : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain): Response {
//            val request = chain.request()
//            return if (Util.isRestrictedApi(request.url().url().toString())) {
//                // Prepare the builder with common stuff.
//                val mOkHttpResponseBuilder = Response.Builder()
//                    .request(request)
//                    .protocol(Protocol.HTTP_1_1)
//                    .code(403)
//                val mediaType = MediaType.parse("application/json")
//                val reponsebody = ResponseBody.create(mediaType, "dummy content")
//                mOkHttpResponseBuilder
//                    .addHeader("Content-Type", "application/json; charset=utf-8")
//                    .message("Forbidden Action!!")
//                    .body(reponsebody)
//                    .build()
//            } else {
//                chain.proceed(request)
//            }
//        }
//    }

    /**
     * Mocks API requests
     * Used to mock API response by adding json here.
     * Uncomment the code to use this for debug builds
     * Add your mock json in place of 'add mock json' below
     */
//    class MockingInterceptor : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain): Response {
//            val request = chain.request()
//            //            if (request.url().url().toString().equals("https://api-systemtest.blackbuck.com/demand-wrapper/v1/kyc/status/799524")) {
////                // Prepare the builder with common stuff.
////                Response.Builder mOkHttpResponseBuilder = new Response.Builder()
////                        .request(request)
////                        .protocol(Protocol.HTTP_1_1)
////                        .code(200);
////
////                MediaType mediaType = MediaType.parse("application/json");
////                ResponseBody reponsebody = ResponseBody.create(mediaType, "{\"status\":0,\"status_code\":null,\"message\":\"Response Received\",\"result\":{\"customer_id\":1001,\"customer_verification_status\":\"MCQ_REQUIRED\",\"customer_status_card\":{\"heading1\":\"test1\",\"heading2\":\"test2\",\"heading3\":\"test3\",\"button1\":\"click on\"},\"ikyc_details\":{\"individual_verification_status\":\"VERIFIED\",\"recent_address_proof\":{\"document_type\":\"AADHAR_CARD\",\"document_sides\":[{\"status\":\"VERIFIED\",\"side\":\"FRONT\",\"url\":\"http://ladasinghasan.weebly.com/uploads/8/0/3/8/8038657/pan_card.jpg\",\"message\":null},{\"status\":\"VERIFIED\",\"side\":\"BACK\",\"url\":\"http://ladasinghasan.weebly.com/uploads/8/0/3/8/8038657/pan_card.jpg\",\"message\":null}]},\"personal_document_status\":[{\"document_type\":\"PAN_CARD\",\"document_sides\":[{\"status\":\"VERIFIED\",\"side\":\"BACK\",\"Url\":\"http://ladasinghasan.weebly.com/uploads/8/0/3/8/8038657/pan_card.jpg\",\"message\":null},{\"status\":\"VERIFIED\",\"side\":\"BACK\",\"url\":\"http://ladasinghasan.weebly.com/uploads/8/0/3/8/8038657/pan_card.jpg\",\"message\":null}]},{\"document_type\":null,\"document_sides\":[{\"status\":null,\"side\":null,\"url\":null,\"message\":null},{\"status\":null,\"side\":null,\"url\":null,\"message\":null}]}]},\"bkyc_details\":{\"business_verification_status\":\"VERIFIED\",\"business_document_status\":[{\"document_type\":null,\"document_sides\":[{\"status\":null,\"side\":null,\"url\":null,\"message\":null},{\"status\":null,\"side\":null,\"url\":null,\"message\":null}]},{\"document_type\":null,\"document_sides\":[{\"status\":null,\"side\":null,\"url\":null,\"message\":null},{\"status\":null,\"side\":null,\"url\":null,\"message\":null}]}]}}}");
////
////                Response okHttpResponse = mOkHttpResponseBuilder
////                        .addHeader("Content-Type", "application/json; charset=utf-8")
////                        .body(reponsebody)
////                        .message("Okay")
////                        .build();
////                return okHttpResponse;
////            } else {
//            return chain.proceed(request)
//            //}
//        }
//    }
}