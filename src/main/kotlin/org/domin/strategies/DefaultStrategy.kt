package org.domin.strategies

import org.domin.interfaces.Strategy

class DefaultStrategy : Strategy {
    override fun execute() {
        println("Default strategy executed")
    }
}