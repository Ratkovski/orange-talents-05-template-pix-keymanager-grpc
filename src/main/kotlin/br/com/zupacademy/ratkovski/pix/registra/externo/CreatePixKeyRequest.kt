package br.com.zupacademy.ratkovski.pix.registra.externo

import br.com.zupacademy.ratkovski.pix.grpcenum.PixKeyType
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix

data class CreatePixKeyRequest(
    val keyType:PixKeyType,
    val key:String,
    val bankAccount:BankAccount,
    val owner:Owner

) {
    /*Companion Object
    Esse tipo de objeto é inicializado quando a classe que
    carrega ele é carregada/resolvida.*/
    companion object {
        fun of(chave: ChavePix): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = PixKeyType.by(chave.tipo),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numeroDaConta,
                    accountType = PixAccountType.by(chave.tipoConta)
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = chave.conta.nomeDoTitular,
                    taxIdNumber = chave.conta.cpfDoTitular
                )

            )
        }
    }
}

//
//CreatePixKeyRequest{
//    keyType*	string
//    example: CPF
//    Tipo de chave. Novos tipos podem surgir
//
//            Enum:
//    [ CPF, CNPJ, PHONE, EMAIL, RANDOM ]

//    key	string
//            maxLength: 77
//    minLength: 0
//    example: 33059192057
//    Chave de endereçamento
//
//    bankAccount*	BankAccountRequest{
//        description:
//        Dados de conta transacional no Brasil
//
//                participant*	string
//        example: 60701190
//        Identificador SPB do provedor da conta (ex: 60701190 ITAÚ UNIBANCO S.A.). Para ver os demais códigos https://www.bcb.gov.br/pom/spb/estatistica/port/ASTR003.pdf
//
//        branch*	string
//        maxLength: 4
//        minLength: 4
//        example: 0001
//        Agência, sem dígito verificador.
//
//        accountNumber*	string
//        maxLength: 6
//        minLength: 6
//        example: 123456
//        Número de conta, incluindo verificador. Se verificador for letra, substituir por 0.
//
//        accountType*	string
//        example: CACC
//        Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)
//
//        Enum:
//        Array [ 2 ]
//    }
//    owner*	OwnerRequest{
//        type*	string
//        Enum:
//        Array [ 2 ]
//        name*	string
//        example: Steve Jobs
//        Nome completo
//
//                taxIdNumber*	string
//        example: 33059192057
//        CPF - Cadastro de Pessoa Física
//
//    }
//}