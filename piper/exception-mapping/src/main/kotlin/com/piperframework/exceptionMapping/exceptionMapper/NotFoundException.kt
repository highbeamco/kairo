package com.piperframework.exceptionMapping.exceptionMapper

import com.piperframework.error.error.NotFoundError
import com.piperframework.exception.exception.notFound.NotFoundException
import com.piperframework.exceptionMapping.ExceptionMapper
import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

internal class NotFoundException : ExceptionMapper<NotFoundException>() {
    override suspend fun PipelineContext<Unit, ApplicationCall>.handle(e: NotFoundException) = NotFoundError()
}
