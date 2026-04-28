package com.tenko.app.data.model

val clinicalHistoryQuestions = listOf(
    // Datos Personales
    ClinicalQuestion("last_name", "Primer Apellido: ", AnswerType.Text),
    ClinicalQuestion("second_last_name", "Segundo Apellido: ", AnswerType.Text),
    ClinicalQuestion("birthdate", "Fecha de Nacimiento: ", AnswerType.DatePicker),


    // Identidad y Biología (Single Choice)
    ClinicalQuestion("sex_biology", "¿Cuál es su sexo registrado al nacer? ", AnswerType.SingleChoice(
        options = mapOf("femenine" to "Femenino", "masculine" to "Masculino")
    )),

    ClinicalQuestion("sex_legally", "¿Cuál es su sexo legal (el que aparece en su documentación oficial)? ", AnswerType.SingleChoice(
        options = mapOf(
            "femenine" to "Femenino",
            "masculine" to "Masculino",
            "non_binary" to "No binario",
            "prefer_not_to_say" to "Prefiero no decirlo"
        )
    )),


    // Screenings y Salud Mental (Binary)
    ClinicalQuestion("depression_screening", "¿Se siente usted frecuentemente triste o deprimido? ", AnswerType.Binary),
    ClinicalQuestion("depression", "¿Se le ha diagnosticado oficialmente depresión? ", AnswerType.Binary),

    ClinicalQuestion("memory_screening", "¿Ha olvidado más cosas que de costumbre?", AnswerType.Binary),
    ClinicalQuestion("memory_alterations", "¿Presenta alteraciones de memoria?", AnswerType.Binary),
    ClinicalQuestion("dementia", "¿Tiene diagnóstico o riesgo de demencia?", AnswerType.Binary),


    // Salud Física General
    ClinicalQuestion("urinary_incontinence_screening", "¿Ha tenido fuga de orina? ", AnswerType.Binary),
    ClinicalQuestion("urinary_incontinence", "¿Padece incontinencia urinaria?", AnswerType.Binary),

    ClinicalQuestion("anemia_screening", "¿Cree usted que padece anemia o presenta síntomas relacionados (como cansancio extremo o palidez)? ", AnswerType.Binary),
    ClinicalQuestion("obesity_screening", "¿Considera que su peso actual se encuentra por encima del rango saludable para su estatura? ", AnswerType.Binary),
    ClinicalQuestion("osteoporosis_screening", "¿Cree tener riesgo de padecer osteoporosis?", AnswerType.Binary),


    // Patologías y Estilo de Vida
    ClinicalQuestion("diabetes_mellitus", "¿Padece usted de algun tipo de Diabetes?", AnswerType.SingleChoice(
        options = mapOf(
            "none" to "Ninguna",
            "type_1" to "Tipo 1",
            "type_2" to "Tipo 2",
            "gestational" to "Gestacional",
            "prediabetes" to "Prediabetes"
        )
    )),


    ClinicalQuestion("arterial_hypertension", "¿Padece hipertensión arterial?", AnswerType.Binary),


    // Selección Múltiple (Checkboxes)
    ClinicalQuestion("sustance_use", "¿Consume alguna de estas sustancias?", AnswerType.MultiChoice(
        options = mapOf(
            "alcohol" to "Alcohol",
            "caffeine" to "Cafeína",
            "nicotine" to "Nicotina",
            "cannabis" to "Marihuana",
            "other" to "Otra"
        )
    )),

    ClinicalQuestion("std", "¿Le han diagnosticado alguna de las siguientes infecciones de transmisión sexual (ITS) en el pasado o actualmente? ", AnswerType.MultiChoice(
        options = mapOf(
            "none" to "Ninguna",
            "hpv" to "VPH",
            "chlamydia" to "Clamidia",
            "gonorrhea" to "Gonorrea",
            "herpes" to "Herpes",
            "syphilis" to "Sífilis",
            "hiv" to "VIH",
            "other" to "Otra"
        )
    )),


    // Salud Femenina
    ClinicalQuestion("turner_syndrome_screening", "¿Ha sido diagnosticada con Síndrome de Turner (una condición genética que ocurre cuando a una mujer le falta total o parcialmente un cromosoma X)? ", AnswerType.Binary),
    ClinicalQuestion("endometriosis_screening", "¿Cree usted que padece de endometriosis? (Esta es una condición donde un tejido similar al revestimiento del útero crece fuera de él, causando a menudo dolores menstruales intensos o dolor pélvico crónico). ", AnswerType.Binary),
    ClinicalQuestion("endometriosis", "¿Ha sido usted diagnosticada oficialmente con endometriosis por un profesional de la salud (mediante ecografía, resonancia o laparoscopia)? ", AnswerType.Binary),
    ClinicalQuestion("pcos_screening", "¿Cree usted que padece el Síndrome de Ovario Poliquístico o SOP? (Un trastorno hormonal común que puede causar periodos irregulares, crecimiento excesivo de vello, acné o dificultades reproductivas). ", AnswerType.Binary),
    ClinicalQuestion("pcos", "¿Ha sido diagnosticada formalmente con SOP por un médico (generalmente mediante análisis de sangre y una ecografía ginecológica)? ", AnswerType.Binary),


    // Vida Sexual y Reproductiva
    ClinicalQuestion("sexually_active", "¿Mantiene usted una vida sexual activa en la actualidad? ", AnswerType.Binary),
    ClinicalQuestion("miscarriages_abortions", "¿Ha tenido alguna vez una pérdida gestacional (aborto)? En caso de ser así, indique el número de veces según corresponda:", AnswerType.Numeric)
)