package com.itzkazuri.kalenderbali.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object RahinaCalculator {
    /**
     * Calculates the Nyepi date based on the 1st Penanggal Sasih Kadasa (the 10th month in the Saka calendar).
     */
    fun getNyepiDate(year: Int): String {
        val nyepiDate = SakaCalendarHelper.calculateNyepi(year)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(nyepiDate.time)
    }

    /**
     * Returns a list of Balinese Hindu holidays (Nyepi, Siwaratri, Saraswati, Pagerwesi, Purnama, Tilem) based on the Gregorian date.
     */
    fun getRerahinan(tanggal: Int, bulan: Int, tahun: Int): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(tahun, bulan - 1, tanggal)
        }

        val saka = SakaCalendarHelper.createSakaCalendar(calendar)
        val sasih = saka.getSakaCalendar(SakaCalendar.NO_SASIH)
        val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL)
        val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)
        val wuku = saka.getSakaCalendar(SakaCalendar.NO_WUKU)
        val pancawara = saka.getSakaCalendar(SakaCalendar.NO_PANCAWARA)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val rerahinanList = mutableSetOf<String>() // Menggunakan Set agar tidak ada duplikasi

        // Nyepi: 1 Penanggal Sasih Kadasa (10 Sasih)
        if (sasih == 10 && penanggal == 1 && !isPangelong) {
            rerahinanList.add("Nyepi")
        }

        // Siwaratri: 14 Pangelong Sasih Kapitu (7 Sasih)
        if (sasih == 7 && penanggal == 14 && isPangelong) {
            rerahinanList.add("Siwaratri")
        }

        // Saraswati: Sabtu Umanis Wuku Watugunung
        if (wuku == 30 && pancawara == 1 && dayOfWeek == Calendar.SATURDAY) {
            rerahinanList.add("Saraswati")
        }

        // Pagerwesi: Rabu Kliwon Wuku Sinta
        if (wuku == 1 && pancawara == 5 && dayOfWeek == Calendar.WEDNESDAY) {
            rerahinanList.add("Pagerwesi")
        }

        // Purnama & Tilem
        val importantDates = SakaCalendarHelper.getImportantDates(tahun)
        importantDates.forEach { (eventCalendar, eventName) ->
            if (eventCalendar.get(Calendar.DAY_OF_MONTH) == tanggal &&
                eventCalendar.get(Calendar.MONTH) + 1 == bulan
            ) {
                rerahinanList.add(eventName)
            }
        }

        return rerahinanList.toList()
    }
}
