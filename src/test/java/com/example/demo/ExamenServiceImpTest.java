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
import org.mockito.InOrder;
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
	private ExamenRepositoryImpl repository;
	
	@Mock
	private PreguntasRepositoryImpl preguntaRepository;
	
	@InjectMocks
	private ExamenServiceImp service;
	
	@Captor
	ArgumentCaptor<Long> captor;
	
	@BeforeEach
	void setUp() {
		// Opcion 1. De esta manera se habilita la injeccion por dependencias con mockito.
//		MockitoAnnotations.openMocks(this);
		
		
		// Se definen los mock para simular las instancias.
//		repository = mock(PreguntasRepositoryImpl.class);
//		preguntaRepository = mock(PreguntasRepositoryImpl.class);
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
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(5, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Arimetica"));
	}
	
	
	@Test
	void testPreguntasExamenVerify() {
		// Los metodos when son propios de Mockito y permiten simular pruebas.
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
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
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
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
		newExamen.setPreguntas(Datos.PREGUNTAS);
		
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
	
	
	
	/******************************************************
	 * Argument Matcher, para validar el argumento
	 ******************************************************/
	
	@Test
	void testArgumentMatchers() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		
		verify(repository).findAll();
//		verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
		verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 5L));
//		verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
		
	}
	
	
	@Test
	void testArgumentMatchers2() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
	}
	
	@Test
	void testArgumentMatchers3() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		
		verify(repository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(argThat((argument)-> argument != null && argument > 0));
	}
	
	
	public static class MiArgsMatchers implements ArgumentMatcher<Long> {
		
		private Long argument;
		
		@Override
		public boolean matches(Long argument) {
			this.argument = argument;
			return argument != null && argument > 0;
		}

		@Override
		public String toString() {
			return "Es para un mensaje personalizado de error "
					+ "que imprime mockito  en caso de que falle el test"
					+ argument + " debe ser un entero positivo.";
		}	
	}
	
	/******************************************************/
	
	/******************************************************
	 * Argument Captor, para capturar el argumento
	 ******************************************************/
	
	@Test
	void testArgumentCaptor() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
//		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		service.findExamenPorNombreConPreguntas("Matematicas");
		
		// Con la clase ArgumentCaptor podemos capturar el agumento que esta pasando, de manera especifica.
//		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());
		
		assertEquals(5L, captor.getValue()); 
	}
	
	/******************************************************
	 * Metodos doThrow, doAnswer
	 ******************************************************/
	
	@Test
	void testDoThrow() {
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS);
		
		/**
		 * Con la anotacion doThrow() podemos ejecutar un metodo que no retorna nada (void)
		 * pero debemos tener en cuenta qu el orden cambia, luego de ejecutar el doThrwo
		 * se ejecuta el metodo when().
		 */
		doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());
	
		assertThrows(IllegalArgumentException.class, () -> {
			service.guardar(examen);
		});
	}
	
	@Test
	void testDoAnswer() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
//		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			return id == 5 ? Datos.PREGUNTAS : Collections.emptyList();
		}).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		
		assertEquals(5L, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Geometria"));
		assertEquals(5L, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		
		verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
	}
	
	
	
	@Test
	void testDoAnswerGuardarExamen() {
		
		// 1. Given = Dado
		Examen newExamen = Datos.EXAMEN;
		newExamen.setPreguntas(Datos.PREGUNTAS);
		
		doAnswer(new Answer<Examen>() {
			
			// Iniciara apartir de 8 ya que hay examenes creados en otras clases.
			Long secuencia = 8L;
			
			@Override
			public Examen answer(InvocationOnMock invocation) throws Throwable {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuencia++);
				return examen;
			}
		}).when(repository).guardar(any(Examen.class));
		
		// 2. When = Cuando
		Examen examen = service.guardar(newExamen);
		
		// 3. Then = Entonces
		assertNotNull(examen.getId());
		assertEquals(8L, examen.getId());
		assertEquals("Fisica", examen.getNombre());
		
		verify(repository).guardar(any(Examen.class));
		verify(preguntaRepository).guardarVarias(anyList());
	}
	
	/******************************************************
	 * DoCallRealMethod, llamada real al metodo
	 ******************************************************/
	@Test
	void testDoCallRealMethod() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
//		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
		
		doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
		
		Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(5L, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
	}
	
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
		ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
		PreguntaRepository preguntaRepository = spy(PreguntasRepositoryImpl.class);
		ExamenService examenService = new ExamenServiceImp(examenRepository, preguntaRepository);
		
		List<String> preguntas = Arrays.asList("Aritmetica");
		
		/***
		 * Al utilizar los espias y al tratar de simular una prueba, se recomienda utilizar
		 * los doXXX ya que con el metodo when se presenta un bug.
		 */
//		when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
		doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

		Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

		assertEquals(5, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		assertEquals(1, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Aritmetica"));
		
		verify(examenRepository).findAll();
		verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
	}

	
	@Test
	void testOrdenInvocaciones() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		service.findExamenPorNombreConPreguntas("Lenguaje");
		
		/***
		 * Mediante la clase y metodo inOrder, podemos validar el orden de ejecucion
		 * de la prueba.
		 */
		InOrder inOrder = inOrder(preguntaRepository);
		inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
		inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
	}
	
	
	@Test
	void testOrdenInvocaciones2() {
		when(repository.findAll()).thenReturn(Datos.EXAMENES);
		
		service.findExamenPorNombreConPreguntas("Matematicas");
		service.findExamenPorNombreConPreguntas("Lenguaje");
		
		/***
		 * Mediante la clase y metodo inOrder, podemos validar el orden de ejecucion
		 * de la prueba.
		 */
		InOrder inOrder = inOrder(repository, preguntaRepository);
		// Estas dos lineas corresponde al llamado de Matematicas.
		inOrder.verify(repository).findAll();
		inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
		
		// Estas dos lineas corresponde al llamado de Lenguaje.
		inOrder.verify(repository).findAll();
		inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
	}
	
}
