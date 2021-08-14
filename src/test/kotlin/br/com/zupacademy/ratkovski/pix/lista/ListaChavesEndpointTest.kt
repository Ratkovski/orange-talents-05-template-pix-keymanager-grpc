package br.com.zupacademy.ratkovski.pix.lista

import br.com.zupacademy.ratkovski.DetailsPixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.KeyType
import br.com.zupacademy.ratkovski.ListaKeyPixRequest
import br.com.zupacademy.ratkovski.ListaPixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.pix.carrega.CarregaChaveEndpointTest
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ListaChavesEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: ListaPixKeyManagerServiceGrpc.ListaPixKeyManagerServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: ChavesDeClientesNoBcbClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }


    @BeforeEach
    fun setUp() {
        repository.save(chave(tipo = TipoChave.EMAIL, chave = "ratkovski@email.com", clienteId = CLIENTE_ID))
        repository.save(chave(tipo = TipoChave.RANDOM, chave = "randomkey-2", clienteId = UUID.randomUUID()))
        repository.save(chave(tipo = TipoChave.RANDOM, chave = "randomkey-3", clienteId = CLIENTE_ID))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar todas as chaves do cliente`(){
        //cenário
        val clienteId = CLIENTE_ID.toString()
        //ação
        val response = grpcClient.lista(ListaKeyPixRequest.newBuilder()
            .setClienteId(clienteId)
            .build())
        //validação
        with(response.chavesList){
            assertThat(this,hasSize(2))
            assertThat(this.map { Pair(it.tipo, it.chave) }.toList(), containsInAnyOrder(
                Pair(KeyType.RANDOM,"randomkey-3"),
                Pair(KeyType.EMAIL,"ratkovski@email.com")
            ))

        }
    }
    @Test
    fun `nao deve listar todas as chaves do cliente quando cliente nao possuir chave`(){
        //cenário
val clienteSemChave = UUID.randomUUID().toString()
        //ação

        val response = grpcClient.lista(ListaKeyPixRequest.newBuilder()
            .setClienteId(clienteSemChave)
            .build())
        //validação
        assertEquals(0,response.chavesCount)
    }
    @Test
    fun `nao deve listar todas as chaves do cliente quando clienteId for invalido`(){
        val clienteInvalido = ""
        //ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.lista(
                ListaKeyPixRequest.newBuilder()
                    .setClienteId(clienteInvalido)
                    .build()
            )
        }
        //validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("Cliente ID não pode ser nulo",status.description)
        }
    }



    @Factory
    class Clients {
        @Bean
        fun protobufStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListaPixKeyManagerServiceGrpc.ListaPixKeyManagerServiceBlockingStub {
            return ListaPixKeyManagerServiceGrpc.newBlockingStub(channel)
        }
    }
}
fun chave(
    tipo: TipoChave,
    chave: String = UUID.randomUUID().toString(),
    clienteId: UUID = UUID.randomUUID(),
): ChavePix {
    return ChavePix(
        clienteId = clienteId,
        tipo = tipo,
        chave = chave,
        tipoConta = TipoConta.CONTA_CORRENTE,
        conta = ContaAssociada(
            instituicao = "UNIBANCO ITAU",
            nomeDoTitular = "Ratkovski",
            cpfDoTitular = "63657520325",
            agencia = "1218",
            numeroDaConta = "291900"
        )
    )
}
