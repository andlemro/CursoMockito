package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.aopalliance.intercept.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lemos.app_mockito.ejemplos.modelos.Examen;
import org.lemos.app_mockito.ejemplos.repositories.ExamenRepository;
import org.lemos.app_mockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.lemos.app_mockito.ejemplos.repositories.PreguntaRepository;
import org.lemos.app_mockito.ejemplos.repositories.PreguntasRepositoryImpl;
import org.lemos.app_mockito.ejemplos.servicios.Datos;
import org.lemos.app_mockito.ejemplos.servicios.ExamenService;
import org.lemos.app_mockito.ejemplos.servicios.ExamenServiceImp;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamenServiceImpSpyTest {
	
	/***
	 * Esta seria una clase de pruebas con anotaciones Spy.
	 */
	
	@Spy
	private ExamenRepositoryImpl repository;
	
	@Spy
	private PreguntasRepositoryImpl preguntaRepository;
	
	@InjectMocks
	private ExamenServiceImp service;

	
	/******************************************************
	 * Espias, Spy
	 ******************************************************/
	@Test
	void testSpy() {
		/**
		 * Para los espias, es necesario utilizar la clase con implementacion,
		 * ya que si utilizamos la interfaz que utiliza metodos astractos nos 
		 * arrojara un error ya que la clase no tiene una implementacion.
		 */
		
		List<String> preguntas = Arrays.asList("Aritmetica");
		
		/***
		 * Al utilizar los espias y al tratar de simular una prueba, se recomienda utilizar
		 * los doXXX ya que con el metodo when se presenta un bug.
		 */
//		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
		doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

		assertEquals(5, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		assertEquals(1, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Aritmetica"));
		
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
	}
	
}
