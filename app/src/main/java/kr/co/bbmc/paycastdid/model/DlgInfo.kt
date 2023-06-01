package kr.co.bbmc.paycastdid.model

import kr.co.bbmc.paycast.ui.component.theme.ButtonType

data class DlgInfo (
    val type: ButtonType = ButtonType.Cancel,
    val contentTitle: String? = "PayCast",
    val contents: String? = "",
    val positiveCallback: (() -> Unit)? = null,
    val negativeCallback: (() -> Unit)? = null,
    val iconResource: Int? = null
)