package kr.co.bbmc.paycastdid.network.model

data class ResDidData(
    val data: List<CookingData>? = null,
    val message: String? = "",
    val success: Boolean
)

data class CookingData(
    val orderNumber: Int = 0,
    val menuName: String? = "",
    val count: Int = 0,
    val cookingState: String = "O"   // N :stay 대기중, O : complete 조리완료, delete: 수령완료(X)
)