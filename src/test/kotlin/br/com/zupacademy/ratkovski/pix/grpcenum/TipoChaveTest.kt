package br.com.zupacademy.ratkovski.pix.grpcenum

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


internal class TipoChaveTest {

    /**
     * @Nested é usado para sinalizar que a classe anotada é uma classe de teste aninhada
     * e não estática (ou seja, uma classe interna) que pode compartilhar a configuração
     * e o estado com uma instância de sua classe envolvente. A classe envolvente pode ser
     * uma classe de teste de nível superior ou outra classe de teste @Nested, e o aninhamento
     * pode ser arbitrariamente profundo.
     * Ciclo de vida da instância de teste
     *Uma classe de teste @Nested pode ser configurada com seu próprio modo TestInstance.
     * Lifecycle, que pode ser diferente daquele de uma classe de teste envolvente.
     *Uma classe de teste @Nested não pode alterar o modo TestInstance.
     * Lifecycle de uma classe de teste envolvente.
     */

    /**
     * Testando a chave aleatoria
     */
    @Nested
    inner class RANDOM {

        @Test
        fun `deve ser valido quando o campo de chave aleatoria for nulo ou vazio`() {
            with(TipoChave.RANDOM) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando o campo de chave aleatoria possuir um valor`() {
            with(TipoChave.RANDOM) {
                assertFalse(valida("teste"))
            }
        }

    }
    /**
     * Testando a chave CPF
     */
    @Nested
    inner class CPF {

        @Test
        fun `deve ser valido quando o campo cpf for valido`() {
            with(TipoChave.CPF) {
                assertTrue(valida("91103941097"))

            }
        }

        @Test
        fun `nao deve ser valido quando o campo cpf for valido`() {
            with(TipoChave.CPF) {
                assertFalse(valida("9110394109"))

            }
        }

        @Test
        fun `nao deve ser valido quando o campo cpf for nulo ou vazio`() {
            with(TipoChave.CPF) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando o campo cpf possuir letras`() {
            with(TipoChave.CPF) {
                assertFalse(valida("9110394102a"))

            }
        }
    }
    /**
     * Testando a chave CELULAR
     */
    @Nested
    inner class PHONE {

        @Test
        fun `deve ser valido quando o campo celular for um numero valido`() {
            with(TipoChave.PHONE) {
                assertTrue(valida("+5518918979876"))

            }
        }
        @Test
        fun `nao deve ser valido quando o campo celular for nulo ou vazio`() {
            with(TipoChave.PHONE) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }
        @Test
        fun `nao deve ser valido quando o numero do celular for invalido`() {
            with(TipoChave.PHONE) {
                assertFalse(valida("5518918979876090"))
                assertFalse(valida("+55189189a"))
            }
        }

    }
    /**
     * Testando a chave EMAIL
     */
    @Nested
    inner class EMAIL {

        @Test
        fun `deve ser valido quando o campo email possuir um formato  valido`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("ratkovski@email.com"))

            }
        }
        @Test
        fun `nao deve ser valido quando o campo email for nulo ou vazio`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }
        @Test
        fun `nao deve ser valido quando o campo email possuir formato invalido`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("ratkovskiemail.com"))
                assertFalse(valida("ratkovski@email.com."))
            }
        }

    }


}

