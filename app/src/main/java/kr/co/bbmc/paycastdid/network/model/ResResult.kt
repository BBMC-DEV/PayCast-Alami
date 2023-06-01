package kr.co.bbmc.paycastdid.network.model

data class ResDidData(
    val data: List<CookingData>,
    val message: String? = "",
    val success: Boolean
)

data class CookingData(
    val orderNumber: Int = 0,
    val menuName: String? = "",
    val count: Int = 0,
    val cookingState: String = "stay"   // stay 대기중, complete 조리완료, delete: 수령완료
)