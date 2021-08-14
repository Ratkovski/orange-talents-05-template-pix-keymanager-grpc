package br.com.zupacademy.ratkovski.pix.remove


import br.com.zupacademy.ratkovski.pix.exception.ChavePixNaoEncontradaException
import br.com.zupacademy.ratkovski.pix.registra.externo.DeletePixKeyRequest
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import br.com.zupacademy.ratkovski.pix.validation.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import java.net.http.HttpClient

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemovePixKeyManagerService(
    @Inject val repository: ChavePixRepository,
    @Inject val bcbClient: ChavesDeClientesNoBcbClient) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "cliente Id com formato inválido")clienteId:String?,
        @NotBlank @ValidUUID(message = "pix Id com formato inválido")pixId:String?

    ){
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)

        val chave = repository.findByIdAndClienteId(uuidPixId,uuidClienteId)
            .orElseThrow{ChavePixNaoEncontradaException("Chave pix não encontrada no sistema para este cliente")
           }
       repository.delete(chave)

       val request = DeletePixKeyRequest(chave.chave)
       val bcbResponse = bcbClient.delete(key=chave.chave,request = request)
       if (bcbResponse.status != HttpStatus.OK){
           throw IllegalStateException("Erro ao remover chave pix no BCB")
       }

//       try {
//        val request = DeletePixKeyRequest(chave.chave)
//        val bcbResponse = bcbClient.delete(key=chave.chave,request = request)
//      if (bcbResponse.status.equals(HttpStatus.OK)) {
//          repository.delete(chave)
//         //  throw IllegalStateException("Erro ao remover chave pix no BCB")
//      }
//
//       }catch (e:HttpClientResponseException){
//           when(e.status){
//               HttpStatus.FORBIDDEN ->throw IllegalStateException("Esta operacao não é permitida para o BCB")
//               HttpStatus.NOT_FOUND ->throw ChavePixNaoEncontradaException("Esta chave não foi encontrada no sistema do bcb")
//               else -> throw IllegalStateException("Erro ao remover chave pix no BCB")
//           }
       }

    }





//}
