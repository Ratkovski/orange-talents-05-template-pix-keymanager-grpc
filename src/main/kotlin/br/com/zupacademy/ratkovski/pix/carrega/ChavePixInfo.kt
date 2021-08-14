package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import java.time.LocalDateTime
import java.util.*


data class ChavePixInfo(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoDeConta: TipoConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chave.pixId,
                clienteId = chave.clienteId,
                tipo = chave.tipo,
                chave = chave.chave,
                tipoDeConta = chave.tipoConta,
                conta = chave.conta,
                registradaEm = chave.criadaEm
            )
        }
    }
}