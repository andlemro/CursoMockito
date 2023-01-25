package org.lemos.app_mockito.ejemplos.servicios;

import java.util.Optional;

import org.lemos.app_mockito.ejemplos.modelos.Examen;

public interface ExamenService {

	Optional<Examen> findExamenPorNombre(String nombre);
	
	Examen findExamenPorNombreConPreguntas(String nombre);
	
	Examen guardar(Examen examen);
}
