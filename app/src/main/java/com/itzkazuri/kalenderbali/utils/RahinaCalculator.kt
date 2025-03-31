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
     * Returns a list of Balinese Hindu holidays (Nyepi, Siwaratri, Saraswati, Pagerwesi) based on the Gregorian date.
     */
    fun getRerahinan(tanggal: Int, bulan: Int, tahun: Int): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(tahun, bulan - 1, tanggal)
        }

        val saka = SakaCalendarHelper.createSakaCalendar(calendar) // Create Saka calendar instance

        val sasih = saka.getSakaCalendar(SakaCalendar.NO_SASIH) // Get Sasih (month in Saka calendar)
        val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL) // Get Penanggal (day in Saka calendar)
        val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG) // Check if it's Pangelong

        val rerahinanList = mutableListOf<String>()

        // 1. Nyepi: 1st Penanggal Sasih Kadasa (10th Sasih)
        if (sasih == 10 && penanggal == 1 && !isPangelong) {
            rerahinanList.add("Nyepi")
        }

        // 2. Siwaratri: 14th Pangelong Sasih Kapitu (7th Sasih)
        if (sasih == 7 && penanggal == 14 && isPangelong) {
            rerahinanList.add("Siwaratri")
        }

        // 3. Saraswati: Saturday Umanis Wuku Watugunung
        val wuku = saka.getWuku(SakaCalendar.NO_WUKU) // Get Wuku (Balinese week cycle)
        val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA) // Get Pancawara (5-day cycle)

        if (wuku == 30 && pancawara == 1 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            rerahinanList.add("Saraswati")
        }

        // 4. Pagerwesi: Wednesday Kliwon Wuku Sinta (Wuku 1, Pancawara 5, Wednesday)
        if (wuku == 1 && pancawara == 5 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            rerahinanList.add("Pagerwesi")
        }

        // Add Purnama and Tilem (Full Moon and New Moon) if present
        val purnamaTilemList = SakaCalendarHelper.getImportantDates(tahun)
            .filter {
                it.first.get(Calendar.DAY_OF_MONTH) == tanggal &&
                        it.first.get(Calendar.MONTH) + 1 == bulan &&
                        (it.second == "Purnama" || it.second == "Tilem")
            }
            .map { it.second }

        rerahinanList.addAll(purnamaTilemList)

        return rerahinanList
    }
}
