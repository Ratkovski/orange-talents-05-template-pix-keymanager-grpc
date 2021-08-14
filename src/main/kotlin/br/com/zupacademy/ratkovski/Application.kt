package br.com.zupacademy.ratkovski

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zupacademy.ratkovski")
		.start()
}

