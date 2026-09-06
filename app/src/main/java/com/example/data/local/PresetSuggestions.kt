package com.example.data.local

import com.example.data.model.VehicleType

data class SuggestionItem(
    val name: String,
    val category: String,
    val isPart: Boolean, // true = Peça, false = Serviço
    val estimatedPrice: Double,
    val forVehicleType: String = "BOTH", // "CAR", "MOTORCYCLE", "BICYCLE", "E_BIKE", "TRUCK", "BOTH", "ALL"
    val defaultIntervalKm: Int = 10000,
    val defaultIntervalMonths: Int = 6,
    val description: String = "",
    val defaultMode: String = "TROCA", // "TROCA", "LIMPEZA", "AJUSTE", "MELHORIA", "INSPECAO", "LUBRIFICACAO"
    val isCustom: Boolean = false
)

data class RevisionTemplate(
    val title: String,
    val description: String,
    val category: String,
    val vehicleType: VehicleType,
    val intervalKm: Int,
    val intervalMonths: Int,
    val items: List<SuggestionItem>
)

object PresetSuggestions {

    val allSuggestions: List<SuggestionItem> = listOf(
        // ==========================================
        // 🚲 BICICLETA & E-BIKE (TODAS AS PEÇAS E ACESSÓRIOS)
        // ==========================================
        // Transmissão de Bicicleta
        SuggestionItem(
            name = "Corrente de Bicicleta (8v/9v/10v/11v/12v)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 120.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 2000,
            defaultIntervalMonths = 6,
            description = "Substituição preventiva com medidor de desgaste para não desgastar o cassete.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Limpeza Profunda & Lubrificação de Corrente (Cera/Óleo)",
            category = "Transmissão",
            isPart = false,
            estimatedPrice = 30.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 300,
            defaultIntervalMonths = 1,
            description = "Desengraxe e aplicação de lubrificante seco ou úmido específico para correntes.",
            defaultMode = "LIMPEZA"
        ),
        SuggestionItem(
            name = "Cassete / Catraca (Roda Livre)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 220.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 12,
            description = "Conjunto de engrenagens traseiras. Trocar quando houver dentes 'tubarão' ou corrente pulando.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Pedivela & Coroas (Chainring)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 280.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 8000,
            defaultIntervalMonths = 24,
            description = "Braços do pedal e engrenagens dianteiras (Direct Mount ou BCD).",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Movimento Central (Hollowtech / Ponta Quadrada)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 12,
            description = "Eixo e rolamentos onde o pedivela gira. Elimina estalos e folgas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Pedais (Plataforma ou Clip SPD)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 150.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 24,
            description = "Pedais com rolamentos selados ou sistema de encaixe para sapatilha.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Câmbio Traseiro",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 260.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 24,
            description = "Desviador traseiro de marchas com embreagem / cage longo ou médio.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Câmbio Dianteiro",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 130.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 12000,
            defaultIntervalMonths = 36,
            description = "Desviador dianteiro para sistemas com 2 ou 3 coroas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Roldanas do Câmbio Traseiro",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 50.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 4000,
            defaultIntervalMonths = 12,
            description = "Polias guia e tensora do câmbio traseiro. Roldanas gastas deixam as trocas imprecisas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Trocadores de Marcha (Shifters / Rapid Fire)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 180.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Alavancas de acionamento das marchas no guidão.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Cabos de Aço e Conduítes de Câmbio",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 3000,
            defaultIntervalMonths = 6,
            description = "Cabos teflonados e conduítes com lubrificação interna para marchas macias.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Regulagem de Marchas & Alinhamento da Gancheira",
            category = "Transmissão",
            isPart = false,
            estimatedPrice = 50.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 1500,
            defaultIntervalMonths = 3,
            description = "Alinhamento com ferramenta específica da gancheira e ajuste dos parafusos H/L e tensão B.",
            defaultMode = "AJUSTE"
        ),

        // Freios de Bicicleta
        SuggestionItem(
            name = "Pastilhas de Freio a Disco (Resina / Metálica)",
            category = "Freios",
            isPart = true,
            estimatedPrice = 70.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 2000,
            defaultIntervalMonths = 6,
            description = "Pastilhas para freio hidráulico ou mecânico. Substituir antes de desgastar até o metal.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Sapatas de Freio V-Brake / Caliper",
            category = "Freios",
            isPart = true,
            estimatedPrice = 35.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 2000,
            defaultIntervalMonths = 6,
            description = "Borrachas de atrito para aros de alumínio.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Discos de Freio Rotor (160mm / 180mm / 203mm)",
            category = "Freios",
            isPart = true,
            estimatedPrice = 120.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 6000,
            defaultIntervalMonths = 18,
            description = "Rotores de aço inox (6 furos ou Center Lock). Verificar espessura mínima de 1.5mm.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Sangria de Freio Hidráulico (Óleo Mineral / DOT)",
            category = "Freios",
            isPart = false,
            estimatedPrice = 60.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 3000,
            defaultIntervalMonths = 6,
            description = "Eliminação de bolhas de ar e substituição de fluido hidráulico para restaurar pressão firme na manete.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Manetes de Freio e Calipers / Pinças",
            category = "Freios",
            isPart = true,
            estimatedPrice = 280.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Conjunto de pinças e manetes de freio a disco hidráulico.",
            defaultMode = "MELHORIA"
        ),

        // Rodas, Raios e Pneus de Bicicleta
        SuggestionItem(
            name = "Pneus de Bicicleta (MTB Kevlar / Speed / Urbano)",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 180.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 3500,
            defaultIntervalMonths = 12,
            description = "Pneus de alta rolagem e aderência com proteção antifuro (Aramado ou Dobrável Kevlar).",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Câmaras de Ar (Válvula Presta / Schrader)",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 35.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 2000,
            defaultIntervalMonths = 12,
            description = "Câmara em borracha butílica com válvula fina (Presta) ou bico grosso (Schrader).",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Líquido Selante Tubeless (Reposição)",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 1000,
            defaultIntervalMonths = 3,
            description = "Reposição de 60ml a 100ml de selante líquido antifuro para sistemas sem câmara.",
            defaultMode = "LUBRIFICACAO"
        ),
        SuggestionItem(
            name = "Fita de Aro Tubeless & Bicos de Válvula",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 65.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 18,
            description = "Fita vedante de alta pressão e válvulas com núcleo removível para conversão tubeless.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Centragem de Rodas & Tensão de Raios",
            category = "Rodas & Pneus",
            isPart = false,
            estimatedPrice = 40.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 1500,
            defaultIntervalMonths = 6,
            description = "Eliminação de empenos radiais/laterais e ajuste uniforme de tensão dos raios.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Cubos de Roda & Rolamentos (Freehub HG/XD)",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 250.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 8000,
            defaultIntervalMonths = 24,
            description = "Revisão e engraxe dos rolamentos ou substituição do núcleo freehub.",
            defaultMode = "LUBRIFICACAO"
        ),

        // Quadro, Cockpit & Suspensão de Bicicleta
        SuggestionItem(
            name = "Suspensão Dianteira a Ar/Mola (Garfo)",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 850.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 12000,
            defaultIntervalMonths = 36,
            description = "Garfo telescópico com trava no guidão e ajuste de retorno (Rebound).",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Revisão de Suspensão (Troca de Óleo e Retentores)",
            category = "Suspensão",
            isPart = false,
            estimatedPrice = 140.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 2000,
            defaultIntervalMonths = 6,
            description = "Desmontagem das canelas, limpeza interna, novos raspadores/retentores e óleo hidráulico 5wt/15wt.",
            defaultMode = "LIMPEZA"
        ),
        SuggestionItem(
            name = "Amortecedor Traseiro Shock a Ar",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 950.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 12000,
            defaultIntervalMonths = 36,
            description = "Shock para bicicletas full suspension com câmara de ar positiva/negativa.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Caixa de Direção (Headset Integrada/Tapered)",
            category = "Quadro & Cockpit",
            isPart = true,
            estimatedPrice = 80.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 12,
            description = "Rolamentos da espiga do garfo no quadro. Evita folgas no guidão.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Guidão e Mesa / Avanço (Stem)",
            category = "Quadro & Cockpit",
            isPart = true,
            estimatedPrice = 160.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 48,
            description = "Componentes de controle de pilotagem em alumínio ou carbono.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Manoplas com Trava / Fita de Guidão Speed",
            category = "Quadro & Cockpit",
            isPart = true,
            estimatedPrice = 55.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 3000,
            defaultIntervalMonths = 12,
            description = "Grips ergonômicos em silicone ou borracha com trava lock-on para evitar escorregar.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Selim Ergonômico Antiprostático (Banco)",
            category = "Quadro & Cockpit",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 24,
            description = "Selim com canal vazado para alívio de pressão perineal em pedaladas longas.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Canote de Selim Retrátil (Dropper Post)",
            category = "Quadro & Cockpit",
            isPart = true,
            estimatedPrice = 650.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 8000,
            defaultIntervalMonths = 24,
            description = "Canote hidráulico com acionamento no guidão para baixar o banco em descidas técnicas.",
            defaultMode = "MELHORIA"
        ),

        // Componentes Específicos de Bicicleta Elétrica (E-Bike)
        SuggestionItem(
            name = "Bateria de Lítio E-Bike (36V / 48V)",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 1800.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Pack de baterias de íon de lítio com BMS integrado. Verificar saúde das células e autonomia.",
            defaultMode = "INSPECAO"
        ),
        SuggestionItem(
            name = "Motor Central (Mid-Drive) / Motor de Cubo",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 1600.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 48,
            description = "Motor elétrico brushless de 250W a 750W. Limpeza e verificação de engrenagens de nylon.",
            defaultMode = "INSPECAO"
        ),
        SuggestionItem(
            name = "Display LCD & Painel de Controle de Potência",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 320.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Computador de bordo da E-Bike com níveis de assistência, velocímetro e odômetro.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Sensor de Pedalada Assistida (PAS) e Sensor de Torque",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 8000,
            defaultIntervalMonths = 24,
            description = "Sensor magnético do movimento central que aciona a assistência do motor ao pedalar.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Chicote Elétrico e Conectores à Prova d'Água (IP65)",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 90.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 24,
            description = "Cabos de conexão entre motor, bateria, acelerador e sensor de freio.",
            defaultMode = "INSPECAO"
        ),
        SuggestionItem(
            name = "Carregador Inteligente de Bateria E-Bike",
            category = "Elétrica & Motor E-Bike",
            isPart = true,
            estimatedPrice = 190.0,
            forVehicleType = "E_BIKE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Fonte bivolt automática com corte térmico e proteção contra sobrecarga.",
            defaultMode = "INSPECAO"
        ),

        // Acessórios de Bicicleta e Ciclista
        SuggestionItem(
            name = "Capacete de Ciclismo com Tecnologia MIPS",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 280.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 36,
            description = "Equipamento essencial de segurança com proteção contra impactos rotacionais.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Farol Dianteiro LED Recarregável USB (800+ Lumens)",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 24,
            description = "Iluminação frontal potente para pedal noturno e trilhas.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Lanterna Traseira LED com Sensor de Frenagem",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 75.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 24,
            description = "Luz de presença traseira com sensor que acende forte ao frear.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Ciclocomputador GPS com Integração Strava",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 420.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Monitoramento de velocidade, altimetria, cadência e mapeamento de rotas.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Suporte de Caramanhola & Garrafa Térmica",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 60.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 24,
            description = "Suporte no quadro e caramanhola térmica para hidratação durante o pedal.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Para-lamas Dianteiro & Traseiro",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 70.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 8000,
            defaultIntervalMonths = 24,
            description = "Proteção contra lama, cascalho e água respingada nas pernas e costas.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Bagageiro Traseiro & Alforges / Bolsa de Viagem",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 180.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 36,
            description = "Suporte em alumínio para transporte de carga, compras ou bikepacking.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Bomba de Ar Portátil com Manômetro & Espátulas",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 85.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 36,
            description = "Kit essencial de emergência fixado no quadro para encher pneus em caso de furo.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Cadeado U-Lock em Aço Temperado / Trava Articulada",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "BICYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 48,
            description = "Segurança máxima antifurto para prender o quadro e rodas em bicicletários.",
            defaultMode = "MELHORIA"
        ),

        // ==========================================
        // 🏍️ MOTOCICLETA (TODAS AS PEÇAS E ACESSÓRIOS)
        // ==========================================
        SuggestionItem(
            name = "Óleo de Motor 4T (10W30 / 10W40 / 20W50)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 55.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 3000,
            defaultIntervalMonths = 6,
            description = "Lubrificante com aditivos JASO MA2 específico para motos com embreagem banhada a óleo.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Óleo de Moto",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 30.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 3000,
            defaultIntervalMonths = 6,
            description = "Filtro metálico ou papel cartridge. Trocar junto com o óleo do motor.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Ar de Moto (Espuma ou Papel)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 6000,
            defaultIntervalMonths = 12,
            description = "Filtro lavável em espuma ou elemento de papel selado.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Vela de Ignição Iridium / Convencional (Moto)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 75.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 12000,
            defaultIntervalMonths = 12,
            description = "Faísca estável para resposta imediata do acelerador e economia de combustível.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Regulagem de Válvulas de Admissão e Escape",
            category = "Motor & Óleo",
            isPart = false,
            estimatedPrice = 90.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 12,
            description = "Ajuste com cálibre de lâminas na folga recomendada de fábrica para motor frio.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Limpeza de Bico Injetor / Carburador",
            category = "Motor & Óleo",
            isPart = false,
            estimatedPrice = 70.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 12,
            description = "Limpeza ultrassônica e equalização da vazão de combustível.",
            defaultMode = "LIMPEZA"
        ),
        SuggestionItem(
            name = "Kit Relação Completo (Coroa, Pinhão e Corrente com Retentor)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 280.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 18,
            description = "Transmissão secundária com retentores O-ring para maior durabilidade.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Limpeza & Lubrificação da Corrente de Transmissão",
            category = "Transmissão",
            isPart = false,
            estimatedPrice = 35.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 1000,
            defaultIntervalMonths = 1,
            description = "Limpeza com querosene/desengraxante e lubrificação com spray C4 ou óleo 90.",
            defaultMode = "LUBRIFICACAO"
        ),
        SuggestionItem(
            name = "Coxins da Coroa (Buchas da Roda Traseira)",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 40.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 18,
            description = "Borrachas amortecedoras do cubo traseiro que evitam trancos nas arrancadas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Pastilhas de Freio Dianteiras Sinterizadas",
            category = "Freios",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 12,
            description = "Pastilhas de alta fricção e frenagem precisa em piso seco ou molhado.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Lonas de Freio / Pastilhas Traseiras (Moto)",
            category = "Freios",
            isPart = true,
            estimatedPrice = 65.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 18000,
            defaultIntervalMonths = 18,
            description = "Sapatas para freio a tambor ou pastilha traseira a disco.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Fluido de Freio DOT 4 / 5.1 e Sangria (Moto)",
            category = "Freios",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 18,
            description = "Substituição completa do fluido dos reservatórios dianteiro e traseiro.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Óleo de Bengala (Garfo Dianteiro) e Retentores",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 18,
            description = "Fluido hidráulico Fork Oil 10W/15W e retentores para evitar vazamento nos cilindros.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Rolamentos da Caixa de Direção Cônicos",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 95.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 24,
            description = "Elimina calos, folgas e rigidez ao esterçar a motocicleta.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Pneu Dianteiro de Moto",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 260.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 18000,
            defaultIntervalMonths = 24,
            description = "Pneu de perfil esportivo, trail ou urbano com banda de rodagem segura.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Pneu Traseiro de Moto",
            category = "Rodas & Pneus",
            isPart = true,
            estimatedPrice = 320.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 12000,
            defaultIntervalMonths = 18,
            description = "Pneu de tração traseira com indicador TWI de desgaste.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Bateria Selada / Gel 12V (Moto)",
            category = "Elétrica",
            isPart = true,
            estimatedPrice = 190.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 25000,
            defaultIntervalMonths = 24,
            description = "Bateria livre de manutenção com alto CCA para partida elétrica rápida.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Lâmpada do Farol LED / H4 (Moto)",
            category = "Elétrica",
            isPart = true,
            estimatedPrice = 65.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 24,
            description = "Iluminação branca de alto alcance para visibilidade no trânsito.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Baú / Top Case (28L a 45L) e Suporte Reforçado",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 240.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 36,
            description = "Baú com chave para guardar capacete, capa de chuva e pertences.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Slider de Chassi / Protetor de Carenagem",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 180.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 25000,
            defaultIntervalMonths = 48,
            description = "Protege motor, radiador e carenagem contra quedas e tombos laterais.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Antena Corta-Pipa Retrátil de Aço",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 35.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 36,
            description = "Acessório indispensável de segurança contra linhas com cerol e chilenas.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Suporte de Celular com Carregador USB por Indução",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 95.0,
            forVehicleType = "MOTORCYCLE",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 36,
            description = "Fixação no guidão ou retrovisor com conexão na bateria da moto.",
            defaultMode = "MELHORIA"
        ),

        // ==========================================
        // 🚗 CARRO (TODAS AS PEÇAS E ACESSÓRIOS)
        // ==========================================
        SuggestionItem(
            name = "Óleo do Motor Sintético (0W20 / 5W30 / 5W40)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 190.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Óleo lubrificante com especificação API SP / ILSAC GF-6 para motores flex e turbo.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Óleo (Carro)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Elemento filtrante de óleo. Trocar sempre junto com o óleo do motor.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Ar do Motor (Carro)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 60.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 12,
            description = "Retém poeira e detritos, garantindo ar limpo na câmara de combustão.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Combustível (Carro)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 45.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 12,
            description = "Protege bicos injetores e bomba de combustível contra impurezas do tanque.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Velas de Ignição Iridium / Platina (Carro)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 180.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 24,
            description = "Jogo de 3 ou 4 velas. Velas gastas aumentam o consumo de combustível e provocam falhas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Correia Dentada / Sincronizadora e Tensor",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 380.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 45000,
            defaultIntervalMonths = 36,
            description = "Item de segurança crítica do motor. Se quebrar, empena válvulas e destrói o cabeçote.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Correia de Acessórios (Poly-V) e Polias",
            category = "Transmissão",
            isPart = true,
            estimatedPrice = 95.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 36,
            description = "Movimenta alternador, compressor do ar-condicionado e bomba de água/direção.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Líquido de Arrefecimento (Aditivo Orgânico + Água Desmineralizada)",
            category = "Arrefecimento",
            isPart = true,
            estimatedPrice = 90.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 24,
            description = "Protege contra superaquecimento, fervura e inibe corrosão nas galerias do motor.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Limpeza do Sistema de Arrefecimento & Radiador",
            category = "Arrefecimento",
            isPart = false,
            estimatedPrice = 110.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 24,
            description = "Esgotamento, circulação de água limpa para lavagem e pressurização para teste de vazamentos.",
            defaultMode = "LIMPEZA"
        ),
        SuggestionItem(
            name = "Pastilhas de Freio Dianteiras Cerâmica / Orgânica",
            category = "Freios",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 12,
            description = "Principal componente de atrito da frenagem. Trocar antes de marcar os discos.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Discos de Freio Dianteiros Ventilados",
            category = "Freios",
            isPart = true,
            estimatedPrice = 320.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 36,
            description = "Verificar espessura com micrômetro e empenamento que gera trepidação no pedal.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Fluido de Freio DOT 4 / 5.1 e Sangria Eletrônica ABS",
            category = "Freios",
            isPart = true,
            estimatedPrice = 85.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 24,
            description = "Substituição com sangria automatizada para módulo do ABS.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Amortecedores Dianteiros e Traseiros (com Batentes)",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 750.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 50000,
            defaultIntervalMonths = 48,
            description = "Amortecedores pressurizados com kit de batentes, coifas e rolamentos superiores.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Bieletas da Barra Estabilizadora e Buchas",
            category = "Suspensão",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 25000,
            defaultIntervalMonths = 24,
            description = "Elimina estalos e rangidos ao passar por ondulações e valetas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Alinhamento 3D e Balanceamento das 4 Rodas",
            category = "Suspensão",
            isPart = false,
            estimatedPrice = 110.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Geometria de suspensão e balanceamento dinâmico para evitar desgaste prematuro dos pneus.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Rodízio dos 4 Pneus",
            category = "Suspensão",
            isPart = false,
            estimatedPrice = 40.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Inversão entre eixos para uniformizar o desgaste da borracha.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Filtro de Cabine / Ar-Condicionado",
            category = "Climatização",
            isPart = true,
            estimatedPrice = 50.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Filtro com carvão ativado contra ácaros, pólen e odores externos.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Higienização do Ar-Condicionado com Ozônio",
            category = "Climatização",
            isPart = false,
            estimatedPrice = 75.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 10000,
            defaultIntervalMonths = 6,
            description = "Elimina bactérias e fungos nos dutos de ar sem resíduos químicos.",
            defaultMode = "LIMPEZA"
        ),
        SuggestionItem(
            name = "Bateria 12V 60Ah / EFB / AGM (Start-Stop)",
            category = "Elétrica",
            isPart = true,
            estimatedPrice = 420.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 30,
            description = "Bateria com garantia de fábrica e teste de carga no alternador.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Palhetas do Limpador de Para-brisa (Silicone / Flat Blade)",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 65.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 15000,
            defaultIntervalMonths = 12,
            description = "Varredura silenciosa e sem marcas no vidro dianteiro e traseiro.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Sensor de Estacionamento & Câmera de Ré",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 250.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 48,
            description = "Auxílio para manobras de ré com linhas dinâmicas de guia.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Película de Proteção Solar (Insulfilm Térmico)",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 300.0,
            forVehicleType = "CAR",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 48,
            description = "Redução térmica e proteção contra raios UV conforme limites do Contran.",
            defaultMode = "MELHORIA"
        ),

        // ==========================================
        // 🚛 CAMINHÃO / LINHA PESADA (TODAS AS PEÇAS E ACESSÓRIOS)
        // ==========================================
        SuggestionItem(
            name = "Óleo Motor Diesel 15W40 CI-4 / CK-4 (20L a 30L)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 580.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 6,
            description = "Óleo mineral / semissintético de alta viscosidade para trabalho pesado e proteção contra fuligem.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Óleo Lubrificante Duplo (Caminhão)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 6,
            description = "Filtros blindados de fluxo total e bypass para motor diesel.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Combustível Separador de Água (Racor)",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 110.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 20000,
            defaultIntervalMonths = 6,
            description = "Drena água do diesel antes dos bicos injetores e bomba de alta pressão.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro de Ar Primário e Secundário de Alta Eficiência",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 220.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 12,
            description = "Conjunto duplo com indicador de restrição para estradas com poeira intensa.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro do Sistema Arla 32 & Bomba Dosadora SCR",
            category = "Motor & Óleo",
            isPart = true,
            estimatedPrice = 130.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 12,
            description = "Filtro de ureia para o catalisador SCR de redução de emissões Euro 5 / Euro 6.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Filtro Secador de Ar do Sistema Pneumático (Válvula APU)",
            category = "Freios Pneumáticos",
            isPart = true,
            estimatedPrice = 160.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 12,
            description = "Filtro coalescente que elimina umidade e óleo das linhas de freio a ar.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Lonas de Freio Pesadas e Rebites (Por Eixo)",
            category = "Freios Pneumáticos",
            isPart = true,
            estimatedPrice = 240.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 12,
            description = "Lonas de freio reforçadas moldadas para tambores de 410mm.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Cuícas de Freio Simples / Dupla Spring Brake",
            category = "Freios Pneumáticos",
            isPart = true,
            estimatedPrice = 290.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 24,
            description = "Cilindro pneumático com mola de freio de emergência/estacionamento.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Bolsas de Ar da Suspensão Pneumática (Fole)",
            category = "Suspensão & Rodagem",
            isPart = true,
            estimatedPrice = 450.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 80000,
            defaultIntervalMonths = 36,
            description = "Bolsa de borracha reforçada com cordéis de náilon para amortecimento de carga.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Feixes de Molas, Pinos e Grampos de Eixo",
            category = "Suspensão & Rodagem",
            isPart = true,
            estimatedPrice = 650.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 24,
            description = "Revisão e reaperto dos grampos em U e arqueamento de molas mestras.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Pneus de Carga Pesada (Direcional, Tração ou Carreta)",
            category = "Suspensão & Rodagem",
            isPart = true,
            estimatedPrice = 1900.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 70000,
            defaultIntervalMonths = 24,
            description = "Pneu radial 295/80R22.5 com banda reforçada para alta quilometragem.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Recapagem / Vulcanização a Frio de Pneus de Caminhão",
            category = "Suspensão & Rodagem",
            isPart = false,
            estimatedPrice = 650.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 18,
            description = "Aplicação de nova banda pré-moldada em carcaças aprovadas por ultrassom.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Alinhamento a Laser de Todos os Eixos (Cavalo & Carreta)",
            category = "Suspensão & Rodagem",
            isPart = false,
            estimatedPrice = 280.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 6,
            description = "Alinhamento a laser dos eixos dianteiro, tração e carretas para economizar combustível e pneus.",
            defaultMode = "AJUSTE"
        ),
        SuggestionItem(
            name = "Graxa Especial para Quinta Roda & Pino Rei",
            category = "Transmissão & Chassi",
            isPart = false,
            estimatedPrice = 60.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 5000,
            defaultIntervalMonths = 2,
            description = "Engraxe com composto de lítio/bissulfeto de molibdênio na mesa de engate.",
            defaultMode = "LUBRIFICACAO"
        ),
        SuggestionItem(
            name = "Óleo de Câmbio Pesado 80W90 e Diferencial 85W140",
            category = "Transmissão & Chassi",
            isPart = true,
            estimatedPrice = 380.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 24,
            description = "Substituição do lubrificante de engrenagens hipoides do diferencial e caixa de marchas.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Cruzetas e Mancal Central do Cardan",
            category = "Transmissão & Chassi",
            isPart = true,
            estimatedPrice = 320.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 40000,
            defaultIntervalMonths = 12,
            description = "Substituição e engraxe sob pressão dos bicos graxeiros do eixo cardan.",
            defaultMode = "LUBRIFICACAO"
        ),
        SuggestionItem(
            name = "Baterias 24V de Alta Capacidade (150Ah a 180Ah)",
            category = "Elétrica",
            isPart = true,
            estimatedPrice = 850.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 30,
            description = "Par de baterias reforçadas para partida de motor pesado e sistemas de bordo.",
            defaultMode = "TROCA"
        ),
        SuggestionItem(
            name = "Aferição e Certificação de Tacógrafo Digital (Inmetro)",
            category = "Elétrica",
            isPart = false,
            estimatedPrice = 220.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 50000,
            defaultIntervalMonths = 24,
            description = "Inspeção obrigatória de cronotacógrafo com emissão de certificado oficial.",
            defaultMode = "INSPECAO"
        ),
        SuggestionItem(
            name = "Sistema Rodoar / Calibrador Automático Contínuo",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 750.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 60000,
            defaultIntervalMonths = 36,
            description = "Mantém a calibragem correta dos pneus mesmo em caso de furo lento durante a viagem.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Climatizador de Teto com Reservatório de Água",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 890.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 50000,
            defaultIntervalMonths = 48,
            description = "Resfriamento ecológico da cabine para pernoite do caminhoneiro com motor desligado.",
            defaultMode = "MELHORIA"
        ),
        SuggestionItem(
            name = "Cintas de Amarração de Carga com Catraca (5 Toneladas)",
            category = "Acessórios",
            isPart = true,
            estimatedPrice = 140.0,
            forVehicleType = "TRUCK",
            defaultIntervalKm = 30000,
            defaultIntervalMonths = 24,
            description = "Cintas de poliéster de alta resistência para travamento seguro de carga.",
            defaultMode = "MELHORIA"
        )
    )

