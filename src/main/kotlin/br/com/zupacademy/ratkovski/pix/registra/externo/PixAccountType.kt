package br.com.zupacademy.ratkovski.pix.registra.externo

import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import java.lang.IllegalArgumentException

enum class PixAccountType() {
    CACC,
    SVGS;
    // Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)
    companion object{

        fun by(domainType: TipoConta):PixAccountType{
            return when(domainType){
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
            }

        }
    }
/*enum class PixAccountType(val domainType: TipoConta?) {
    CACC(TipoConta.CONTA_CORRENTE),
    SVGS(TipoConta.CONTA_POUPANCA);
    // Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)
    companion object{
        private val mapping = PixAccountType.values().associateBy(PixAccountType::domainType)
        fun by(domainType: TipoConta?):PixAccountType{
            return mapping[domainType]?: throw IllegalArgumentException("PixAccountType invalid or not found for $domainType")

        }
    }*/






    }


