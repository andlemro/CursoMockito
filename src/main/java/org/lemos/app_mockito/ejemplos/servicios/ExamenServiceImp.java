package org.lemos.app_mockito.ejemplos.servicios;

import java.util.List;
import java.util.Optional;

import org.lemos.app_mockito.ejemplos.modelos.Examen;
import org.lemos.app_mockito.ejemplos.repositories.ExamenRepository;
import org.lemos.app_mockito.ejemplos.repositories.PreguntaRepository;

public class ExamenServiceImp implements ExamenService{
	
	private ExamenRepository examenRepository;
	private PreguntaRepository preguntaRepository;

	public ExamenServiceImp(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
		super();
		this.examenRepository = examenRepository;
		this.preguntaRepository = preguntaRepository;
	}

	@Override
	public Optional<Examen> findExamenPorNombre(String nombre) {
		return examenRepository.findAll()
				.stream()
				.filter(e -> e.getNombre().contains(nombre))
				.findFirst();
	}

	@Override
	public Examen findExamenPorNombreConPreguntas(String nombre) {
		Optional<Examen> examenOptional = findExamenPorNombre(nombre);
		Examen examen = null;
		if (examenOptional.isPresent()) {
			examen = examenOptional.orElseThrow();
			List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
			examen.setPreguntas(preguntas);
		}
		return examen;
	}

	@Override
	public Examen guardar(Examen examen) {
		if(!examen.getPreguntas().isEmpty()) {
			this.preguntaRepository.guardarVarias(examen.getPreguntas());
		}
		return examenRepository.guardar(examen);
	}

	
}
