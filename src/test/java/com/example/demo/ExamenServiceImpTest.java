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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

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
	    // 1. Given
		// Los metodos when son propios de Mockito y permiten simular pruebas.
		when(repository.findAll()).thenReturn(Collections.emptyList());
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.preguntas);
		
		// 2. When
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		
		// 3. Then
		assertNull(examen);
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(5L);
	}
	
	@Test
	void testGuardarExamen() {
		
		/**
		 * BDD = Desarrollo impulsado por el comportamiento.
		 * 
		 */
		
		// 1. Given = Dado
		Examen newExamen = Datos.EXAMEN;
		newExamen.setPreguntas(Datos.preguntas);
		
		/**
		 * En la respuesta (.then) implementamos una respuesta (Answer) de tipo Examen
		 * para poder especificar la secuencia con la que queremos que arranque nuestro
		 * ID incremental, esto simulando con mockito. 
		 **/
		when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
			
			// Iniciara apartir de 8 ya que hay examenes creados en otras clases.
			Long secuencia = 8L;
			
			@Override
			public Examen answer(InvocationOnMock invocation) throws Throwable {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuencia++);
				return examen;
			}
		});
		
		// 2. When = Cuando
		Examen examen = service.guardar(newExamen);
		
		// 3. Then = Entonces
		assertNotNull(examen.getId());
		assertEquals(8L, examen.getId());
		assertEquals("Fisica", examen.getNombre());
		
		verify(repository).guardar(any(Examen.class));
		verify(preguntaRepository).guardarVarias(anyList());
	}
	
	
	@Test
	void testManejoException() {
		
		// Given
		when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
		when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
		
		// When
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.findExamenPorNombreConPreguntas("Matematicas");
		});
		
		// Then
		assertEquals(IllegalArgumentException.class, exception.getClass());
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(isNull());
	}
	
	@Test
	void testArgumentMatchers() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.preguntas);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		
		verify(repository).findAll();
//		verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
		verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 5L));
//		verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
		
	}
}
