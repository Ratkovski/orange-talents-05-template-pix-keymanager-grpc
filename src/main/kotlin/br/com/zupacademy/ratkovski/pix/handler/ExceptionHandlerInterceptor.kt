package br.com.zupacademy.ratkovski.pix.handler
import br.com.zupacademy.ratkovski.pix.exception.ChavePixNaoEncontradaException
import br.com.zupacademy.ratkovski.pix.exception.ChavePixExistenteException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<Any, Any> {

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        return try {
            return context.proceed()
        } catch (ex: Exception) {

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            val status = when (ex) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(ex.message).withCause(ex.cause)
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(ex.message).withCause(ex.cause)
                is ChavePixExistenteException -> Status.ALREADY_EXISTS.withDescription(ex.message).withCause(ex.cause)
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(ex.message).withCause(ex.cause)
                is ChavePixNaoEncontradaException -> Status.NOT_FOUND.withDescription(ex.message).withCause(ex.cause)
                else -> Status.UNKNOWN.withDescription(ex.message).withCause(ex.cause)
            }

            responseObserver.onError(status.asRuntimeException())
        }
    }

}