package me.vipa.userManagement.bean.allSecondaryDetails

data class SecondaryUserDetails(
	val data: Data? = null,
	val debugMessage: Any? = null,
	val responseCode: Int? = null
)

data class PrimaryAccountRefOne(
	val manualLinked: Boolean? = null,
	val profileStep: Any? = null,
	val isFbLinked: Boolean? = null,
	val subscriptions: Any? = null,
	val gender: Any? = null,
	val secondaryAccounts: Any? = null,
	val customData: Any? = null,
	val expiryDate: Any? = null,
	val password: Any? = null,
	val id: Int? = null,
	val email: String? = null,
	val kidsAccount: Any? = null,
	val verified: Boolean? = null,
	val appUserPlans: Any? = null,
	val dateOfBirth: Any? = null,
	val verificationDate: Any? = null,
	val primaryAccount: Boolean? = null,
	val userEconomics: Any? = null,
	val accountId: String? = null,
	val phoneNumber: Any? = null,
	val fbLinked: Boolean? = null,
	val profilePicURL: Any? = null,
	val primaryAccountRef: Any? = null,
	val name: String? = null,
	val status: String? = null,
	val gplusLinked: Boolean? = null
)

data class Data(
	val manualLinked: Boolean? = null,
	val profileStep: Any? = null,
	val isFbLinked: Boolean? = null,
	val subscriptions: Any? = null,
	val gender: Any? = null,
	val secondaryAccounts: Any? = null,
	val customData: Any? = null,
	val expiryDate: Any? = null,
	val password: Any? = null,
	val id: Int? = null,
	val email: Any? = null,
	val kidsAccount: Boolean? = null,
	val verified: Boolean? = null,
	val appUserPlans: Any? = null,
	val dateOfBirth: Any? = null,
	val verificationDate: Long? = null,
	val primaryAccount: Boolean? = null,
	val userEconomics: Any? = null,
	val accountId: String? = null,
	val phoneNumber: Any? = null,
	val fbLinked: Boolean? = null,
	val profilePicURL: Any? = null,
	val primaryAccountRef: PrimaryAccountRefOne? = null,
	val name: String? = null,
	val status: String? = null,
	val gplusLinked: Boolean? = null
)

