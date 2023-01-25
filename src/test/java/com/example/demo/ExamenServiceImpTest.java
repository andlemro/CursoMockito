package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lemos.app_mockito.ejemplos.modelos.Examen;
import org.lemos.app_mockito.ejemplos.repositories.ExamenRepository;
import org.lemos.app_mockito.ejemplos.repositories.PreguntaRepository;
import org.lemos.app_mockito.ejemplos.servicios.Datos;
import org.lemos.app_mockito.ejemplos.servicios.ExamenService;
import org.lemos.app_mockito.ejemplos.servicios.ExamenServiceImp;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

//Opcion 2. De esta manera se habilita la injeccion por dependencias con mockito con el @ExtendWith(MockitoExtension.class).
@ExtendWith(MockitoExtension.class)
public class ExamenServiceImpTest {
	
	@Mock
	private ExamenRepository repository;
	
	@Mock
	private PreguntaRepository preguntaRepository;
	
	@InjectMocks
	private ExamenServiceImp service;
	
	@BeforeEach
	void setUp() {
		// Opcion 1. De esta manera se habilita la injeccion por dependencias con mockito.
//		MockitoAnnotations.openMocks(this);
		
		
		// Se definen los mock para simular las instancias.
//		repository = mock(ExamenRepository.class);
//		preguntaRepository = mock(PreguntaRepository.class);
//		service = new ExamenServiceImp(repository, preguntaRepository);
	}
	
	@Test
	void findExamenPorNombre() {

		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		Optional<Examen> examen = service.findExamenPorNombre("Matematicas");
		
		assertTrue(examen.isPresent());
		assertEquals(5L, examen.orElseThrow().getId());
		assertEquals("Matematicas", examen.orElseThrow().getNombre());
	}
	
	@Test
	void findExamenPorNombreListaVacia() {
		// Con Collections.emptyList() se define una lista vacia.
		List<Examen> datos = Collections.emptyList();
		
		when(repository.findAll()).thenReturn(datos);
		Optional<Examen> examen = service.findExamenPorNombre("Matematicas");
		
		assertFalse(examen.isPresent());
	}
	
	@Test
	void testPreguntasExamen() {
		// Los metodos when son propios de Mockito y permiten simular pruebas.
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.preguntas);
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(5, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Arimetica"));
	}
	
	
	@Test
	void testPreguntasExamenVerify() {
		// Los metodos when son propios de Mockito y permiten simular pruebas.
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.preguntas);
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(5, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Arimetica"));
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
	}
	
	
	@Test
	void testNoExisteExamenVerify() {
		// Los metodos when son propios de Mockito y permiten simular pruebas.
		when(repository.findAll()).thenReturn(Collections.emptyList());
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.preguntas);
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		assertNull(examen);
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(5L);
	}
	
	@Test
	void testGuardarExamen() {
		Examen newExamen = Datos.EXAMEN;
		newExamen.setPreguntas(Datos.preguntas);
		
		when(repository.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
		Examen examen = service.guardar(newExamen);
		assertNotNull(examen.getId());
		assertEquals(8L, examen.getId());
		assertEquals("Fisica", examen.getNombre());
		
		verify(repository).guardar(any(Examen.class));
		verify(preguntaRepository).guardarVarias(anyList());
	}
	
	
}
