package br.com.zupacademy.ratkovski.pix.registra

import br.com.zupacademy.ratkovski.*
import br.com.zupacademy.ratkovski.pix.handler.ErrorHandler

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@ErrorHandler
@Singleton

 class RegistraChaveEndpoint(@Inject private val service: NovaChavePixService)
    :PixKeyManagerServiceGrpc.PixKeyManagerServiceImplBase(){

    private val logger = LoggerFactory.getLogger(this.javaClass)

   // @Transactional
    override fun registra(
        request: RegisterKeyPixRequest,
        responseObserver: StreamObserver<RegisterKeyPixResponse>) {
        val novaChave = request.toModel()
        val chaveCriada = service.registra(novaChave)

       logger.info("Chave pix Cadastrada. ClienteId: ${chaveCriada.clienteId}, ChavePix: ${chaveCriada.pixId}")

        responseObserver.onNext(RegisterKeyPixResponse.newBuilder()
            .setClienteId(chaveCriada.clienteId.toString())
            .setPixId(chaveCriada.pixId.toString())
            .build())
        responseObserver.onCompleted()

    }

}

