package org.lemos.app_mockito.ejemplos.repositories;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lemos.app_mockito.ejemplos.servicios.Datos;

public class PreguntasRepositoryImpl implements PreguntaRepository {

	@Override
	public List<String> findPreguntasPorExamenId(Long id) {
		System.out.println("PreguntasRepositoryImpl.findPreguntasPorExamenId");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Datos.PREGUNTAS;
	}

	@Override
	public void guardarVarias(List<String> preguntas) {
		System.out.println("PreguntasRepositoryImpl.guardarVarias");
	}

}
