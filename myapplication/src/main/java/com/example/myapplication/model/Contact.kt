package com.example.myapplication.model


data class Contact(
    val id: String,
    val name: String,
    val lastUpdatedTimeStamp: String,
    val firstName: String?,
    val lastName: String?,
    val phoneticFirstName: String?,
    val phoneticMiddleName: String?,
    val phoneticLastName: String?,
    val nickname: String?,
    val middleName: String?,
    val user_id:String?,
    val tenant:String?
) {
    var phone = ArrayList<PhoneNumberDetail>()
    var emails = ArrayList<EmailIdDetails>()
    var address = ArrayList<AddressDetails>()
    var dates = ArrayList<SpecificDateDetails>()
    var workDetail = WorkDetails()
    var website: String? = null

}