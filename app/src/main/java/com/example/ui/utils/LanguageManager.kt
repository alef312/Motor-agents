package com.example.ui.utils

import android.content.Context
import android.content.SharedPreferences

data class LanguageOption(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val localizedNamePt: String,
    val speakersGlobal: String,
    val rank: Int,
    val flag: String
)

object LanguageManager {

    private const val PREFS_NAME = "app_language_prefs"
    private const val KEY_LANGUAGE = "selected_language_code"

    // Languages strictly sorted by total worldwide speakers (most spoken to least spoken)
    val sortedLanguages: List<LanguageOption> = listOf(
        LanguageOption(
            code = "en",
            nativeName = "English",
            englishName = "English",
            localizedNamePt = "Inglês",
            speakersGlobal = "1.50 Bilhões de falantes (#1 Global)",
            rank = 1,
            flag = "🇺🇸"
        ),
        LanguageOption(
            code = "zh",
            nativeName = "中文 (Mandarim)",
            englishName = "Mandarin Chinese",
            localizedNamePt = "Mandarim (Chinês)",
            speakersGlobal = "1.12 Bilhões de falantes (#2 Global)",
            rank = 2,
            flag = "🇨🇳"
        ),
        LanguageOption(
            code = "hi",
            nativeName = "हिन्दी",
            englishName = "Hindi",
            localizedNamePt = "Hindi",
            speakersGlobal = "610 Milhões de falantes (#3 Global)",
            rank = 3,
            flag = "🇮🇳"
        ),
        LanguageOption(
            code = "es",
            nativeName = "Español",
            englishName = "Spanish",
            localizedNamePt = "Espanhol",
            speakersGlobal = "550 Milhões de falantes (#4 Global)",
            rank = 4,
            flag = "🇪🇸"
        ),
        LanguageOption(
            code = "fr",
            nativeName = "Français",
            englishName = "French",
            localizedNamePt = "Francês",
            speakersGlobal = "310 Milhões de falantes (#5 Global)",
            rank = 5,
            flag = "🇫🇷"
        ),
        LanguageOption(
            code = "ar",
            nativeName = "العربية",
            englishName = "Standard Arabic",
            localizedNamePt = "Árabe",
            speakersGlobal = "275 Milhões de falantes (#6 Global)",
            rank = 6,
            flag = "🇸🇦"
        ),
        LanguageOption(
            code = "bn",
            nativeName = "বাংলা",
            englishName = "Bengali",
            localizedNamePt = "Bengali",
            speakersGlobal = "270 Milhões de falantes (#7 Global)",
            rank = 7,
            flag = "🇧🇩"
        ),
        LanguageOption(
            code = "pt",
            nativeName = "Português (Brasil)",
            englishName = "Portuguese",
            localizedNamePt = "Português",
            speakersGlobal = "260 Milhões de falantes (#8 Global)",
            rank = 8,
            flag = "🇧🇷"
        ),
        LanguageOption(
            code = "ru",
            nativeName = "Русский",
            englishName = "Russian",
            localizedNamePt = "Russo",
            speakersGlobal = "255 Milhões de falantes (#9 Global)",
            rank = 9,
            flag = "🇷🇺"
        ),
        LanguageOption(
            code = "ur",
            nativeName = "اردو",
            englishName = "Urdu",
            localizedNamePt = "Urdu",
            speakersGlobal = "230 Milhões de falantes (#10 Global)",
            rank = 10,
            flag = "🇵🇰"
        ),
        LanguageOption(
            code = "id",
            nativeName = "Bahasa Indonesia",
            englishName = "Indonesian",
            localizedNamePt = "Indonésio",
            speakersGlobal = "200 Milhões de falantes (#11 Global)",
            rank = 11,
            flag = "🇮🇩"
        ),
        LanguageOption(
            code = "de",
            nativeName = "Deutsch",
            englishName = "German",
            localizedNamePt = "Alemão",
            speakersGlobal = "135 Milhões de falantes (#12 Global)",
            rank = 12,
            flag = "🇩🇪"
        ),
        LanguageOption(
            code = "ja",
            nativeName = "日本語",
            englishName = "Japanese",
            localizedNamePt = "Japonês",
            speakersGlobal = "125 Milhões de falantes (#13 Global)",
            rank = 13,
            flag = "🇯🇵"
        ),
        LanguageOption(
            code = "it",
            nativeName = "Italiano",
            englishName = "Italian",
            localizedNamePt = "Italiano",
            speakersGlobal = "85 Milhões de falantes (#14 Global)",
            rank = 14,
            flag = "🇮🇹"
        )
    )

    fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "pt") ?: "pt"
    }

    fun saveLanguage(context: Context, code: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, code).apply()
    }

    fun getLanguageByCode(code: String): LanguageOption {
        return sortedLanguages.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: sortedLanguages[7] // Default PT
    }

    // Dynamic localization translation helper for UI strings across all tabs and components
    fun translate(key: String, langCode: String): String {
        val isEn = langCode.equals("en", ignoreCase = true)
        val isEs = langCode.equals("es", ignoreCase = true)
        val isFr = langCode.equals("fr", ignoreCase = true)
        val isDe = langCode.equals("de", ignoreCase = true)
        val isZh = langCode.equals("zh", ignoreCase = true)
        val isJa = langCode.equals("ja", ignoreCase = true)
        val isIt = langCode.equals("it", ignoreCase = true)

        return when (key) {
            // Navigation Tabs
            "tab_garage" -> when {
                isEn -> "Garage"
                isEs -> "Garaje"
                isFr -> "Garage"
                isDe -> "Garage"
                isZh -> "车库"
                isJa -> "ガレージ"
                isIt -> "Garage"
                else -> "Garagem"
            }
            "tab_maintenance" -> when {
                isEn -> "Revisions & Parts"
                isEs -> "Revisiones y Piezas"
                isFr -> "Révisions & Pièces"
                isDe -> "Wartung & Teile"
                isZh -> "保养与配件"
                isJa -> "整備と部品"
                isIt -> "Revisioni e Pezzi"
                else -> "Revisões & Peças"
            }
            "tab_telemetry" -> when {
                isEn -> "Telemetry & Fuel"
                isEs -> "Telemetría y Combustible"
                isFr -> "Télémétrie & Carburant"
                isDe -> "Telemetrie & Kraftstoff"
                isZh -> "遥测与油耗"
                isJa -> "走行データと燃費"
                isIt -> "Telemetria & Carburante"
                else -> "Telemetria"
            }
            "tab_costs" -> when {
                isEn -> "Costs & TCO"
                isEs -> "Costos y Tasas"
                isFr -> "Coûts & TCO"
                isDe -> "Kosten & TCO"
                isZh -> "费用与总成本"
                isJa -> "費用と維持費"
                isIt -> "Costi e TCO"
                else -> "Custos & FIPE"
            }
            "tab_more" -> when {
                isEn -> "More & SOS"
                isEs -> "Más y SOS"
                isFr -> "Plus & SOS"
                isDe -> "Mehr & SOS"
                isZh -> "更多与求救"
                isJa -> "その他とSOS"
                isIt -> "Altro & SOS"
                else -> "Mais & SOS"
            }

            // Modes of intervention
            "mode_replacement" -> when {
                isEn -> "Replacement"
                isEs -> "Reemplazo"
                isFr -> "Remplacement"
                isDe -> "Austausch"
                isZh -> "更换"
                isJa -> "交換"
                isIt -> "Sostituzione"
                else -> "Troca"
            }
            "mode_cleaning" -> when {
                isEn -> "Cleaning"
                isEs -> "Limpieza"
                isFr -> "Nettoyage"
                isDe -> "Reinigung"
                isZh -> "清洁保养"
                isJa -> "洗浄・クリーニング"
                isIt -> "Pulizia"
                else -> "Limpeza"
            }
            "mode_adjustment" -> when {
                isEn -> "Adjustment / Tuning"
                isEs -> "Ajuste / Calibración"
                isFr -> "Réglage"
                isDe -> "Einstellung"
                isZh -> "调整校准"
                isJa -> "調整・チューニング"
                isIt -> "Regolazione"
                else -> "Ajuste"
            }
            "mode_upgrade" -> when {
                isEn -> "Upgrade / Improvement"
                isEs -> "Mejora / Upgrade"
                isFr -> "Amélioration"
                isDe -> "Upgrade / Verbesserung"
                isZh -> "升级改装"
                isJa -> "アップグレード"
                isIt -> "Miglioramento"
                else -> "Melhoria"
            }
            "mode_inspection" -> when {
                isEn -> "Inspection"
                isEs -> "Inspección"
                isFr -> "Inspection"
                isDe -> "Inspektion"
                isZh -> "检查检验"
                isJa -> "点検・検査"
                isIt -> "Ispezione"
                else -> "Inspeção"
            }
            "mode_lubrication" -> when {
                isEn -> "Lubrication"
                isEs -> "Lubricación"
                isFr -> "Lubrification"
                isDe -> "Schmierung"
                isZh -> "润滑"
                isJa -> "潤滑・注油"
                isIt -> "Lubrificazione"
                else -> "Lubrificação"
            }

            // Common actions
            "action_save" -> when {
                isEn -> "Save"
                isEs -> "Guardar"
                isFr -> "Enregistrer"
                isDe -> "Speichern"
                isZh -> "保存"
                isJa -> "保存"
                isIt -> "Salva"
                else -> "Salvar"
            }
            "action_cancel" -> when {
                isEn -> "Cancel"
                isEs -> "Cancelar"
                isFr -> "Annuler"
                isDe -> "Abbrechen"
                isZh -> "取消"
                isJa -> "キャンセル"
                isIt -> "Annulla"
                else -> "Cancelar"
            }
            "action_search_part" -> when {
                isEn -> "Search part or accessory..."
                isEs -> "Buscar pieza o accesorio..."
                isFr -> "Rechercher une pièce ou accessoire..."
                isDe -> "Teil oder Zubehör suchen..."
                isZh -> "搜索配件或耗材..."
                isJa -> "パーツやアクセサリーを検索..."
                isIt -> "Cerca ricambio o accessorio..."
                else -> "Buscar peça ou acessório..."
            }
            "save_as_custom" -> when {
                isEn -> "Save as custom part"
                isEs -> "Guardar como pieza personalizada"
                isFr -> "Enregistrer comme pièce personnalisée"
                isDe -> "Als benutzerdefiniertes Teil speichern"
                isZh -> "另存为自定义配件"
                isJa -> "カスタムパーツとして保存"
                isIt -> "Salva come pezzo personalizzato"
                else -> "Salvar como peça customizada"
            }
            "status_ok" -> when {
                isEn -> "Up to date"
                isEs -> "Al día"
                isFr -> "À jour"
                isDe -> "In Ordnung"
                isZh -> "正常良好"
                isJa -> "順調"
                isIt -> "In regola"
                else -> "Em dia"
            }
            "status_attention" -> when {
                isEn -> "Attention"
                isEs -> "Atención"
                isFr -> "Attention"
                isDe -> "Achtung"
                isZh -> "注意临期"
                isJa -> "注意"
                isIt -> "Attenzione"
                else -> "Atenção"
            }
            "status_overdue" -> when {
                isEn -> "Overdue"
                isEs -> "Vencida"
                isFr -> "En retard"
                isDe -> "Überfällig"
                isZh -> "已逾期"
                isJa -> "期限超過"
                isIt -> "Scaduta"
                else -> "Vencida"
            }
            else -> key
        }
    }
}
