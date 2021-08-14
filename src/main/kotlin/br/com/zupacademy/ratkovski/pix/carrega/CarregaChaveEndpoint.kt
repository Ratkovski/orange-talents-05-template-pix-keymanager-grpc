package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.DetailsKeyPixRequest
import br.com.zupacademy.ratkovski.DetailsKeyPixResponse
import br.com.zupacademy.ratkovski.DetailsPixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.pix.handler.ErrorHandler
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton

class CarregaChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: ChavesDeClientesNoBcbClient,
    @Inject private val validator: Validator
):DetailsPixKeyManagerServiceGrpc.DetailsPixKeyManagerServiceImplBase() {

    override fun carrega(request: DetailsKeyPixRequest,
                         responseObserver: StreamObserver<DetailsKeyPixResponse>) {

        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(repository = repository, bcbClient = bcbClient)
        responseObserver.onNext(CarregaChavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()

    }

}
