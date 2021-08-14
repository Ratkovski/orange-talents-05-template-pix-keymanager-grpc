package br.com.zupacademy.ratkovski.pix.registra.externo

import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada

data class DeletePixKeyRequest(
    val key:String,
    val participant:String = ContaAssociada.ITAU_UNIBANCO_ISPB

) {

}
