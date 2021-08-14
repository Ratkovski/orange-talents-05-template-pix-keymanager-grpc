package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.pix.grpcenum.PixKeyType
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.externo.BankAccount
import br.com.zupacademy.ratkovski.pix.registra.externo.Owner
import br.com.zupacademy.ratkovski.pix.registra.externo.PixAccountType
import java.time.LocalDateTime

data class DetailsPixKeyResponse(
    val keyType: PixKeyType,
    val key:String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    fun toModel(): ChavePixInfo{
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoDeConta = when(this.bankAccount.accountType){
                PixAccountType.CACC -> TipoConta.CONTA_CORRENTE
                PixAccountType.SVGS  -> TipoConta.CONTA_POUPANCA

            },
            conta = ContaAssociada(
                instituicao = Instituicoes.nome(bankAccount.participant),
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }
}