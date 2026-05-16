package com.tenko.app.data.model

enum class LaboratoryStudy(val displayName: String, val description: String) {
    COMPLETE_BLOOD_COUNT(
        "BIOMETRÍA HEMÁTICA",
        "Estudio de laboratorio que evalúa los componentes principales de la sangre, incluyendo glóbulos rojos, glóbulos blancos, hemoglobina y plaquetas. Es útil para detectar anemia, infecciones, inflamación, trastornos hematológicos y otras alteraciones generales de salud."
    ),

    BLOOD_CHEMISTRY_PANEL(
        "QUÍMICA SANGUÍNEA",
        "Conjunto de análisis que permite evaluar diferentes sustancias presentes en la sangre, como glucosa, colesterol, triglicéridos, ácido úrico, urea y creatinina. Ayuda a conocer el funcionamiento general del organismo, especialmente del hígado, riñones y metabolismo."
    ),

    URINALYSIS(
        "EXAMEN GENERAL DE ORINA",
        "Análisis utilizado para detectar infecciones urinarias, enfermedades renales, diabetes y otras alteraciones metabólicas. Evalúa características físicas, químicas y microscópicas de la orina."
    ),

    HORMONAL_PROFILE(
        "PERFIL HORMONAL",
        "Estudio enfocado en medir los niveles hormonales relacionados con el ciclo menstrual, fertilidad, ovulación y menopausia. Puede incluir hormonas como estrógeno, progesterona, FSH, LH y prolactina."
    ),

    OVARIAN_PROFILE(
        "PERFIL OVÁRICO",
        "Conjunto de estudios hormonales utilizados para evaluar la función ovárica, la reserva ovárica y posibles alteraciones relacionadas con fertilidad, síndrome de ovario poliquístico o menopausia. Puede incluir hormonas como estradiol, FSH, LH y hormona antimülleriana."
    ),

    HIRSUTISM_PROFILE(
        "PERFIL DE HIRSUTISMO",
        "Estudios hormonales enfocados en identificar alteraciones relacionadas con crecimiento excesivo de vello corporal en mujeres. Generalmente evalúa testosterona, DHEA-S, androstenediona y otras hormonas androgénicas."
    ),

    ANEMIA_PROFILE(
        "PERFIL DE ANEMIA",
        "Conjunto de pruebas destinadas a detectar y clasificar distintos tipos de anemia. Puede incluir biometría hemática, hierro sérico, ferritina, vitamina B12 y ácido fólico."
    ),

    COAGULATION_PROFILE(
        "PERFIL DE COAGULACIÓN",
        "Pruebas de laboratorio utilizadas para evaluar la capacidad de coagulación de la sangre y detectar trastornos hemorrágicos o trombóticos. Puede incluir TP, TTPa e INR."
    ),

    OSTEOPOROSIS_PROFILE(
        "PERFIL DE OSTEOPOROSIS",
        "Conjunto de estudios enfocados en evaluar la salud ósea y detectar riesgo de osteoporosis. Puede incluir niveles de calcio, vitamina D, fósforo y marcadores óseos."
    ),

    THROMBOPHILIA_PROFILE(
        "PERFIL DE TROMBOFILIA",
        "Pruebas especializadas utilizadas para detectar alteraciones que aumentan el riesgo de formación de coágulos sanguíneos. Es especialmente importante en mujeres con antecedentes de abortos recurrentes o trombosis."
    ),

    BREAST_AND_BONE_PROFILE(
        "PERFIL MAMARIO Y ÓSEO",
        "Evaluación integral orientada a la salud mamaria y ósea de la mujer, especialmente durante la menopausia. Puede incluir estudios hormonales, mastografía y densitometría ósea."
    ),

    EXTENDED_NEWBORN_SCREENING(
        "TAMIZ NEONATAL AMPLIADO",
        "Prueba realizada a recién nacidos para detectar de manera temprana enfermedades metabólicas, genéticas, endocrinas y congénitas que podrían afectar el desarrollo del bebé."
    ),

    MOLECULAR_THROMBOPHILIA_PROFILE(
        "PERFIL MOLECULAR DE TROMBOFILIA",
        "Estudio genético avanzado que identifica mutaciones relacionadas con trombofilia hereditaria, como Factor V Leiden o mutación de protrombina."
    ),

