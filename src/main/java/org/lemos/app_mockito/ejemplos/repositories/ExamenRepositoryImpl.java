package org.lemos.app_mockito.ejemplos.repositories;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lemos.app_mockito.ejemplos.modelos.Examen;
import org.lemos.app_mockito.ejemplos.servicios.Datos;

public class ExamenRepositoryImpl implements ExamenRepository {

	@Override
	public List<Examen> findAll() {
		System.out.println("ExamenRepositoryImpl.findAll");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Datos.EXAMENES;
	}

	@Override
	public Examen guardar(Examen examen) {
		System.out.println("ExamenRepositoryImpl.guardar");
		return Datos.EXAMEN;
	}

}
