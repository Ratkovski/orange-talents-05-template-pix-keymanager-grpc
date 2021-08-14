package br.com.zupacademy.ratkovski.pix.remove

import br.com.zupacademy.ratkovski.KeyType
import br.com.zupacademy.ratkovski.RegisterKeyPixRequest
import br.com.zupacademy.ratkovski.RemoveKeyPixRequest
import br.com.zupacademy.ratkovski.RemovePixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.registra.externo.DeletePixKeyRequest
import br.com.zupacademy.ratkovski.pix.registra.externo.DeletePixKeyResponse
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime

import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(val repository: ChavePixRepository,
val grpcClient: RemovePixKeyManagerServiceGrpc.RemovePixKeyManagerServiceBlockingStub) {

    lateinit var CHAVE_EXISTENTE: ChavePix

    @Inject
    lateinit var bcbClient: ChavesDeClientesNoBcbClient

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(
            chave(
                tipo = TipoChave.EMAIL,
                chave = "ratkovski@gmail.com",
                clienteId = UUID.randomUUID()
            )
        )
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover uma chave pix existente`() {
        //cenario
        `when`(bcbClient.delete("ratkovski@gmail.com", DeletePixKeyRequest("ratkovski@gmail.com")))
            .thenReturn(
                HttpResponse.ok(
                    DeletePixKeyResponse(
                        key = "ratkovski@gmail.com",
                        participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                        deletedAt = LocalDateTime.now()
                    )
                )
            )


        //acao
        val response = grpcClient.remove(
            RemoveKeyPixRequest.newBuilder()
                .setPixId(CHAVE_EXISTENTE.pixId.toString())
                .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                .build()
        )

        //validacao
        with(response) {
            assertEquals(CHAVE_EXISTENTE.clienteId.toString(), clienteId)
            assertEquals(CHAVE_EXISTENTE.pixId.toString(), pixId)
        }
    }

    @Test
    fun `nao deve remover uma chave pix existente quando ocorrer algum erro no BCB`() {


        //cenario
        `when`(bcbClient.delete("ratkovski@gmail.com", DeletePixKeyRequest("ratkovski@gmail.com")))
            .thenReturn(HttpResponse.unprocessableEntity())

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveKeyPixRequest.newBuilder()
                    .setPixId(CHAVE_EXISTENTE.pixId.toString())
                    .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                    .build()
            )
        }

        //validacao
        with(thrown) {
           // assertTrue(repository.existsByChave(chave = null))
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao remover chave pix no BCB", status.description)
        }
    }


    @Test
    fun `nao deve excluir chave pix quando esta for inexistente no sistema`() {
        //cenario
        val chaveInexistente: UUID = UUID.randomUUID()

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveKeyPixRequest.newBuilder()
                    .setPixId(chaveInexistente.toString())
                    .setClienteId(chaveInexistente.toString())
                    .build()
            )
        }

        //validacao
        with(throws) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada no sistema para este cliente", status.description)

        }
    }

    @Test
    fun `nao deve remover chave quando a chave pertencer a outro cliente`() {
        //cenario
        val clienteDiferente: UUID = UUID.randomUUID()

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveKeyPixRequest.newBuilder()
                    .setPixId(CHAVE_EXISTENTE.pixId.toString())
                    .setClienteId(clienteDiferente.toString())
                    .build()
            )
        }

        //validacao
        with(throws) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada no sistema para este cliente", status.description)

        }
    }

    @MockBean(ChavesDeClientesNoBcbClient::class)
    fun bcbClient(): ChavesDeClientesNoBcbClient? {
        return mock(ChavesDeClientesNoBcbClient::class.java)
    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemovePixKeyManagerServiceGrpc.RemovePixKeyManagerServiceBlockingStub {
            return RemovePixKeyManagerServiceGrpc.newBlockingStub(channel)
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

}