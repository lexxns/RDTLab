package org.lexxns.strategies

import org.lexxns.interfaces.Strategy

class DefaultStrategy : Strategy {
    override fun execute() {
        println("Default strategy executed")
    }
}