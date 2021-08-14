package br.com.zupacademy.ratkovski.pix.registra

import br.com.zupacademy.ratkovski.AccountType
import br.com.zupacademy.ratkovski.KeyType
import br.com.zupacademy.ratkovski.PixKeyManagerServiceGrpc
import br.com.zupacademy.ratkovski.RegisterKeyPixRequest
import br.com.zupacademy.ratkovski.pix.grpcenum.PixKeyType
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.registra.externo.*

import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient

import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ContasDeClientesNoItauClient
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
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`

import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.reflect.typeOf


@MicronautTest(transactional =false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient

    @Inject
    lateinit var bcbClient: ChavesDeClientesNoBcbClient


    companion object {
        val CLIENTE_ID: UUID = UUID.randomUUID()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar chave pix`() {
        //cenário
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.create(createPixKeyRequest()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))


        //acao
        val response = grpcClient.registra(
            RegisterKeyPixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChave(KeyType.EMAIL)
                .setChave("ratkovski@email.com")
                .setTipoConta(AccountType.CONTA_CORRENTE)

                .build()
        )

        //validacao
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clienteId)
            assertNotNull(pixId)
        }

    }


    @Test
    fun `nao deve registrar chave pix quando ja estiver registrada no sistema`() {
        //cenario
        repository.save(
            chave(
                tipo = TipoChave.CPF,
                chave = "86135457004",
                clienteId = CLIENTE_ID

            )
        )

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegisterKeyPixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(KeyType.CPF)
                    .setChave("86135457004")
                    .setTipoConta(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }
        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix '86135457004' já cadastrada no sistema", status.description)
        }
    }


    @Test
    fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`() {
        //cenario
        `when`(itauClient.buscaContaPorTipo(CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegisterKeyPixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(KeyType.EMAIL)
                    .setChave("ratkovski@email.com")
                    .setTipoConta(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }
        //validacao
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente não encontrado no Itau", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando parametros forem invalidos`() {
        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegisterKeyPixRequest.newBuilder().build())
        }

        //validacao //preciso melhorar este
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
//           assertEquals("Dados inválidos", status.description)
//            assertThat(
//                violations(), containsInAnyOrder(
//                    Pair("clienteId", "must not be blank"),
//                    Pair("clienteId", "não é um formato válido de UUID"),
//                    Pair("tipo", "must not be null"),
//                    Pair("tipoConta", "must not be null")
//
//                )
//            )
//
        }
    }


    @Test
    fun `nao deve registrar chave pix quando nao for possivel registrar chave no BCB`() {
        //cenário
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.create(createPixKeyRequest()))
            .thenReturn(HttpResponse.badRequest())
        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegisterKeyPixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(KeyType.EMAIL)
                    .setChave("ratkovski@email.com")
                    .setTipoConta(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }
        //validacao
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao registrar chave PIX no BCB", status.description)
        }
    }



    //ainda não consegui fazer o pair funcionar
//    @Test
//    fun `nao deve registrar chave pix quando parametros forem invalidos - chave invalida`() {
//        // ação
//        val thrown = assertThrows<StatusRuntimeException> {
//            grpcClient.registra(
//                RegisterKeyPixRequest.newBuilder()
//                .setClienteId(CLIENTE_ID.toString())
//                .setTipoChave(KeyType.CPF)
//                .setChave("378.930.cpf-invalido.389-73")
//                .setTipoConta(AccountType.CONTA_POUPANCA)
//                .build())
//        }
//
//        // validação
//        with(thrown) {
//            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
//            assertEquals("Dados Inválidos",status.description)
//            assertThat(violations(), containsInAnyOrder(
//                Pair("chave", "chave Pix inválida (CPF)"),
//            ))
//        }
//    }




    //itau
    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClient(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
  //bcb
    }
    @MockBean(ChavesDeClientesNoBcbClient::class)
    fun bcbClient():ChavesDeClientesNoBcbClient? {
        return Mockito.mock(ChavesDeClientesNoBcbClient::class.java)
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub? {
            return PixKeyManagerServiceGrpc.newBlockingStub(channel)
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



    private fun dadosDaContaResponse(): DadosDaContaResponse{
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", ContaAssociada.ITAU_UNIBANCO_ISPB),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse("Ratkovski", "63657520325")
        )
    }

    private fun createPixKeyRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = PixKeyType.EMAIL,
            key = "ratkovski@email.com",
            bankAccount = bankAccount(),
            owner = owner()

        )

    }

    private fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = PixKeyType.EMAIL,
            key = "ratkovski@email.com",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now()

        )

    }

    private fun bankAccount(): BankAccount {
        return BankAccount(
            participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
            branch = "1218",
            accountNumber = "291900",
            accountType = PixAccountType.CACC

        )

    }
    private fun owner(): Owner {
        return Owner(
                type= Owner.OwnerType.NATURAL_PERSON,
                name = "Ratkovski",
                taxIdNumber = "63657520325"
                )
    }
}


