package br.com.zupacademy.ratkovski.pix.registra


import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import br.com.zupacademy.ratkovski.pix.validation.ValidPixKey

import br.com.zupacademy.ratkovski.pix.validation.ValidUUID

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@ValidPixKey
@Introspected
data class NovaChavePix(
    @ValidUUID
    @field:NotBlank
    val clienteId:String?,
    @field:NotNull
    val tipo: TipoChave?,
    @field:Size(max = 77, min=0)
    val chave:String?,
    @field:NotNull
    val tipoConta: TipoConta?

){
    fun toModel(conta:ContaAssociada): ChavePix {
        return ChavePix(
           // clienteId = clienteId.toString(),
            clienteId = UUID.fromString(this.clienteId),
            tipo = TipoChave.valueOf(this.tipo!!.name),
            chave = if (this.tipo == TipoChave.RANDOM)UUID.randomUUID().toString()else this.chave!!,
            tipoConta = TipoConta.valueOf(this.tipoConta!!.name),
            conta=conta
        )
    }
}

