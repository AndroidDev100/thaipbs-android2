package me.vipa.userManagement.bean.allSecondaryDetails

data class SwitchUserDetails(
	val data: Data1? = null,
	val debugMessage: Any? = null,
	val responseCode: Int? = null
)
data class Data1(
	val accountId: String? = null,
	val phoneNumber: Any? = null,
	val expiryTime: Long? = null,
	val name: String? = null,
	val id: Int? = null,
	val email: Any? = null
)

