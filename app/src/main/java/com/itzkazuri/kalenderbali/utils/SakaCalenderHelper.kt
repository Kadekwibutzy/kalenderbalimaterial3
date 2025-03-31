package com.itzkazuri.kalenderbali.utils

import java.util.*

object SakaCalendarHelper {
    // Constants for significant Balinese Hindu holidays
    private const val FULL_MOON = "Purnama"
    private const val NEW_MOON = "Tilem"
    private const val NYEPI = "Hari Raya Nyepi"
    private const val SARASWATI = "Hari Raya Saraswati"
    private const val SIWARATRI = "Hari Suci Siwaratri"
    private const val PAGERWESI = "Hari Raya Pagerwesi"

    // List of Balinese pasaran (5-day cycle)
    private val pasaranList = listOf("Umanis", "Paing", "Pon", "Wage", "Kliwon")

    // List of wuku names (30 weeks in the Balinese calendar cycle)
    private val wukuNames = listOf(
        "Sinta", "Landep", "Ukir", "Kulantir", "Tolu", "Gumbreg", "Wariga", "Warigadian", "Julungwangi", "Sungsang",
        "Dungulan", "Kuningan", "Langkir", "Medangsia", "Pujut", "Pahang", "Krulut", "Merakih", "Tambir", "Madangkungan",
        "Maktal", "Uye", "Manail", "Prangbakat", "Bala", "Ugu", "Wayang", "Kelawu", "Dukut", "Watugunung"
    )

    /**
     * Returns a list of important Balinese Hindu dates for the given year.
     * This includes Purnama, Tilem, Nyepi, Siwaratri, Saraswati, and Pagerwesi.
     */
    fun getImportantDates(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            addAll(getPurnamaTilem(year))
            add(calculateNyepi(year) to NYEPI)
            calculateSiwaratri(year)?.let { add(it to SIWARATRI) }
            addAll(getAllSaraswatiInYear(year))
            addAll(getAllPagerwesiInYear(year))
        }.sortedBy { it.first.timeInMillis }
    }

    /**
     * Calculates and returns a list of Purnama (full moon) and Tilem (new moon) dates for the given year.
     */
    private fun getPurnamaTilem(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                val penanggal = saka.getSakaCalendar(SakaCalendar.PENANGGAL)
                val isPangelong = saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)

                when {
                    penanggal == 15 && !isPangelong -> add(calendar.clone() as Calendar to FULL_MOON) // Full moon
                    penanggal == 15 && isPangelong -> add(calendar.clone() as Calendar to NEW_MOON) // New moon
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Calculates the Nyepi (Balinese New Year) date based on 1 Penanggal Sasih Kadasa (10th month in Saka Calendar).
     */
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

    /**
     * Calculates Siwaratri, which occurs on the 14th Pangelong of Sasih Kapitu (7th month in Saka Calendar).
     */
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

    /**
     * Finds all Saraswati dates in the given year.
     * Saraswati occurs on Saturday Umanis Wuku Watugunung.
     */
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

    /**
     * Finds all Pagerwesi dates in the given year.
     * Pagerwesi occurs on Wednesday Kliwon Wuku Sinta (Wuku 1, Pancawara 5, and Saptawara 3).
     */
    fun getAllPagerwesiInYear(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                val wuku = saka.getWuku(SakaCalendar.NO_WUKU)
                val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA)
                val saptawara = saka.getSaptawara(SakaCalendar.NO_SAPTAWARA)

                if (wuku == 1 && pancawara == 5 && saptawara == 3) {
                    add(calendar.clone() as Calendar to PAGERWESI)
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Creates a SakaCalendar object based on the given Gregorian calendar date.
     */
    fun createSakaCalendar(calendar: Calendar): SakaCalendar {
        return SakaCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
