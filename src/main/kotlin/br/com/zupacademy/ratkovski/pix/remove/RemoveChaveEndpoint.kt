package br.com.zupacademy.ratkovski.pix.remove

import br.com.zupacademy.ratkovski.RemoveKeyPixRequest
import br.com.zupacademy.ratkovski.RemoveKeyPixResponse
import br.com.zupacademy.ratkovski.RemovePixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.pix.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveEndpoint(@Inject private val service:RemovePixKeyManagerService)
    :RemovePixKeyManagerServiceGrpc.RemovePixKeyManagerServiceImplBase() {

    override fun remove(request: RemoveKeyPixRequest,
                        responseObserver: StreamObserver<RemoveKeyPixResponse>) {
        service.remove(clienteId = request.clienteId,pixId=request.pixId)

        responseObserver.onNext(RemoveKeyPixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)

            .build())
        responseObserver.onCompleted()

    }
}