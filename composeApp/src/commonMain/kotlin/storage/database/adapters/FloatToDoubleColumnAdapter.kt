package storage.database.adapters

import app.cash.sqldelight.ColumnAdapter

object FloatToDoubleColumnAdapter : ColumnAdapter<Float, Double> {
    override fun decode(databaseValue: Double): Float = databaseValue.toFloat()

    override fun encode(value: Float): Double = value.toDouble()
}
