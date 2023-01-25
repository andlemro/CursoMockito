package org.lemos.app_mockito.ejemplos.repositories;

import java.util.List;

import org.lemos.app_mockito.ejemplos.modelos.Examen;

public interface ExamenRepository {
	
	Examen guardar(Examen examen);
	List<Examen> findAll();
	
}
