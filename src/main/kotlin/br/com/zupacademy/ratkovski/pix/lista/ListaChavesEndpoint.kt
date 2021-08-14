package br.com.zupacademy.ratkovski.pix.lista

import br.com.zupacademy.ratkovski.*
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.handler.ErrorHandler
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton

class ListaChavesEndpoint (@Inject private val repository: ChavePixRepository):
    ListaPixKeyManagerServiceGrpc.ListaPixKeyManagerServiceImplBase(){

    override fun lista(request: ListaKeyPixRequest?,
                       responseObserver: StreamObserver<ListaKeyPixResponse>?
    ) {
        if(request?.clienteId.isNullOrBlank())throw IllegalArgumentException("Cliente ID não pode ser nulo")

        val clienteId =UUID.fromString(request?.clienteId)
        val chaves = repository.findAllByClienteId(clienteId).map {
            ListaKeyPixResponse.ChavePix.newBuilder()
                .setPixId(it.pixId.toString())
                .setTipo(KeyType.valueOf(it.tipo.name))
                .setChave(it.chave)
                .setTipoDeConta(AccountType.valueOf(it.tipoConta.name))
                .setCriadaEm(it.criadaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
        }
        responseObserver?.onNext(ListaKeyPixResponse.newBuilder()
            .setClienteId(clienteId.toString())
            .addAllChaves(chaves)
            .build())
        responseObserver?.onCompleted()


    }
}
