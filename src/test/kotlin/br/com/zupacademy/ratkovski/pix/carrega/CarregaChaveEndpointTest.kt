package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.DetailsKeyPixRequest
import br.com.zupacademy.ratkovski.DetailsKeyPixRequest.FiltroPorPixId
import br.com.zupacademy.ratkovski.DetailsPixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.pix.grpcenum.PixKeyType
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.registra.externo.*
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import br.com.zupacademy.ratkovski.utils.violations
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.rnorth.visibleassertions.VisibleAssertions.assertThat
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class CarregaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: DetailsPixKeyManagerServiceGrpc.DetailsPixKeyManagerServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: ChavesDeClientesNoBcbClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }


    @BeforeEach
    fun setUp() {
        repository.save(chave(tipo = TipoChave.EMAIL, chave = "ratkovski@email.com", clienteId = CLIENTE_ID))
        repository.save(chave(tipo = TipoChave.CPF, chave = "12345678901", clienteId = UUID.randomUUID()))
        repository.save(chave(tipo = TipoChave.PHONE, chave = "+554755554321", clienteId = CLIENTE_ID))
        repository.save(chave(tipo = TipoChave.RANDOM, chave = "randomkey-3", clienteId = CLIENTE_ID))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve carregar chave por pixId e clienteId`() {
        //cenário]
        val chaveExistente = repository.findByChave("+554755554321").get()
        //ação
        val response = grpcClient.carrega(
            DetailsKeyPixRequest.newBuilder()
                .setPixId(
                    FiltroPorPixId.newBuilder()
                        .setPixId(chaveExistente.pixId.toString())
                        .setClienteId(chaveExistente.clienteId.toString())
                        .build()
                )
                .build()
        )

        //validação
        with(response) {
            assertEquals(chaveExistente.pixId.toString(), pixId)
            assertEquals(chaveExistente.clienteId.toString(), clienteId)
            assertEquals(chaveExistente.tipo.name, chave.tipo.name)
            assertEquals(chaveExistente.chave, chave.chave)
        }
    }

    @Test
    fun `nao deve carregar chave por pixId e clienteId quando filtro for invalido`() {
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                DetailsKeyPixRequest.newBuilder()
                    .setPixId(
                        FiltroPorPixId.newBuilder()
                            .setPixId("")
                            .setClienteId("")
                            .build()
                    )
                    .build()
            )
        }
        //validações
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
//            assertEquals("Dados inválidos",status.description)
//            assertThat(violations(), containsInAnyOrder(
//                Pair("pixId", "must not be blank"),
//                Pair("clienteId", "must not be blank"),
//                Pair("pixId", "não é um formato válido de UUID"),
//                Pair("clienteId", "não é um formato válido de UUID"),
//            ))
        }


    }

    @Test
    fun `nao deve carregar chave por pixId e clienteId quando registro nao existir`() {
        //cenário
        val pixIdNaoExistente = UUID.randomUUID().toString()
        val clienteIdNaoExistente = UUID.randomUUID().toString()

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                DetailsKeyPixRequest.newBuilder()
                    .setPixId(
                        FiltroPorPixId.newBuilder()
                            .setPixId(pixIdNaoExistente)
                            .setClienteId(clienteIdNaoExistente)
                            .build()
                    )
                    .build()
            )
        }
        //validações
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)

        }

    }

    @Test
    fun ` deve carregar chave por pixId e clienteId quando registro existir localmente`() {
        //cenário
        val chaveExistente = repository.findByChave("ratkovski@email.com").get()
        //ação
        val response = grpcClient.carrega(
            DetailsKeyPixRequest.newBuilder()
                .setChave("ratkovski@email.com")
                .build())

        //validação
        with(response) {
            assertEquals(chaveExistente.pixId.toString(), pixId)
            assertEquals(chaveExistente.clienteId.toString(), clienteId)
            assertEquals(chaveExistente.tipo.name, chave.tipo.name)
            assertEquals(chaveExistente.chave, chave.chave)
        }
    }

    @Test
    fun ` deve carregar chave por valor da chave quando registro nao existir localmente mas existir noBCB`() {
        //cenário
        val bcbResponse = detailsPixKeyResponse()
        `when`(bcbClient.findByKey(key = "ratkovski.another@email.com"))
            .thenReturn(HttpResponse.ok(detailsPixKeyResponse()))
        //ação
        val response=grpcClient.carrega(DetailsKeyPixRequest.newBuilder()
            .setChave("ratkovski.another@email.com")
            .build())

        //validação
        with(response){
            assertEquals("",pixId)
            assertEquals("",clienteId)
            assertEquals(bcbResponse.keyType.name,chave.tipo.name)
            assertEquals(bcbResponse.key, chave.chave)

        }

    }

    @Test
    fun ` nao deve carregar chave por valor da chave quando registro nao existir localmente nem no noBCB`() {
        //cenário
        val bcbResponse = detailsPixKeyResponse()
        `when`(bcbClient.findByKey(key = "not.exits.ratkovski@email.com"))
            .thenReturn(HttpResponse.notFound())
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                DetailsKeyPixRequest.newBuilder()
                    .setChave("not.exits.ratkovski@email.com")
                    .build()
            )
        }
        //validação
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Chave Pix não encontrada",status.description)


        }

    }

    @Test
    fun ` nao deve carregar chave por valor da chave quando filtro for invalido`() {
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                DetailsKeyPixRequest.newBuilder()
                    .setChave("")
                    .build()
            )
        }
        //validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
//            assertEquals(message,status.description)
//           assertThat(violations(), containsInAnyOrder(
//                Pair("chave", "must not be blank")
//           ))

        }

    }

    @Test
    fun ` nao deve carregar chave quando filtro for invalido`() {
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                DetailsKeyPixRequest.newBuilder()
                    .setChave("")
                    .build()
            )
        }
        //validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave: não deve estar em branco",status.description)
        }

    }



    @MockBean(ChavesDeClientesNoBcbClient::class)
    fun bcbClient(): ChavesDeClientesNoBcbClient? {
        return mock(ChavesDeClientesNoBcbClient::class.java)
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): DetailsPixKeyManagerServiceGrpc.DetailsPixKeyManagerServiceBlockingStub {
            return DetailsPixKeyManagerServiceGrpc.newBlockingStub(channel)
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


    private fun detailsPixKeyResponse(): DetailsPixKeyResponse {
        return DetailsPixKeyResponse(
            keyType = PixKeyType.EMAIL,
            key = "ratkovski.another@email.com",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now()

        )

    }

    private fun bankAccount(): BankAccount {
        return BankAccount(
            participant = "90400888",
            branch = "9871",
            accountNumber = "987654",
            accountType = PixAccountType.SVGS

        )

    }

    private fun owner(): Owner {
        return Owner(
            type = Owner.OwnerType.NATURAL_PERSON,
            name = "Ratkovski",
            taxIdNumber = "63657520325"
        )
    }

}



