package network.backend.proto

import filamagenta.data.UserTransaction
import network.backend.model.BackendConnector

abstract class ITransactions : BackendConnector() {
    abstract suspend fun getTransactions(): List<UserTransaction>
}