    OBSTETRIC_PROFILE(
        "PERFIL OBSTÉTRICO",
        "Conjunto de estudios clínicos y de laboratorio utilizados para monitorear el estado de salud de la mujer embarazada y detectar posibles riesgos durante la gestación."
    ),

    PAP_SMEAR(
        "PAPANICOLAOU",
        "Prueba ginecológica preventiva que analiza células del cuello uterino para detectar cambios anormales, lesiones precancerosas o signos tempranos de cáncer cervicouterino."
    ),

    HPV_TEST(
        "PRUEBA DE VPH",
        "Análisis diseñado para detectar la presencia del Virus del Papiloma Humano, especialmente los tipos de alto riesgo asociados con cáncer cervicouterino."
    ),

    MAMMOGRAPHY(
        "MASTOGRAFÍA",
        "Estudio de imagen realizado mediante rayos X que permite detectar anomalías en el tejido mamario, incluyendo tumores, quistes o signos tempranos de cáncer de mama."
    ),

    BREAST_ULTRASOUND(
        "ULTRASONIDO MAMARIO",
        "Estudio complementario a la mastografía que utiliza ondas sonoras para evaluar el tejido mamario y diferenciar entre quistes, masas sólidas u otras alteraciones."
    ),

    PELVIC_ULTRASOUND(
        "ULTRASONIDO PÉLVICO",
        "Procedimiento de imagen utilizado para evaluar órganos reproductivos femeninos como útero, ovarios y trompas de Falopio, ayudando a detectar quistes, miomas u otras alteraciones ginecológicas."
    ),

    THYROID_PROFILE(
        "PERFIL TIROIDEO",
        "Análisis que evalúa el funcionamiento de la glándula tiroides mediante la medición de hormonas como TSH, T3 y T4. Ayuda a detectar hipotiroidismo, hipertiroidismo y otros trastornos tiroideos."
    ),

    BLOOD_GLUCOSE_TEST(
        "GLUCOSA EN SANGRE",
        "Prueba utilizada para medir los niveles de glucosa en sangre y detectar condiciones como diabetes, hipoglucemia o resistencia a la insulina."
    ),

    HBA1C_TEST(
        "HEMOGLOBINA GLUCOSILADA",
        "Análisis que muestra el promedio de glucosa en sangre durante los últimos dos o tres meses, siendo una herramienta importante para el diagnóstico y control de la diabetes."
    ),

    LIPID_PROFILE(
        "PERFIL DE LÍPIDOS",
        "Estudio que mide colesterol total, colesterol HDL, colesterol LDL y triglicéridos para evaluar el riesgo de enfermedades cardiovasculares."
    ),

    PREGNANCY_TEST(
        "PRUEBA DE EMBARAZO",
        "Análisis que detecta la hormona hCG en sangre u orina para confirmar un embarazo en etapas tempranas."
    ),

    COLPOSCOPY(
        "COLPOSCOPÍA",
        "Procedimiento ginecológico que permite observar detalladamente el cuello uterino, vagina y vulva mediante un colposcopio para detectar lesiones o anomalías."
    ),

    BONE_DENSITY_TEST(
        "DENSITOMETRÍA ÓSEA",
        "Estudio que mide la densidad mineral ósea para detectar osteoporosis, osteopenia y evaluar el riesgo de fracturas."
    ),

    PRENATAL_SCREENING(
        "PERFIL PRENATAL",
        "Conjunto de análisis y evaluaciones realizados durante el embarazo para monitorear la salud de la madre y el desarrollo del bebé."
    ),

    VAGINAL_CULTURE(
        "CULTIVO VAGINAL",
        "Prueba de laboratorio utilizada para identificar infecciones vaginales causadas por bacterias, hongos u otros microorganismos."
    ),

    STI_SCREENING(
        "PRUEBAS ETS",
        "Conjunto de estudios destinados a detectar enfermedades de transmisión sexual como VIH, sífilis, clamidia, gonorrea y hepatitis."
    ),

    OBSTETRIC_ULTRASOUND(
        "ULTRASONIDO OBSTÉTRICO",
        "Estudio de imagen utilizado durante el embarazo para monitorear el crecimiento, desarrollo y bienestar del bebé."
    )
}