    // Templates prontos para cada veículo
    val templates: List<RevisionTemplate> = listOf(
        // Bicicleta
        RevisionTemplate(
            title = "Revisão Geral de Bicicleta (Preventiva)",
            description = "Revisão completa para bicicletas: regulagem fina de marchas, sangria/freios, centragem de rodas e lubrificação.",
            category = "Geral",
            vehicleType = VehicleType.BICYCLE,
            intervalKm = 1500,
            intervalMonths = 3,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Limpeza Profunda") },
                allSuggestions.first { it.name.startsWith("Regulagem de Marchas") },
                allSuggestions.first { it.name.startsWith("Pastilhas de Freio a Disco") },
                allSuggestions.first { it.name.startsWith("Centragem de Rodas") }
            )
        ),
        RevisionTemplate(
            title = "Revisão E-Bike (Motor & Bateria)",
            description = "Checagem de autonomia da bateria, conexões do motor elétrico, sensor de pedalada e freios com sensor de corte.",
            category = "Elétrica",
            vehicleType = VehicleType.E_BIKE,
            intervalKm = 3000,
            intervalMonths = 6,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Bateria de Lítio") },
                allSuggestions.first { it.name.startsWith("Sensor de Pedalada") },
                allSuggestions.first { it.name.startsWith("Chicote Elétrico") },
                allSuggestions.first { it.name.startsWith("Pastilhas de Freio a Disco") }
            )
        ),
        // Moto
        RevisionTemplate(
            title = "Revisão Periódica Moto (3.000 / 5.000 km)",
            description = "Troca de óleo de motor 4T, filtro, regulagem de corrente e inspeção geral de segurança.",
            category = "Geral",
            vehicleType = VehicleType.MOTORCYCLE,
            intervalKm = 3000,
            intervalMonths = 6,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Óleo de Motor 4T") },
                allSuggestions.first { it.name.startsWith("Filtro de Óleo de Moto") },
                allSuggestions.first { it.name.startsWith("Limpeza & Lubrificação da Corrente") },
                allSuggestions.first { it.name.startsWith("Pastilhas de Freio Dianteiras Sinterizadas") }
            )
        ),
        // Carro
        RevisionTemplate(
            title = "Troca de Óleo & Filtros (Carro)",
            description = "Troca de óleo sintético, filtro de óleo, filtro de ar e filtro de combustível recomendada a cada 10.000 km.",
            category = "Motor & Óleo",
            vehicleType = VehicleType.CAR,
            intervalKm = 10000,
            intervalMonths = 6,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Óleo do Motor Sintético") },
                allSuggestions.first { it.name == "Filtro de Óleo (Carro)" },
                allSuggestions.first { it.name == "Filtro de Ar do Motor (Carro)" },
                allSuggestions.first { it.name == "Filtro de Combustível (Carro)" }
            )
        ),
        RevisionTemplate(
            title = "Revisão dos 10.000 km (Carro)",
            description = "Pacote completo: óleo, todos os filtros, alinhamento 3D, balanceamento e higienização de cabine.",
            category = "Geral",
            vehicleType = VehicleType.CAR,
            intervalKm = 10000,
            intervalMonths = 6,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Óleo do Motor Sintético") },
                allSuggestions.first { it.name == "Filtro de Óleo (Carro)" },
                allSuggestions.first { it.name.startsWith("Alinhamento 3D") },
                allSuggestions.first { it.name.startsWith("Rodízio dos 4 Pneus") },
                allSuggestions.first { it.name.startsWith("Filtro de Cabine") }
            )
        ),
        // Caminhão
        RevisionTemplate(
            title = "Revisão Pesada de Lubrificação (Caminhão)",
            description = "Troca do cárter de óleo diesel (20L+), filtros de óleo duplos, racor e engraxe da quinta roda.",
            category = "Motor & Óleo",
            vehicleType = VehicleType.TRUCK,
            intervalKm = 20000,
            intervalMonths = 6,
            items = listOf(
                allSuggestions.first { it.name.startsWith("Óleo Motor Diesel") },
                allSuggestions.first { it.name.startsWith("Filtro de Óleo Lubrificante Duplo") },
                allSuggestions.first { it.name.startsWith("Filtro de Combustível Separador") },
                allSuggestions.first { it.name.startsWith("Graxa Especial para Quinta Roda") }
            )
        )
    )
}
