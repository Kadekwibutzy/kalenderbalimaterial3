package com.itzkazuri.kalenderbali.utils

import java.util.Calendar

object SakaCalendarHelper {
    private const val FULL_MOON = "Purnama"
    private const val NEW_MOON = "Tilem"
    private const val NYEPI = "Hari Raya Nyepi"
    private const val SARASWATI = "Hari Raya Saraswati"
    private const val SIWARATRI = "Hari Suci Siwaratri"
    private const val PAGERWESI = "Hari Raya Pagerwesi"
    private const val KAJENG_KLIWON = "Kajeng Kliwon"
    private const val SUGIHAN_JAWA = "Sugihan Jawa"
    private const val SUGIHAN_BALI = "Sugihan Bali"

    fun getImportantDates(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            addAll(getPurnamaTilem(year))
            add(calculateNyepi(year) to NYEPI)
            calculateSiwaratri(year)?.let { add(it to SIWARATRI) }
            addAll(getAllSaraswatiInYear(year))
            addAll(getAllPagerwesiInYear(year))
            addAll(getAllKajengKliwonInYear(year))
            addAll(getAllSugihanInYear(year))
        }.sortedBy { it.first.timeInMillis }
    }

    private fun getPurnamaTilem(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            val sasihName = getSasihName(saka.getSakaCalendar(SakaCalendar.NO_SASIH))
            if (saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 15) {
                val event = if (saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG)) NEW_MOON else FULL_MOON
                result.add(calendar.clone() as Calendar to "$event $sasihName")
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun calculateNyepi(year: Int): Calendar {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.MARCH, 1) }
        while (true) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 10 && saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 1) {
                return calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun calculateSiwaratri(year: Int): Calendar? {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
        var siwaratriDate: Calendar? = null

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 7 && saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 14) {
                siwaratriDate = calendar.clone() as Calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return siwaratriDate
    }

    fun getAllSaraswatiInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            if (saka.getWuku(SakaCalendar.NO_WUKU) == 30 &&
                saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 1 &&
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
            ) {
                result.add(calendar.clone() as Calendar to SARASWATI)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun getAllPagerwesiInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            if (saka.getWuku(SakaCalendar.NO_WUKU) == 1 &&
                saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 5 &&
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
            ) {
                result.add(calendar.clone() as Calendar to PAGERWESI)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun getAllKajengKliwonInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            if (saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 5 && saka.getTriwara() == 3) {
                result.add(calendar.clone() as Calendar to KAJENG_KLIWON)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun getAllSugihanInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            val wuku = saka.getWuku(SakaCalendar.NO_WUKU)
            val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA)
            val saptawara = saka.getSaptawara(SakaCalendar.NO_SAPTAWARA)

            // Sugihan Jawa: Kamis Wage wuku Sungsang (Wuku 10)
            if (wuku == 10 && pancawara == 4 && saptawara == 4) {
                val sugihanJawa = calendar.clone() as Calendar
                result.add(sugihanJawa to SUGIHAN_JAWA)

                // Sugihan Bali: Jumat Kliwon wuku Sungsang (1 hari setelah Sugihan Jawa)
                val sugihanBali = calendar.clone() as Calendar
                sugihanBali.add(Calendar.DAY_OF_MONTH, 1)

                val sakaBali = createSakaCalendar(sugihanBali)
                if (sakaBali.getWuku(SakaCalendar.NO_WUKU) == 10 &&
                    sakaBali.getPancawara(SakaCalendar.NO_PANCAWARA) == 5 &&
                    sakaBali.getSaptawara(SakaCalendar.NO_SAPTAWARA) == 5) {
                    result.add(sugihanBali to SUGIHAN_BALI)
                }
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }


    private fun getSasihName(sasihNumber: Int): String {
        return listOf("Kasa", "Karo", "Katiga", "Kapat", "Kalima", "Kanem", "Kapitu", "Kawolu", "Kasanga", "Kadasa", "Destha", "Sadha")[sasihNumber - 1]
    }

    fun createSakaCalendar(calendar: Calendar): SakaCalendar {
        return SakaCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }
}
