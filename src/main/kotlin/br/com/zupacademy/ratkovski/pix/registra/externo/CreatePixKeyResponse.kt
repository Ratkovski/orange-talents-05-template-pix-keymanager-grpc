package br.com.zupacademy.ratkovski.pix.registra.externo

import br.com.zupacademy.ratkovski.pix.grpcenum.PixKeyType
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import java.time.LocalDateTime

data class CreatePixKeyResponse (
    val keyType:PixKeyType,
    val key:String,
    val bankAccount:BankAccount,
    val owner:Owner,
    val createdAt: LocalDateTime

        ) {
//    data class BankAccountResponse(
//        val participant: String,
//        val branch: String,
//        val accountNumber: String,
//        val accountType: PixAccountType
//    )

//    data class OwnerResponse(
//        val type: Owner.OwnerType,
//        val name: String,
//        val taxIdNumber: String
//    )
}





/*

keyType	string
Enum:
[ CPF, CNPJ, PHONE, EMAIL, RANDOM ]
key	string
bankAccount	BankAccountResponse{
    participant	string
            branch	string
            accountNumber	string
            accountType	string
            Enum:
    [ CACC, SVGS ]
}
owner	OwnerResponse{
    type	string
            Enum:
    [ NATURAL_PERSON, LEGAL_PERSON ]
    name	string
            taxIdNumber	string
}
createdAt	string($date-time)
}*/
