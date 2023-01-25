package org.lemos.app_mockito.ejemplos.servicios;

import java.util.Arrays;
import java.util.List;

import org.lemos.app_mockito.ejemplos.modelos.Examen;

public class Datos {
	
	public final static List<Examen> EXAMENES = Arrays.asList(
			new Examen(5L, "Matematicas", null), 
			new Examen(6L, "Lenguaje", null),
			new Examen(7L, "Historia", null)
	);
	
	public final static List<String> preguntas = Arrays.asList(
			"Arimetica", "Integrales", "Derivadas", "Trigonometria", "Geometria"
			);
	
	public final static Examen EXAMEN = new Examen(8L, "Fisica", null);

}
