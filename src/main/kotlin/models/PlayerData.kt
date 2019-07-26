package models

abstract class PlayerData<T>(
    oldData: T,
    newData: T,
    private val name: String,
    private val id: Long
) {

    private val data: Pair<T,T>

    init {
        this.data = Pair(oldData, newData)
    }

    fun getId() = id
    fun getName(quotes:Boolean = true) = if (quotes) "“$name”" else name
    fun isValid() = id > 0
    fun oldData() = this.data.first
    fun getData() = this.data.second

}