package com.sdm3.parent.data.remote.api

object Endpoints {
    const val SANCTUM_CSRF_COOKIE = "/sanctum/csrf-cookie"
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val API_TOKEN = "/api/sanctum/token"
    const val API_USER = "/api/user"

    const val PARENT_STUDENTS = "/api/parent/students"
    const val PARENT_STUDENT_DETAIL = "/api/parent/students/{id}"
    const val PARENT_GRADES = "/api/parent/grades"
    const val PARENT_GRADE_COMPONENTS = "/api/parent/grade-components"
    const val PARENT_ATTENDANCES = "/api/parent/attendances"
    const val PARENT_ATTENDANCE_SUMMARY = "/api/parent/attendance-summary"
    const val PARENT_PAYMENTS = "/api/parent/payments"
    const val PARENT_STUDENT_FEES = "/api/parent/student-fees"
    const val PARENT_NOTIFICATIONS = "/api/parent/notifications"
    const val PARENT_NOTIFICATION_READ = "/api/parent/notifications/{id}/read"
    const val PARENT_ARTICLES = "/api/parent/articles"
    const val PARENT_RAPOR_INSTANCES = "/api/parent/rapor-instances"
    const val PARENT_RAPOR_DOWNLOAD = "/api/parent/rapor/{id}/download"
    const val PARENT_RAPOR_VERIFY = "/api/parent/rapor/verify"
    const val PARENT_EXTRACURRICULARS = "/api/parent/extracurriculars"
    const val PARENT_PROFILE = "/api/parent/profile"
    const val PARENT_DASHBOARD = "/api/parent/dashboard"

    const val FORGOT_PASSWORD = "/api/forgot-password"
    const val VERIFY_OTP = "/api/verify-otp"
    const val RESET_PASSWORD = "/api/reset-password"

    const val MIDTRANS_SNAP_TOKEN = "/dashboard/midtrans/snap-token/{payment}"
    const val MIDTRANS_STATUS = "/dashboard/midtrans/status/{chargeId}"
    const val MIDTRANS_PAYMENT_METHODS = "/dashboard/midtrans/payment-methods"
}
