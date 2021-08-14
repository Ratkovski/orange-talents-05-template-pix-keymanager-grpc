package br.com.zupacademy.ratkovski.pix.registra

import br.com.zupacademy.ratkovski.AccountType
import br.com.zupacademy.ratkovski.KeyType
import br.com.zupacademy.ratkovski.RegisterKeyPixRequest
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta

fun RegisterKeyPixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        clienteId = clienteId,
        tipo = when (tipoChave) {
                KeyType.UNKNOWN_KEY -> null
            else -> TipoChave.valueOf(tipoChave.name)

        },
        chave = chave,
        tipoConta = when (tipoConta) {
            AccountType.UNKNOWN_ACCOUNT -> null
            else -> TipoConta.valueOf(tipoConta.name)

        }

    )

}
