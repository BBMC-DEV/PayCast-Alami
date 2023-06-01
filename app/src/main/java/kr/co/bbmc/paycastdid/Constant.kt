package kr.co.bbmc.paycastdid

import io.reactivex.rxjava3.subjects.PublishSubject

const val ACTION_SERVICE_COMMAND = "kr.co.bbmc.paycastdid.serviceCommand"
const val ACTION_ACTIVITY_UPDATE = "kr.co.bbmc.paycastdid.activityupdate"
@JvmField
var MAX_ALARAM_COUNT_PER_SCREEN = 4 //8->4로 전시회를 위해 수정
@JvmField
var LOG_SAVE = true

// fcm message
val firebaseMsg = PublishSubject.create<String>()

@JvmField
var storeId = -1
@JvmField
var deviceId = ""
