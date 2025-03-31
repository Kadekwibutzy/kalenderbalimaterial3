package com.itzkazuri.kalenderbali.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object RahinaCalculator {
    /**
     * Menghitung tanggal Nyepi berdasarkan 1 Penanggal Sasih Kadasa (Sasih ke-10 dalam kalender Saka).
     */
    fun getNyepiDate(year: Int): String {
        val nyepiDate = SakaCalendarHelper.calculateNyepi(year)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(nyepiDate.time)
    }

    /**
     * Mengembalikan daftar hari raya (Nyepi, Siwaratri, Saraswati, Pagerwesi) berdasarkan tanggal Masehi.
     */
    fun getRerahinan(tanggal: Int, bulan: Int, tahun: Int): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(tahun, bulan - 1, tanggal)
        }

        val saka = SakaCalendarHelper.createSakaCalendar(calendar)

        val sasih = saka.getSakaCalendar(SakaCalendar.NO_SASIH)
        val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL)
        val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)
        val wuku = saka.getWuku(SakaCalendar.NO_WUKU)
        val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val rerahinanList = mutableListOf<String>()

        // Nyepi: 1 Penanggal Sasih Kadasa (Sasih 10)
        if (sasih == 10 && penanggal == 1 && !isPangelong) {
            rerahinanList.add("Nyepi")
        }

        // Siwaratri: 14 Pangelong Sasih Kapitu (Sasih 7)
        if (sasih == 7 && penanggal == 14 && isPangelong) {
            rerahinanList.add("Siwaratri")
        }

        // Saraswati: Sabtu Umanis Wuku Watugunung
        if (wuku == 30 && pancawara == 1 && dayOfWeek == Calendar.SATURDAY) {
            rerahinanList.add("Saraswati")
        }

        // Pagerwesi: Buda Kliwon Wuku Sinta
        if (wuku == 1 && pancawara == 5 && dayOfWeek == Calendar.WEDNESDAY) {
            rerahinanList.add("Pagerwesi")
        }

        // Purnama & Tilem
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
