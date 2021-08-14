package br.com.zupacademy.ratkovski.pix.grpcenum

import java.lang.IllegalArgumentException


enum class PixKeyType(val domainType: TipoChave?) {
    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.PHONE),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.RANDOM);

    companion object {
        private val mapping = PixKeyType.values().associateBy(PixKeyType::domainType)
        fun by(domainType: TipoChave): PixKeyType {
            return mapping[domainType]
                ?: throw IllegalArgumentException("PixKeyType invalid or not found for $domainType")
        }

    }

}