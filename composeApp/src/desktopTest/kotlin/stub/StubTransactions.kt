package stub

import data.TransactionType
import filamagenta.data.UserTransaction
import kotlinx.datetime.LocalDate
import network.backend.proto.ITransactions

object StubTransactions: ITransactions() {
    override suspend fun getTransactions(): List<UserTransaction> = listOf(
        UserTransaction(
            id = 1,
            date = LocalDate(2023, 12, 22),
            description = "Testing description",
            income = true,
            units = 1U,
            pricePerUnit = 10f,
            type = TransactionType.DEBT,
            userId = 1
        )
    )
}
