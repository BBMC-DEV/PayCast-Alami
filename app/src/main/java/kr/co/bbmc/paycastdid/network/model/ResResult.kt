package kr.co.bbmc.paycastdid.network.model

data class ResDidData(
    val data: List<OrderData>,
    val message: String? = "",
    val success: Boolean
)

data class OrderData(
    val kioskEnable: String? = "true",
    val openType: String? = "O"
)