package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMethod

abstract class AbstractController<T>(clazz: Class<T>) {

    private val log: Logger = LoggerFactory.getLogger(clazz)

    protected fun <B> reportRequest(
        method: RequestMethod,
        path: String,
        pathVariables: HashMap<String, String> = hashMapOf(),
        requestParams: HashMap<String, String?> = hashMapOf(),
        body: B?
    ) = log.info(
        "path: [${buildPath(path, pathVariables)}] method: [${method.name}] " +
                "requestParam: ${buildParam(requestParams)} " +
                buildRequestBody(body)
    )

    private fun buildPath(path: String, mapParam: HashMap<String, String>): String {
        var fullPath = path

        mapParam.forEach { (k, v) ->
            fullPath = fullPath.replace("{$k}", v)
        }

        return fullPath
    }

    private fun buildParam(mapParam: HashMap<String, String?>): String =
        if (mapParam.isEmpty() || mapParam.all { it.value.isNullOrBlank() }) {
            "EMPTY"
        } else {
            mapParam
                .filterNot { it.value.isNullOrBlank() }
                .map { it.key + "=" + it.value }
                .joinToString(
                    prefix = "[",
                    separator = ", ",
                    postfix = "]",
                )
        }

    private fun <B> buildRequestBody(body: B?): String =
        "requestBody: " +
                if (body != null) {
                    "[ " + Gson().toJson(body) + " ]"
                } else {
                    "EMPTY"
                }

    protected fun <R> reportResponse(status: HttpStatus, response: R? = null): ResponseEntity<R> {
        log.info("status: [${status.value()}] ${buildResponse(response)}")
        return ResponseEntity.status(status).body(response)
    }

    private fun <R> buildResponse(response: R? = null): String =
        "response: " +
                if (response != null) {
                    "[ " + Gson().toJson(response) + " ]"
                } else {
                    "EMPTY"
                }

    protected fun <R> reportError(e: RuntimeException, status: HttpStatus): ResponseEntity<R> {
        log.error("status: [${status.value()}] errorMessage: [ " + e.message + " ]", e)
        return ResponseEntity.status(status).build()
    }

}