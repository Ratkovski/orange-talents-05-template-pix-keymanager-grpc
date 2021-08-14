package br.com.zupacademy.ratkovski.pix.registra

import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ContasDeClientesNoItauClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import br.com.zupacademy.ratkovski.pix.exception.ChavePixExistenteException
import br.com.zupacademy.ratkovski.pix.registra.externo.CreatePixKeyRequest
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import io.micronaut.http.HttpStatus

import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Validated
@Singleton
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ContasDeClientesNoItauClient,
                          @Inject val bcbClient: ChavesDeClientesNoBcbClient) {

    private val Logger = LoggerFactory.getLogger(this::class.java)

   // @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {


        //1.verifica se a chave já existe no sistema
        if (repository.existsByChave(novaChave.chave))
            throw ChavePixExistenteException("Chave Pix '${novaChave.chave}' já cadastrada no sistema")
        //2.busca dados da conta no ERP do ITAU

        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!,novaChave.tipoConta!!.name)
        val conta = response.body()?.toModel() ?: throw  IllegalStateException("Cliente não encontrado no Itau")

        //3.grava no banco de dados
        val chave = novaChave.toModel(conta)
        repository.save(chave)
     //   return chave



        val bcbRequest = CreatePixKeyRequest.of(chave).also {
            Logger.info("Registrando a chave PIX no BCB:$it")
        }


      val bcbResponse = bcbClient.create(bcbRequest)
      Logger.info("Status BCB ${bcbResponse.status.toString()}")
      if (bcbResponse.status != HttpStatus.CREATED)
          throw IllegalStateException("Erro ao registrar chave PIX no BCB")

      chave.atualiza(bcbResponse.body()!!.key)
                  return chave
    }



}
