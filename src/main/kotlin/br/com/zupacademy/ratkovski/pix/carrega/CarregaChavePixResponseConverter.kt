package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.AccountType
import br.com.zupacademy.ratkovski.DetailsKeyPixResponse
import br.com.zupacademy.ratkovski.KeyType
import com.google.protobuf.Timestamp
import java.time.ZoneId

class CarregaChavePixResponseConverter {

    fun convert(chaveInfo: ChavePixInfo): DetailsKeyPixResponse {
        return DetailsKeyPixResponse.newBuilder()
            .setClienteId(chaveInfo.clienteId?.toString() ?: "") // Protobuf usa "" como default value para String
            .setPixId(chaveInfo.pixId?.toString() ?: "") // Protobuf usa "" como default value para String
            .setChave(DetailsKeyPixResponse.ChavePix
                .newBuilder()
                .setTipo(KeyType.valueOf(chaveInfo.tipo.name))
                .setChave(chaveInfo.chave)
                .setConta(DetailsKeyPixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(AccountType.valueOf(chaveInfo.tipoDeConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setNomeDoTitular(chaveInfo.conta.nomeDoTitular)
                    .setCpfDoTitular(chaveInfo.conta.cpfDoTitular)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setNumeroDaConta(chaveInfo.conta.numeroDaConta)
                    .build()
                )
                .setCriadaEm(chaveInfo.registradaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
            )
            .build()
    }

}