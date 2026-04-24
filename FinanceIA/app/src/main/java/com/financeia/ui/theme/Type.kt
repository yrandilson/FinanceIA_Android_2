package com.financeia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FinanceTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold,   lineHeight = 40.sp),
    headlineMedium= TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold,lineHeight = 32.sp),
    headlineSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold,lineHeight = 28.sp),
    titleLarge    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium,  lineHeight = 26.sp),
    titleMedium   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium,  lineHeight = 24.sp),
    titleSmall    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium,  lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal,  lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal,  lineHeight = 20.sp),
    bodySmall     = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal,  lineHeight = 16.sp),
    labelLarge    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium,  lineHeight = 20.sp),
    labelSmall    = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium,  lineHeight = 16.sp),
)
