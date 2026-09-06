package com.example.data.model

enum class InterventionType(
    val id: String,
    val labelPt: String,
    val shortLabelPt: String,
    val badgeEmoji: String,
    val colorHex: String,
    val descriptionPt: String
) {
    REPLACEMENT(
        id = "TROCA",
        labelPt = "Troca / Substituição",
        shortLabelPt = "Troca",
        badgeEmoji = "🔄",
        colorHex = "#E53935",
        descriptionPt = "Substituição completa da peça gasta por uma nova."
    ),
    CLEANING(
        id = "LIMPEZA",
        labelPt = "Limpeza / Higienização",
        shortLabelPt = "Limpeza",
        badgeEmoji = "🧼",
        colorHex = "#0288D1",
        descriptionPt = "Lavagem técnica, desengraxe, descarbonização ou higienização."
    ),
    ADJUSTMENT(
        id = "AJUSTE",
        labelPt = "Ajuste / Regulagem",
        shortLabelPt = "Ajuste",
        badgeEmoji = "⚙️",
        colorHex = "#F57C00",
        descriptionPt = "Regulagem fina, reaperto de torque, sangria, alinhamento ou calibragem."
    ),
    UPGRADE(
        id = "MELHORIA",
        labelPt = "Melhoria / Upgrade",
        shortLabelPt = "Melhoria",
        badgeEmoji = "🚀",
        colorHex = "#2E7D32",
        descriptionPt = "Instalação de componente de melhor performance ou acessório novo."
    ),
    INSPECTION(
        id = "INSPECAO",
        labelPt = "Inspeção / Diagnóstico",
        shortLabelPt = "Inspeção",
        badgeEmoji = "🔍",
        colorHex = "#7B1FA2",
        descriptionPt = "Checagem visual, medição de folgas, teste de bateria ou scanner."
    ),
    LUBRICATION(
        id = "LUBRIFICACAO",
        labelPt = "Lubrificação",
        shortLabelPt = "Lubrificação",
        badgeEmoji = "🛢️",
        colorHex = "#D84315",
        descriptionPt = "Aplicação de graxa especial, óleo de corrente ou cera lubrificante."
    );

    companion object {
        fun fromId(id: String?): InterventionType {
            if (id.isNullOrBlank()) return REPLACEMENT
            return values().firstOrNull {
                it.id.equals(id, ignoreCase = true) ||
                it.name.equals(id, ignoreCase = true) ||
                it.shortLabelPt.equals(id, ignoreCase = true)
            } ?: REPLACEMENT
        }
    }
}
