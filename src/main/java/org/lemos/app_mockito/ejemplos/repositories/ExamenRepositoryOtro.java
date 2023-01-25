package org.lemos.app_mockito.ejemplos.repositories;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lemos.app_mockito.ejemplos.modelos.Examen;

public class ExamenRepositoryOtro implements ExamenRepository {

	@Override
	public List<Examen> findAll() {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Examen guardar(Examen examen) {
		// TODO Auto-generated method stub
		return null;
	}

}
