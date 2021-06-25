package me.vipa.userManagement.bean.UserProfile

data class Response(
	val data: Data? = null,
	val debugMessage: Any? = null,
	val responseCode: Int? = null
)

data class PrimaryAccountRef(
	val manualLinked: Boolean? = null,
	val profileStep: Any? = null,
	val isFbLinked: Boolean? = null,
	val subscriptions: Any? = null,
	val entitlementState: Any? = null,
	val gender: Any? = null,
	val secondaryAccounts: Any? = null,
	val customData: CustomDataTwo? = null,
	val expiryDate: Any? = null,
	val password: Any? = null,
	val id: Int? = null,
	val email: String? = null,
	val kidsAccount: Any? = null,
	val verified: Boolean? = null,
	val appUserPlans: Any? = null,
	val dateOfBirth: Any? = null,
	val killBillSubscriptionId: Any? = null,
	val appleLinked: Boolean? = null,
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

data class CustomDataTwo(
	val address: String? = null,
	val parentalPin: String? = null,
	val notificationCheck: String? = null
)

data class Data(
	val manualLinked: Boolean? = null,
	val profileStep: Any? = null,
	val isFbLinked: Boolean? = null,
	val subscriptions: Any? = null,
	val entitlementState: Any? = null,
	val gender: Any? = null,
	val secondaryAccounts: Any? = null,
	val customData: CustomData? = null,
	val expiryDate: Any? = null,
	val password: Any? = null,
	val id: Int? = null,
	val email: Any? = null,
	val kidsAccount: Boolean? = null,
	val verified: Boolean? = null,
	val appUserPlans: Any? = null,
	val dateOfBirth: Any? = null,
	val killBillSubscriptionId: Any? = null,
	val appleLinked: Boolean? = null,
	val verificationDate: Long? = null,
	val primaryAccount: Boolean? = null,
	val userEconomics: Any? = null,
	val accountId: String? = null,
	val phoneNumber: Any? = null,
	val fbLinked: Boolean? = null,
	val profilePicURL: Any? = null,
	val primaryAccountRef: PrimaryAccountRef? = null,
	val name: String? = null,
	val status: String? = null,
	val gplusLinked: Boolean? = null
)

