package br.com.zupacademy.ratkovski.pix.registra.externo

data class BankAccount(
 //   val description:String,
    val participant:String,
    val branch:String,
    val accountNumber:String,
    val accountType: PixAccountType

) {


}

/*
*para lembrar
BankAccountRequest{
    description:
    Dados de conta transacional no Brasil

            participant*	string
    example: 60701190
    Identificador SPB do provedor da conta (ex: 60701190 ITAÚ UNIBANCO S.A.). Para ver os demais códigos https://www.bcb.gov.br/pom/spb/estatistica/port/ASTR003.pdf

    branch*	string
    maxLength: 4
    minLength: 4
    example: 0001
    Agência, sem dígito verificador.

    accountNumber*	string
    maxLength: 6
    minLength: 6
    example: 123456
    Número de conta, incluindo verificador. Se verificador for letra, substituir por 0.

    accountType*	string
    example: CACC
    Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)

    Enum:
    [ CACC, SVGS ]
}*/
