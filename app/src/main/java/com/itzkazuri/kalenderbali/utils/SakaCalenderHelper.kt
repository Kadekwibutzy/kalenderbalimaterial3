package com.itzkazuri.kalenderbali.utils

import java.util.*

object SakaCalendarHelper {
    private const val FULL_MOON = "Purnama"
    private const val NEW_MOON = "Tilem"
    private const val NYEPI = "Hari Raya Nyepi"
    private const val SARASWATI = "Hari Raya Saraswati"
    private const val SIWARATRI = "Hari Suci Siwaratri"
    private const val PAGERWESI = "Hari Raya Pagerwesi"

    private val pasaranList = listOf("Umanis", "Paing", "Pon", "Wage", "Kliwon")
    private val wukuNames = listOf(
        "Sinta", "Landep", "Ukir", "Kulantir", "Tolu", "Gumbreg", "Wariga", "Warigadian", "Julungwangi", "Sungsang",
        "Dungulan", "Kuningan", "Langkir", "Medangsia", "Pujut", "Pahang", "Krulut", "Merakih", "Tambir", "Madangkungan",
        "Maktal", "Uye", "Manail", "Prangbakat", "Bala", "Ugu", "Wayang", "Kelawu", "Dukut", "Watugunung"
    )

    fun getImportantDates(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            addAll(getPurnamaTilem(year))
            add(calculateNyepi(year) to NYEPI)
            calculateSiwaratri(year)?.let { add(it to SIWARATRI) }
            addAll(getAllSaraswatiInYear(year))
            addAll(getAllPagerwesiInYear(year))
        }.sortedBy { it.first.timeInMillis }
    }

    private fun getPurnamaTilem(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL)
                val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)

                when {
                    penanggal == 15 && !isPangelong -> add(calendar.clone() as Calendar to FULL_MOON)
                    penanggal == 15 && isPangelong -> add(calendar.clone() as Calendar to NEW_MOON)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    fun calculateNyepi(year: Int): Calendar {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.MARCH, 1) }
        val endDate = Calendar.getInstance().apply { set(year, Calendar.APRIL, 1) }

        while (calendar < endDate) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 10 &&
                saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 1 &&
                !saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)) {
                return calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return Calendar.getInstance()
    }

    fun calculateSiwaratri(year: Int): Calendar? {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
        val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

        while (calendar <= endDate) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 7 &&
                saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 14 &&
                saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)) {
                return calendar.clone() as Calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return null
    }

    fun getAllSaraswatiInYear(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                if (saka.getWuku(SakaCalendar.NO_WUKU) == 30 &&
                    saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 1 &&
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    add(calendar.clone() as Calendar to SARASWATI)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    fun getAllPagerwesiInYear(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)

                // Tambahkan logging untuk melihat nilai yang dihitung
                val wuku = saka.getWuku(SakaCalendar.NO_WUKU)
                val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA)
                val saptawara = saka.getSaptawara(SakaCalendar.NO_SAPTAWARA)

                println("Tanggal: ${calendar.time} | Wuku: $wuku | Pancawara: $pancawara | Saptawara: $saptawara")

                if (wuku == 1 && pancawara == 5 && saptawara == 3) {
                    add(calendar.clone() as Calendar to PAGERWESI)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }


    fun createSakaCalendar(calendar: Calendar): SakaCalendar {
        return SakaCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
